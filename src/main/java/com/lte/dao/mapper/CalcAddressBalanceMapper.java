package com.lte.dao.mapper;

import com.lte.dao.entity.CalcAddressBalance;

import java.util.List;

public interface CalcAddressBalanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CalcAddressBalance record);

    int insertSelective(CalcAddressBalance record);

    CalcAddressBalance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CalcAddressBalance record);

    int updateByPrimaryKey(CalcAddressBalance record);

    int insertBatch(List<CalcAddressBalance> list);

    List<CalcAddressBalance> selectBatch(CalcAddressBalance record);

    void deleteBatch(CalcAddressBalance record);
}