package com.qiuzhitech.onlineshopping_07.db.dao;

import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;

import java.util.List;

/**
 *  command + shift + t to create unit test
 *  option + enter to Select "Implement method
 */
public interface OnlineShoppingCommodityDao {


    int insertCommodity(OnlineShoppingCommodity commodity);

    OnlineShoppingCommodity selectCommodityById(long commodityId);
    List<OnlineShoppingCommodity> listCommodities();

    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);

    int updateCommodity(OnlineShoppingCommodity commodity);


    int deductStockWithCommodityId(long commodityId);

    int revertStockWithCommodityId(long commodityId);
}
