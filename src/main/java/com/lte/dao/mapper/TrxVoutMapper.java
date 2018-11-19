package com.lte.dao.mapper;

import com.lte.dao.entity.TrxVout;

import java.util.List;

public interface TrxVoutMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrxVout record);

    int insertSelective(TrxVout record);

    TrxVout selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrxVout record);

    int updateByPrimaryKey(TrxVout record);

    List<TrxVout> selectListByTrxId(String trxId);

    void insertBatch(List<TrxVout> list);

    int updateUseByCondition(TrxVout record);

    TrxVout selectOneByCondition(TrxVout record);

    List<TrxVout> calcBalance(TrxVout record);

    List<TrxVout> calcBalanceList(TrxVout record);

    List<String> selectTrxIdByAddress(String address);

    TrxVout getNewBanlance(String address);
}