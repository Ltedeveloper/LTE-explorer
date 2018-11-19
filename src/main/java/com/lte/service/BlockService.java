package com.lte.service;

import com.alibaba.fastjson.JSONObject;
import com.lte.dao.entity.*;
import com.lte.dao.mapper.*;
import com.lte.prototol.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayakui on 2018/1/11 0011.
 */
@Service
public class BlockService {

    private static Logger logger = LoggerFactory.getLogger(BlockService.class);

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TrxVoutService trxVoutService;

    @Autowired
    private TrxVinService trxVinService;

    @Autowired
    private BlockInfoMapper blockInfoMapper;

    @Autowired
    private BlockTransactionMapper blockTransactionMapper;

    @Autowired
    private TrxVinMapper trxVinMapper;

    @Autowired
    private TrxVoutMapper trxVoutMapper;

    public Long queryMaxBlockNum() {
        return blockInfoMapper.queryMaxBlockNum();
    }

    @Transactional
    public void batchInsertSyncData(List<BlockInfo> blockList,
                                    List<BlockTransaction> transactionList,
                                    List<TrxVout> trxVoutList,
                                    List<TrxVin> trxVinList) {
        try {
            if (blockList != null && blockList.size() > 0) {
                blockInfoMapper.insertBatch(blockList);
            }

            if (transactionList != null && transactionList.size() > 0) {

                List<String> trxIdList = new ArrayList<>();
                for (BlockTransaction transaction : transactionList) {
                    trxIdList.add(transaction.getTrxId());
                }
                Map<String, BlockTransaction> trxMap = new HashMap<String, BlockTransaction>();
                if (trxIdList.size() > 0) {
                    BlockTransaction condition = new BlockTransaction();
                    condition.setTrxIdList(trxIdList);
                    List<BlockTransaction> recordList = blockTransactionMapper.selectTrxListByIds(condition);
                    if (recordList != null && recordList.size() > 0) {
                        for (BlockTransaction trx : recordList) {
                            trxMap.put(trx.getTrxId(), trx);
                        }
                    }
                }

                for (BlockTransaction transaction : transactionList) {
                    BlockTransaction trxRecord = trxMap.get(transaction.getTrxId());
                    if (trxRecord == null) {
                        blockTransactionMapper.insert(transaction);
                    } else {
                        trxRecord.setBlockNum(transaction.getBlockNum());
                        trxRecord.setTrxTime(transaction.getTrxTime());
                        blockTransactionMapper.updateByPrimaryKeySelective(trxRecord);
                    }
                }
            }

            if (trxVoutList != null && trxVoutList.size() > 0) {
                for (TrxVout trxVout : trxVoutList) {
                    TrxVout trxVoutRecord = new TrxVout();
                    trxVoutRecord.setTrxId(trxVout.getTrxId());
                    trxVoutRecord.setVout(trxVout.getVout());

                    trxVoutRecord = trxVoutMapper.selectOneByCondition(trxVoutRecord);
                    if (trxVoutRecord == null) {
                        trxVoutMapper.insert(trxVout);
                    } else {
                        trxVoutRecord.setBlockNum(trxVout.getBlockNum());
                        trxVoutMapper.updateByPrimaryKeySelective(trxVoutRecord);
                    }
                }
            }

            if (trxVinList != null && trxVinList.size() > 0) {
                for (TrxVin trxVin : trxVinList) {
                    TrxVin trxVinRecord = new TrxVin();
                    trxVinRecord.setTrxId(trxVin.getTrxId());
                    trxVinRecord.setVout(trxVin.getVout());

                    trxVinRecord = trxVinMapper.selectOneByCondition(trxVinRecord);
                    if (trxVinRecord == null) {
                        trxVinMapper.insert(trxVin);
                    } else {
                        trxVinRecord.setBlockNum(trxVin.getBlockNum());
                        trxVinMapper.updateByPrimaryKeySelective(trxVinRecord);
                    }
                }
            }

            balanceService.deal(trxVinList, trxVoutList);
            //根据更新vout
        } catch (Exception e) {
            e.printStackTrace();

            throw new WalletException("批量插入失败");
        }
    }

    @Transactional
    public void batchInsertSyncData2(List<BlockInfo> blockList,
                                     List<BlockTransaction> transactionList,
                                     List<TrxVout> trxVoutList,
                                     List<TrxVin> trxVinList) {
        try {
            if (blockList != null && blockList.size() > 0) {
                blockInfoMapper.insertBatch(blockList);
            }

            if (transactionList != null && transactionList.size() > 0) {

                blockTransactionMapper.insertBatch(transactionList);
            }

            if (trxVoutList != null && trxVoutList.size() > 0) {
                trxVoutService.insertBatch(trxVoutList);
            }

            if (trxVinList != null && trxVinList.size() > 0) {
                trxVinService.insertBatch(trxVinList);
            }
            //根据更新vout
        } catch (Exception e) {
            e.printStackTrace();

            throw new WalletException("批量插入失败");
        }
    }

    public BlockInfo queryBlockInfoByNum(Long blockNum) {
        return blockInfoMapper.selectByPrimaryKey(blockNum);
    }

    @Transactional
    public void addUnconfirmTrx(List<BlockTransaction> transactionList,
                                List<TrxVout> trxVoutList,
                                List<TrxVin> trxVinList) {
        try {
            if (transactionList != null && transactionList.size() > 0) {
                blockTransactionMapper.insertBatch(transactionList);
            }
            if (trxVoutList != null && trxVoutList.size() > 0) {
                trxVoutService.insertBatch(trxVoutList);
            }
            if (trxVinList != null && trxVinList.size() > 0) {
                trxVinMapper.insertBatch(trxVinList);
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new WalletException("批量插入失败");
        }
    }

    public JSONObject queryAddressDetail(String address) {

        JSONObject data = new JSONObject();
        Balance balance = balanceService.queryBalance(address);
        data.put("address", balance.getAddress());
        data.put("balance", balance.getBalance());
        //根据地址查询所有相关交易id
        List<String> vinTrxList = trxVinMapper.selectTrxIdByAddress(address);
        List<String> voutTrxList = trxVoutMapper.selectTrxIdByAddress(address);

        List<String> trxIdList = new ArrayList<>();
        if (vinTrxList != null && vinTrxList.size() > 0) {
            for (String trxId : vinTrxList) {
                if (!trxIdList.contains(trxId)) {
                    trxIdList.add(trxId);
                }
            }
        }
        if (voutTrxList != null && voutTrxList.size() > 0) {
            for (String trxId : voutTrxList) {
                if (!trxIdList.contains(trxId)) {
                    trxIdList.add(trxId);
                }
            }
        }

        data.put("trxNum", trxIdList.size());
        return data;
    }
}
