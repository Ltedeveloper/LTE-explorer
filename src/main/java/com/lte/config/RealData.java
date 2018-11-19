package com.lte.config;

import org.springframework.stereotype.Component;

/**
 * Created by mayakui on 2018/1/15 0015.
 * 实时统计数据缓存
 */
@Component
public class RealData {

    private String blockInfo;

    private String transactionInfo;

    //最新块高度
    private Long blockNum;

    //未确认交易数
    private Integer unconfirms;

    //全网信息
    private MiningInfo miningInfo;

    public String getBlockInfo() {
        return blockInfo;
    }

    public void setBlockInfo(String blockInfo) {
        this.blockInfo = blockInfo;
    }

    public String getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(String transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
        this.blockNum = blockNum;
    }

    public Integer getUnconfirms() {
        return unconfirms;
    }

    public void setUnconfirms(Integer unconfirms) {
        this.unconfirms = unconfirms;
    }

    public MiningInfo getMiningInfo() {
        return miningInfo;
    }

    public void setMiningInfo(MiningInfo miningInfo) {
        this.miningInfo = miningInfo;
    }
}
