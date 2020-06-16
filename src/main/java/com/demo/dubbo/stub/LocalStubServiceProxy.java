package com.demo.dubbo.stub;

import com.demo.dubbo.service.stub.StubService;

/**
 * Created by chenxyz on 2020/6/11.
 */
public class LocalStubServiceProxy implements StubService {

    private StubService stubService;

    public LocalStubServiceProxy(StubService stubService) {
        this.stubService = stubService;
    }

    @Override
    public String stub(String s) {
        System.out.println("我是localstub，我就相当于一个过滤器，在代理调用远程方法的时候，我先做一下拦截工作");

        try {
            return stubService.stub(s);
        }
        catch (Exception e) {
            System.out.println("远程调用出现异常了，我是本地stub，我要对远程服务进行伪装，达到服务降级的目的");
            return "远程调用出现异常了，我是本地stub，我要对远程服务进行伪装，达到服务降级的目的";
        }
    }
}
