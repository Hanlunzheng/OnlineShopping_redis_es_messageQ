package com.qiuzhitech.onlineshopping_07.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_07.service.mq.RocketMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class OrderService {

    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private RedisService redisService;
    @Resource
    private RocketMQService rocketMQService;

    public OnlineShoppingOrder placeOrderOriginal(long commodityId, long userId) {
        OnlineShoppingCommodity onlineShoppingCommodity =
                onlineShoppingCommodityDao.selectCommodityById(commodityId);
        int availableStock = onlineShoppingCommodity.getAvailableStock();
        int lockStock = onlineShoppingCommodity.getLockStock();
        if (availableStock > 0 ) {
            availableStock--;
            lockStock++;
            onlineShoppingCommodity.setAvailableStock(availableStock);
            onlineShoppingCommodity.setLockStock(lockStock);
            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
            OnlineShoppingOrder order = createOrder(commodityId, userId, onlineShoppingCommodity.getPrice());
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully, current availableStock:" +  availableStock);
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + onlineShoppingCommodity.getCommodityId());
            return null;
        }
    }

//    //this is the standard way to handle business logic
//    public OnlineShoppingOrder placeOrder(long userId, long commodityId) {
//
//        //select the commodity by commodityId
//        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectCommodityById(commodityId);
//        //check if the available stock > 0
//
//        if (onlineShoppingCommodity == null) {
//            return null; // or throw a custom exception if needed
//        }
//        if (onlineShoppingCommodity.getAvailableStock() > 0) {
//            OnlineShoppingOrder onlineShoppingOrder = OnlineShoppingOrder.builder().userId(userId).commodityId(commodityId).orderNo(UUID.randomUUID().toString()).orderStatus(1).orderAmount(1L).createTime(new Date()).build();
//            //create an order record
//            onlineShoppingOrderDao.insertOrder(onlineShoppingOrder);
//            //commodity stock update -1
//
//            //only modifies the object in memory (i.e., in the JVM heap).
//            onlineShoppingCommodity.setAvailableStock(onlineShoppingCommodity.getAvailableStock() - 1);
//            //we have to manually update in database
//            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
//
//            return onlineShoppingOrder;
//
//        } else {
//            return null;
//        }
//
//    }
    public OnlineShoppingOrder placeOrderOneSQL(long commodityId, long userId) {
        int result = onlineShoppingCommodityDao.deductStockWithCommodityId(commodityId);
        if (result > 0 ) {
            OnlineShoppingOrder order = createOrder(commodityId, userId, 1);
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully");
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderRedis(long commodityId, long userId) {
        String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodityId;
        long result = redisService.deductStockWithCommodityId(redisKey);
        if (result >= 0 ) {
            OnlineShoppingOrder order = placeOrderOriginal(commodityId, userId);
//            OnlineShoppingOrder order = createOrder(commodityId, userId, 1);
//            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully");
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    /**
     * this method use messagequeue
     * @param commodityId
     * @param userId
     * @return
     */
    public OnlineShoppingOrder placeOrderFinal(long commodityId, long userId) {
        if (redisService.isInDenyList(String.valueOf(userId),
                String.valueOf(commodityId))) {
            log.info("Each user have only one quote for this commodity {}", commodityId);
            return null;
        }
        String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodityId;
        long result = redisService.deductStockWithCommodityId(redisKey);
        if (result >= 0 ) {
            OnlineShoppingOrder order = createOrder(commodityId, userId, 1);

            //name message queue "createTopic" that handle case that write into database
            rocketMQService.sendFIFOMessage("createOrder", JSON.toJSONString(order));

            log.info("Place order successfully");
            redisService.addToDenyList(String.valueOf(userId),
                    String.valueOf(commodityId));
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        OnlineShoppingOrder onlineShoppingOrder =
                onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        return onlineShoppingOrder;

    }
    public int payOrder(String orderNum){
        OnlineShoppingOrder onlineShoppingOrder = onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        onlineShoppingOrder.setOrderStatus(2);
        onlineShoppingOrder.setPayTime(new Date());

        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectCommodityById(onlineShoppingOrder.getCommodityId());
        onlineShoppingCommodity.setLockStock(onlineShoppingCommodity.getLockStock() - 1);
        onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
        return onlineShoppingOrderDao.updateOrder(onlineShoppingOrder);


    }
    private  OnlineShoppingOrder createOrder(long commodityId, long userId, long orderAmount) {
        OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                .commodityId(commodityId)
                .orderNo(UUID.randomUUID().toString())
                .orderStatus(1)
                // 0: invalid order
                // 1. pending payment
                // 2. finish payment
                // 99. overtime order
                .orderAmount(orderAmount)
                .createTime(new Date())
                .userId(userId)
                .build();
        return order;
    }
}



