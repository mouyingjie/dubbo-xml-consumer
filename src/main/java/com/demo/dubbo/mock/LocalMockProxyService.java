package com.demo.dubbo.mock;

import com.demo.dubbo.service.mock.MockService;

/**
 * Created by chenxyz on 2020/6/11.
 */
public class LocalMockProxyService implements MockService{
    @Override
    public String mock(String s) {
        System.out.println("local mock  做一下容错处理，这个就是服务降级");
        return "local mock  做一下容错处理，这个就是服务降级";
    }
}
