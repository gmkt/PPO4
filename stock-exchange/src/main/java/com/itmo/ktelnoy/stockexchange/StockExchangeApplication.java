package com.itmo.ktelnoy.stockexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
@EnableSwagger2
public class StockExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockExchangeApplication.class, args);
    }
}
