package com.lte.config;

import com.lte.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mayakui on 2018/1/15 0015.
 */
@Component
public class SystemInit implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(SystemInit.class);

    @Autowired
    private StaticService staticService;

    @Override
    public void afterPropertiesSet(){
        logger.info("开始初始化");

        staticService.setBlockRealData();
        staticService.setTransactionRealData();
        staticService.setMiningInfo();
        logger.info("初始化结束");
    }
}
