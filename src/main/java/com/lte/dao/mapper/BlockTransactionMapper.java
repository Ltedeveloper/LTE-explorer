package com.lte.dao.mapper;

import com.lte.dao.entity.BlockTransaction;

import java.util.List;

public interface BlockTransactionMapper {
    int deleteByPrimaryKey(String trxId);

    int insert(BlockTransaction record);

    int insertSelective(BlockTransaction record);

    BlockTransaction selectByPrimaryKey(String trxId);

    int updateByPrimaryKeySelective(BlockTransaction record);

    int updateByPrimaryKey(BlockTransaction record);

    void insertBatch(List<BlockTransaction> list);

    List<BlockTransaction> getNewTransaction();

    List<BlockTransaction> selectTransactionByBlockNum(BlockTransaction record);

    List<BlockTransaction> selectTransactionList(BlockTransaction record);

    List<BlockTransaction> selectTrxListByIds(BlockTransaction record);

}