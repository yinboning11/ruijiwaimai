package com.boning.ruijiwaimai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
public class RuijiwaimaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuijiwaimaiApplication.class, args);
        log.info("项目启动成功。。。");
    }

}
