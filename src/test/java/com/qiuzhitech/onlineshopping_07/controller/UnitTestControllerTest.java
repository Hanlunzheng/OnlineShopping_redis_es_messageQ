//package com.qiuzhitech.onlineshopping_07.controller;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//import javax.annotation.Resource;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class UnitTestControllerTest {
//    @Resource
//    UnitTestController ut;
//
//    @Mock
//    FakeDependency fk;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void add5() {
//        ut = new UnitTestController(fk);
//        when(fk.add(anyInt(),anyInt())).thenReturn(100);
//
//        int res = ut.add5(3,4);
//        assertEquals(105, res );
//    }
//}