package com.qiuzhitech.onlineshopping_07.controller;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UnitTestController {
    @Resource
    FakeDependency fk;
    public  UnitTestController(FakeDependency fk) {
        this.fk = fk;
    }

    public int add5(int a, int b) {
        return fk.add(a,b) + 5;
    }
}
