package com.qiuzhitech.onlineshopping_07.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserDemo {
    Integer id;
    String name;
    String email;

}
