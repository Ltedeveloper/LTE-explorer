package com.lte.dao.entity;

import java.math.BigDecimal;

public class TrxVin {
    private Integer id;

    private Long blockNum;

    private String trxId;

    private String address;

    private BigDecimal amount;

    private String sourceTrxId;

    private Integer vout;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
        this.blockNum = blockNum;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId == null ? null : trxId.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSourceTrxId() {
        return sourceTrxId;
    }

    public void setSourceTrxId(String sourceTrxId) {
        this.sourceTrxId = sourceTrxId == null ? null : sourceTrxId.trim();
    }

    public Integer getVout() {
        return vout;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }
}