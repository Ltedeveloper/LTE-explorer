package com.lte.service;

import com.lte.config.RealData;
import com.lte.dao.entity.BlockTransaction;
import com.lte.dao.mapper.BlockTransactionMapper;
import com.lte.dao.mapper.TrxVinMapper;
import com.lte.dao.mapper.TrxVoutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayakui on 2018/1/15 0015.
 */
@Service
public class TransactionService {

    @Autowired
    private BlockTransactionMapper blockTransactionMapper;

    @Autowired
    private TrxVinMapper trxVinMapper;

    @Autowired
    private TrxVoutMapper trxVoutMapper;

    @Autowired
    private RealData realData;

    public List<BlockTransaction> selectTransactionByBlockNum(Long blockNum,int start){
        int end = start + 25;
        BlockTransaction transaction = new BlockTransaction();
        transaction.setBlockNum(blockNum);
        transaction.setStart(start);
        transaction.setEnd(end);
        List<BlockTransaction> transactionList = blockTransactionMapper.selectTransactionByBlockNum(transaction);
        if(transactionList!=null && transactionList.size()>0){
            for(BlockTransaction tx:transactionList){//查询vin和vout
                tx.setVinList(trxVinMapper.selectListByTrxId(tx.getTrxId()));
                tx.setVoutList(trxVoutMapper.selectListByTrxId(tx.getTrxId()));
            }
        }

        return transactionList;
    }

    /**
     * 查询交易信息
     * @param trxId
     * @return
     */
    public BlockTransaction selectByPrimaryKey(String trxId){
        BlockTransaction transaction = blockTransactionMapper.selectByPrimaryKey(trxId);

        //计算交易确认次数和vin、vout
        if(transaction!=null){
            transaction.setVinList(trxVinMapper.selectListByTrxId(transaction.getTrxId()));
            transaction.setVoutList(trxVoutMapper.selectListByTrxId(transaction.getTrxId()));
            if(transaction.getBlockNum()!=null && transaction.getBlockNum()>0){
                transaction.setConfirms(realData.getBlockNum()-transaction.getBlockNum()+1);
            }
        }
        if(transaction.getConfirms()==null || transaction.getConfirms()<0){
            transaction.setConfirms(0L);
        }
        return transaction;
    }

    public List<BlockTransaction> queryTransactionByAddress(String address,int start){

        //根据地址查询所有相关交易id
        List<String> vinTrxList = trxVinMapper.selectTrxIdByAddress(address);
        List<String> voutTrxList = trxVoutMapper.selectTrxIdByAddress(address);

        List<String> trxIdList = new ArrayList<>();
        if(vinTrxList!=null && vinTrxList.size()>0){
            for(String trxId:vinTrxList){
                if(!trxIdList.contains(trxId)){
                    trxIdList.add(trxId);
                }
            }
        }
        if(voutTrxList!=null && voutTrxList.size()>0){
            for(String trxId:voutTrxList){
                if(!trxIdList.contains(trxId)){
                    trxIdList.add(trxId);
                }
            }
        }

        BlockTransaction record = new BlockTransaction();
        record.setTrxIdList(trxIdList);
        record.setStart(start);
        record.setEnd(start + 25);
        List<BlockTransaction> transactionList = blockTransactionMapper.selectTransactionList(record);

        if(transactionList!=null && transactionList.size()>0){
            for(BlockTransaction transaction:transactionList){
                //计算交易确认次数和vin、vout
                if(transaction!=null){
                    transaction.setVinList(trxVinMapper.selectListByTrxId(transaction.getTrxId()));
                    transaction.setVoutList(trxVoutMapper.selectListByTrxId(transaction.getTrxId()));
                    if(transaction.getBlockNum()!=null && transaction.getBlockNum()>0){
                        transaction.setConfirms(realData.getBlockNum()-transaction.getBlockNum()+1);
                    }
                }
                if(transaction.getConfirms()==null || transaction.getConfirms()<0){
                    transaction.setConfirms(0L);
                }
            }
        }
        return transactionList;
    }
}
