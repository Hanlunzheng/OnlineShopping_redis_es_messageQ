package com.qiuzhitech.onlineshopping_07.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_07.service.EsService;
import com.qiuzhitech.onlineshopping_07.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
public class CommodityController {

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    EsService esService;
    @Resource
    SearchService searchService;

    @RequestMapping("/addItem")
    public String addCommodity() {
        return "add_commodity";
    }


    @PostMapping("commodities")
    public String handleAddCommodity(@RequestParam("commodityId") long commodityId,
                                     @RequestParam("commodityName") String commodityName,
                                     @RequestParam("commodityDesc") String commodityDesc,
                                     @RequestParam("price") int price,
                                     @RequestParam("availableStock") int availableStock,
                                     @RequestParam("creatorUserId") long creatorUserId,
                                     Map<String, Object> resultMap
    ) throws IOException {
        OnlineShoppingCommodity onlineShoppingCommodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .creatorUserId(creatorUserId)
                .totalStock(availableStock)
                .lockStock(0)
                .build();
        onlineShoppingCommodityDao.insertCommodity(onlineShoppingCommodity);
        esService.addCommodity(onlineShoppingCommodity);
        resultMap.put("Item", onlineShoppingCommodity);
        return "add_commodity_success";
    }

    @GetMapping("/commodities/{sellerId}")
    public String getCommoditiesByUserId(@PathVariable("sellerId") Long sellerId, Map<String, Object> map) {

        try (Entry entry = SphU.entry("listItemsRule", EntryType.IN, 1, sellerId)) {
            List<OnlineShoppingCommodity> onlineShoppingCommodity = onlineShoppingCommodityDao.listCommoditiesByUserId(sellerId);
            map.put("itemList",onlineShoppingCommodity);
            return "list_items";
        } catch (BlockException e) {
            log.error("ListItems got throttled" + e.toString());
            return "wait";
        }
    }
    @GetMapping({"commodities","/"})
    public String listCommodities(Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities =
                onlineShoppingCommodityDao.listCommodities();
        resultMap.put("itemList", onlineShoppingCommodities);
        return "list_items";
    }


    @GetMapping({"/getCommodities/{commodityId}"})
    public String getCommoditiesByCommodityId(@PathVariable Long commodityId ,Map<String, Object> resultMap) {
        OnlineShoppingCommodity onlineShoppingCommodities =
                onlineShoppingCommodityDao.selectCommodityById(commodityId);
        resultMap.put("commodity", onlineShoppingCommodities);
        return "item_detail";
    }
    @GetMapping("/searchAction")
    public String searchAction(@RequestParam("keyWord") String keyword, Map<String, Object> resultMap) throws IOException {
        List<OnlineShoppingCommodity> commodityResult = searchService.searchCommodityWithEs(keyword);
        resultMap.put("itemList", commodityResult);
        return "search_items";
    }
    @PostConstruct
    public void CommodityControllerFlow() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("listItemsRule");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //if qps is greater than one,
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
