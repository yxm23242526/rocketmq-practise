package com.example.tomcatqps.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class QpsTestController {


    @GetMapping("test")
    public String qpsTest(){
        return "ok";
    }


    @GetMapping("test2")
    public String qpsTest2(){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "ok";
    }
}
