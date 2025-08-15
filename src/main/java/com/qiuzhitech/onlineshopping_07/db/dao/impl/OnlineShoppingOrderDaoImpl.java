package com.qiuzhitech.onlineshopping_07.db.dao.impl;

import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_07.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {

    @Resource
    private OnlineShoppingOrderMapper onlineShoppingOrderMapper;

    @Override
    public int insertOrder(OnlineShoppingOrder order) {
        return onlineShoppingOrderMapper.insert(order);
    }

    @Override
    public OnlineShoppingOrder selectOrderByOrderNum(String orderNum) {
        return onlineShoppingOrderMapper.selectByOrderNum(orderNum);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        return onlineShoppingOrderMapper.queryOrderByOrderNum(orderNum);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder onlineShoppingOrder) {
        return onlineShoppingOrderMapper.updateByPrimaryKeySelective(onlineShoppingOrder);
    }
}
