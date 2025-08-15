//package com.qiuzhitech.onlineshopping_07.db.dao;
//
//import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@Slf4j
//@SpringBootTest
//class OnlineShoppingCommodityDaoTest {
//
//    @Resource
//    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
//    @BeforeEach
//    void setUp() {
//    }
//
//    @Test
//    void insertCommodity() {
//
//        OnlineShoppingCommodity onlineShoppingCommodity = OnlineShoppingCommodity.builder().commodityName("NAME test2").commodityDesc("TEST2").availableStock(112).totalStock(112).price(111).lockStock(0).creatorUserId(123L).build();
//
//        onlineShoppingCommodityDao.insertCommodity(onlineShoppingCommodity);
//    }
//
//    @Test
//    void selectCommodityById() {
//        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectCommodityById(1002);
//
//        log.info("COMMODITY 1002 IS" + onlineShoppingCommodity);
//    }
//
//    @Test
//    void listCommodities() {
//        List<OnlineShoppingCommodity> onlineShoppingCommodity = onlineShoppingCommodityDao.listCommodities();
//        log.info("return all the list" + onlineShoppingCommodity);
//    }
//
//    @Test
//    void listCommoditiesByUserId() {
//        List<OnlineShoppingCommodity> onlineShoppingCommodity = onlineShoppingCommodityDao.listCommoditiesByUserId(123L);
//        log.info("return list by userid = 123" + onlineShoppingCommodity);
//    }
//}