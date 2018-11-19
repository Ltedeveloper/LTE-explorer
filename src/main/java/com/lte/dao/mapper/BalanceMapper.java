package com.lte.dao.mapper;

import com.lte.dao.entity.Balance;

public interface BalanceMapper {
    int deleteByPrimaryKey(String address);

    int insert(Balance record);

    int insertSelective(Balance record);

    Balance selectByPrimaryKey(String address);

    int updateByPrimaryKeySelective(Balance record);

    int updateByPrimaryKey(Balance record);

    int updateValidBatch(Balance record);
}