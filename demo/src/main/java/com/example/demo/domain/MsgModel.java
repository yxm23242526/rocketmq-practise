package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MsgModel {

    private String orderId;

    private Integer userId;

    private String desc;
}
