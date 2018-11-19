package com.lte.dao.entity;

import java.math.BigDecimal;
import java.util.List;

public class BlockTransaction {
    private String trxId;

    private Long blockNum;

    private String fromAddress;

    private String toAddress;

    private String hash;

    private String blockHash;

    private BigDecimal fee;

    private BigDecimal amount;

    private String memo;

    private Integer trxType;

    private String trxTime;

    private String createdAt;

    private String coinbase;

    //查询条件
    private Integer start;
    private Integer end;
    //交易id列表
    List<String> trxIdList;

    //查询返回结果
    List<TrxVin> vinList;
    List<TrxVout> voutList;
    //页面显示确认数
    private Long confirms;

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId == null ? null : trxId.trim();
    }

    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
        this.blockNum = blockNum;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress == null ? null : fromAddress.trim();
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress == null ? null : toAddress.trim();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash == null ? null : blockHash.trim();
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Integer getTrxType() {
        return trxType;
    }

    public void setTrxType(Integer trxType) {
        this.trxType = trxType;
    }

    public String getTrxTime() {
        return trxTime;
    }

    public void setTrxTime(String trxTime) {
        this.trxTime = trxTime == null ? null : trxTime.trim();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt == null ? null : createdAt.trim();
    }

    public List<TrxVin> getVinList() {
        return vinList;
    }

    public void setVinList(List<TrxVin> vinList) {
        this.vinList = vinList;
    }

    public List<TrxVout> getVoutList() {
        return voutList;
    }

    public void setVoutList(List<TrxVout> voutList) {
        this.voutList = voutList;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Long getConfirms() {
        return confirms;
    }

    public void setConfirms(Long confirms) {
        this.confirms = confirms;
    }

    public String getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(String coinbase) {
        this.coinbase = coinbase;
    }

    public List<String> getTrxIdList() {
        return trxIdList;
    }

    public void setTrxIdList(List<String> trxIdList) {
        this.trxIdList = trxIdList;
    }
}