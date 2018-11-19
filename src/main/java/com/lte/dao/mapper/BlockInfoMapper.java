package com.lte.dao.mapper;

import com.lte.dao.entity.BlockInfo;

import java.util.List;

public interface BlockInfoMapper {
    int deleteByPrimaryKey(Long blockNum);

    int insert(BlockInfo record);

    int insertSelective(BlockInfo record);

    BlockInfo selectByPrimaryKey(Long blockNum);

    int updateByPrimaryKeySelective(BlockInfo record);

    int updateByPrimaryKey(BlockInfo record);

    Long queryMaxBlockNum();

    void insertBatch(List<BlockInfo> list);

    List<BlockInfo> getNewBlockList();
}