package com.demo.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.demo.dubbo.service.AsyncService;
import com.demo.dubbo.service.CacheService;
import com.demo.dubbo.service.TestService;
import com.demo.dubbo.service.callback.CallbackListener;
import com.demo.dubbo.service.callback.CallbackService;
import com.demo.dubbo.service.impl.AnnotionService;
import com.demo.dubbo.service.mock.MockService;
import com.demo.dubbo.service.stub.StubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by chenxyz on 2020/6/9.
 */
@RestController
@RequestMapping("/index")
public class IndexController implements ApplicationContextAware{

    Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    TestService testService;

    @RequestMapping("/test")
    public String test(String name){
       return testService.test(name);
    }

//    @Reference(version = "*")
//    AnnotionService annotionService;
//
//    @RequestMapping("/anno")
//    public String anno(String param){
//        return annotionService.anno(param);
//    }


    @Reference(version = "1.0")
    AnnotionService annotionServiceVersion;

    @RequestMapping("/version1")
    public String annoversion(String param){
        return annotionServiceVersion.anno(param);
    }

    @Reference(version = "2.0")
    AnnotionService annotionServiceVersion2;

    @RequestMapping("/version2")
    public String annoversion2(String param){
        return annotionServiceVersion2.anno(param);
    }

    @Reference(group = "group1")
    AnnotionService group1;

    @RequestMapping("/group1")
    public String group1(String param){
        return group1.anno(param);
    }

    @Reference(group = "group2")
    AnnotionService group2;
    @RequestMapping("/group2")
    public String group2(String param){
        return group2.anno(param);
    }


    @Reference(cache = "lru")
    CacheService cacheService;
    @RequestMapping("/cache")
    public String cache(String param){

        String fix=null;
        // 测试缓存生效，多次调用返回同样的结果。(服务器端自增长返回值)
        for (int i = 0; i <5 ; i++) {
            String result=cacheService.findCode(param);
            if (fix==null || fix.equals(result)){
                System.out.println("ok "+result);
            }else {
                System.err.println("error "+result);
            }
            fix = result;
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        // LRU的缺省cache.size为1000，执行1001次，应有溢出
        for (int n = 0; n < 1001; n++) {
            String pre = null;
            for (int i = 0; i < 10; i++) {
                String result = cacheService.findCode(String.valueOf(n));
                if (pre != null && !pre.equals(result)) {
                    System.err.println("ERROR: " + result);
                }
                pre = result;
            }
        }


        // 测试LRU有移除最开始的一个缓存项
        String result = cacheService.findCode(param);
        if (fix != null && !fix.equals(result)) {
            System.out.println("OK: " + result);
        }
        else {
            System.err.println("ERROR: " + result);
        }
        return "OK";
    }


    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/generic")
    public String generic(String param){
        //<dubbo:reference interface=""  id="genericTest" generic="true"/> 。getBean的id必须为此处配置的reference的id
        GenericService genericService=(GenericService) applicationContext.getBean("genericTest");
        return  (String) genericService.$invoke("test",new String[]{"java.lang.String"},new String[]{param});
    }


    @Reference
    GenericService genericService;

    @RequestMapping("/localgeneric")
    public String localgeneric(String param){
        return  (String) genericService.$invoke("test",new String[]{"java.lang.String"},new String[]{param});
    }

    @Reference(async = true,timeout = 1200000)
    AsyncService asyncService;
    @RequestMapping("/async")
    public String async(String param){
        String result= asyncService.async(param);

        Future<String> foofuture= RpcContext.getContext().getFuture();
        try {
            result=foofuture.get();
        }catch (InterruptedException e){
            logger.debug(e.getMessage());
        }catch (ExecutionException e){
            logger.debug(e.getMessage());
        }
        return  result;
    }


    @Reference
    CallbackService callbackService;
    @RequestMapping("/callback")
    public String callback(String param){
        callbackService.addListener("foo.bar", new CallbackListener() {
            @Override
            public void change(String s) {
                System.out.println("callback1:" +s);
            }
        });

        return "ok";
    }

    //@Reference(stub = "com.demo.dubbo.stub.LocalStubServiceProxy")
    @Autowired
    StubService stubService;
    @RequestMapping("/stub")
    public String stub(String param){
        return stubService.stub(param);
    }

    //@Reference(mock = "com.demo.dubbo.mock.LocalMockProxyService")
    @Autowired
    MockService mockService;
    @RequestMapping("/mock")
    public String mock(String param){
        return mockService.mock(param);
    }
}
