package com.qiuzhitech.onlineshopping_07.db.mappers;

import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OnlineShoppingOrder record);

    int insertSelective(OnlineShoppingOrder record);

    OnlineShoppingOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(OnlineShoppingOrder record);

    int updateByPrimaryKey(OnlineShoppingOrder record);

    OnlineShoppingOrder selectByOrderNum(String orderNum);

    OnlineShoppingOrder queryOrderByOrderNum(String orderNum);
}