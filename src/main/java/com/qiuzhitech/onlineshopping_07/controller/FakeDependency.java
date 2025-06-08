package com.qiuzhitech.onlineshopping_07.controller;

import org.springframework.stereotype.Service;

@Service
public class FakeDependency {
    public int add(int a, int b) { return (a + b); }
}
