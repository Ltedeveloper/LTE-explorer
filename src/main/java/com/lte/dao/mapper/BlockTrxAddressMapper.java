package com.lte.dao.mapper;

import com.lte.dao.entity.BlockTrxAddress;

import java.util.List;

public interface BlockTrxAddressMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BlockTrxAddress record);

    int insertSelective(BlockTrxAddress record);

    BlockTrxAddress selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BlockTrxAddress record);

    int updateByPrimaryKey(BlockTrxAddress record);

    void insertBatch(List<BlockTrxAddress> list);

    List<BlockTrxAddress> selectVinList(String trxId);
    List<BlockTrxAddress> selectVoutList(String trxId);
}