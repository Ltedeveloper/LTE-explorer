package com.lte.dao.mapper;

import com.lte.dao.entity.TrxVin;

import java.util.List;

public interface TrxVinMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrxVin record);

    int insertSelective(TrxVin record);

    TrxVin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrxVin record);

    int updateByPrimaryKey(TrxVin record);

    void insertBatch(List<TrxVin> list);

    List<TrxVin> selectListByTrxId(String trxId);

    TrxVin selectOneByCondition(TrxVin record);

    List<String> selectTrxIdByAddress(String address);
}