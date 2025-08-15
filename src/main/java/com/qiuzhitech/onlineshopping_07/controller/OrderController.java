package com.qiuzhitech.onlineshopping_07.controller;


import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_07.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_07.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {


    @Resource
    OrderService orderService;
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @GetMapping("/commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(@PathVariable("userId") Long userId, @PathVariable("commodityId") Long commodityId, Map<String, Object> map) {
//
//        OnlineShoppingOrder order = orderService.placeOrder(userId,commodityId);

    //    OnlineShoppingOrder order = orderService.placeOrderOneSQL(commodityId,userId);
//        OnlineShoppingOrder order = orderService.placeOrderRedis(commodityId, userId);
     //   OnlineShoppingOrder order = orderService.placeOrderRedis(commodityId, userId);
          OnlineShoppingOrder order = orderService.placeOrderFinal( commodityId,userId);
        if (order != null) {
            map.put("resultInfo", "success");
            map.put("orderNo", order.getOrderNo());
        }else{
            map.put("resultInfo", "fail");
            map.put("orderNo", "");
        }


        return "order_result";


    }

    @GetMapping("commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> resultMap) {
        OnlineShoppingOrder order = orderService.queryOrderByOrderNum(orderNum);

        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectCommodityById(order.getCommodityId());


        resultMap.put("order",order);
        resultMap.put("commodity",onlineShoppingCommodity);
        return "order_check";
    }

    @RequestMapping("commodity/payOrder/{orderNum}")
    public String payOrder(@PathVariable("orderNum") String orderNum,
                           Map<String, Object> resultMap) {

        orderService.payOrder(orderNum);
        return orderQuery(orderNum, resultMap);
    }




}
