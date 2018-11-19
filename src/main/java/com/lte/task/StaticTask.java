package com.lte.task;

import com.lte.service.BalanceService;
import com.lte.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by mayakui on 2018/1/15 0015.
 * 计算定时统计数据，页面显示
 */
@Component
public class StaticTask {
    private static Logger logger = LoggerFactory.getLogger(StaticTask.class);
    @Autowired
    private StaticService staticService;

    @Scheduled(cron="0/10 * * * * ? ")
    public void staticBlockData(){
        staticService.setBlockRealData();
        staticService.setMiningInfo();
    }

    @Scheduled(cron="0/10 * * * * ? ")
    public void staticTransactionRealData(){
        staticService.setTransactionRealData();
    }

    /**
     * 定时统计地址余额
     */
    @Scheduled(cron="0/5 * * * * ? ")
    public void calcAddressBalance(){
        staticService.calcAddressBalance();
    }
}
