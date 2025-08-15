package com.qiuzhitech.onlineshopping_07.db.dao;

import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderDao {

     int insertOrder(OnlineShoppingOrder order);

     OnlineShoppingOrder selectOrderByOrderNum(String orderNum);

     OnlineShoppingOrder queryOrderByOrderNum(String orderNum);

     int updateOrder(OnlineShoppingOrder onlineShoppingOrder);


}
