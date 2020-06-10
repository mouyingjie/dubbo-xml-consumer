package com.demo.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.demo.dubbo.service.TestService;
import com.demo.dubbo.service.impl.AnnotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chenxyz on 2020/6/9.
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    TestService testService;

    @RequestMapping("/test")
    public String test(String name){
       return testService.test(name);
    }

    @Reference
    AnnotionService annotionService;

    @RequestMapping("/anno")
    public String anno(String param){
        return annotionService.anno(param);
    }
}
