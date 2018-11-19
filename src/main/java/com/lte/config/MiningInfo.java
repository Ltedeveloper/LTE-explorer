package com.lte.config;

import java.math.BigDecimal;

/**
 * Created by mayakui on 2018/1/18 0018.
 */
public class MiningInfo {

    private Long blocks;

    //难度
    private String difficulty;

    //全网算力
    private BigDecimal networkhashps;

    private Integer pooledtx;

    public Long getBlocks() {
        return blocks;
    }

    public void setBlocks(Long blocks) {
        this.blocks = blocks;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public BigDecimal getNetworkhashps() {
        return networkhashps;
    }

    public void setNetworkhashps(BigDecimal networkhashps) {
        this.networkhashps = networkhashps;
    }

    public Integer getPooledtx() {
        return pooledtx;
    }

    public void setPooledtx(Integer pooledtx) {
        this.pooledtx = pooledtx;
    }
}
