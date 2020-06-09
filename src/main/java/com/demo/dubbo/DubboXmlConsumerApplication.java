package com.demo.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:dubbo-consumer.xml"})
public class DubboXmlConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboXmlConsumerApplication.class, args);
	}

}
