package com.qiuzhitech.onlineshopping_07.db.mappers;

import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    List<OnlineShoppingCommodity> listCommodities();

    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);

    int deductStockWithCommodityId(long commodityId);

    int revertStockWithCommodityId(long commodityId);
}