package com.qiuzhitech.onlineshopping_07.db.dao.impl;

import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {

    @Resource
    OnlineShoppingCommodityMapper onlineShoppingCommodityMapper;
    @Override
    public int insertCommodity(OnlineShoppingCommodity commodity) {
        return onlineShoppingCommodityMapper.insert(commodity);
    }

    @Override
    public OnlineShoppingCommodity selectCommodityById(long commodityId) {
        return onlineShoppingCommodityMapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return onlineShoppingCommodityMapper.listCommodities();
    }

    @Override
    public List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId) {
        return onlineShoppingCommodityMapper.listCommoditiesByUserId(userId);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity commodity) {
        return onlineShoppingCommodityMapper.updateByPrimaryKeySelective(commodity);
    }

    @Override
    public int deductStockWithCommodityId(long commodityId) {
        return onlineShoppingCommodityMapper.deductStockWithCommodityId(commodityId);
    }

    @Override
    public int revertStockWithCommodityId(long commodityId) {
        return onlineShoppingCommodityMapper.revertStockWithCommodityId(commodityId) ;
    }


}
