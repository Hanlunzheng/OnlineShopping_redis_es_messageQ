package com.qiuzhitech.onlineshopping_07.service;

import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class SearchService {
    @Resource
    EsService esService;
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

//    public List<OnlineShoppingCommodity> searchCommodityWithMySQL(String keyword) {
//        return  onlineShoppingCommodityDao.searchCommodityByKeyword(keyword);
//    }
public List<OnlineShoppingCommodity> searchCommodityWithEs(String keyword) throws IOException {
    return  esService.searchCommodity(keyword, 0 , 100);
}
}
