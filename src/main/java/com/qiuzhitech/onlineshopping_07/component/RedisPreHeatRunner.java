package com.qiuzhitech.onlineshopping_07.component;


import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_07.service.EsService;
import com.qiuzhitech.onlineshopping_07.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
//make sure it auto run when ide starts
@Slf4j

public class RedisPreHeatRunner implements ApplicationRunner {

        // read data from mysql
        // write it to redis
        @Resource
        private RedisService redisService;

        @Resource
        OnlineShoppingCommodityDao onlineShoppingCommodityDao;
        @Resource
        EsService esService;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            List<OnlineShoppingCommodity> onlineShoppingCommodities =
                    onlineShoppingCommodityDao.listCommodities();
            for  (OnlineShoppingCommodity commodity : onlineShoppingCommodities) {
                String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodity.getCommodityId();
                redisService.setValue(redisKey, commodity.getAvailableStock().toString());
                esService.addCommodity(commodity);
                log.info("preHeat Staring: Initialize commodity :" + commodity.getCommodityId());
            }

    }
}
