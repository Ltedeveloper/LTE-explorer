package com.lte.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lte.config.MiningInfo;
import com.lte.config.RealData;
import com.lte.dao.entity.*;
import com.lte.dao.mapper.*;
import com.lte.util.Constant;
import com.lte.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayakui on 2018/1/15 0015.
 */
@Service
public class StaticService {

    private static Logger logger = LoggerFactory.getLogger(StaticService.class);

    @Autowired
    private RequestWalletService requestWalletService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private RealData realData;

    @Autowired
    private BlockInfoMapper blockInfoMapper;

    @Autowired
    private BlockTransactionMapper blockTransactionMapper;

    @Autowired
    private CalcAddressBalanceMapper calcAddressBalanceMapper;

    @Autowired
    private TrxVoutMapper trxVoutMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    public void setBlockRealData() {
        List<BlockInfo> blockList = blockInfoMapper.getNewBlockList();
        if (blockList != null && blockList.size() > 0) {
            realData.setBlockNum(blockList.get(0).getBlockNum());
        }
        realData.setBlockInfo(JSONObject.toJSONString(blockList));
    }

    public void setTransactionRealData() {
        List<BlockTransaction> transactionList = blockTransactionMapper.getNewTransaction();
        realData.setTransactionInfo(JSONObject.toJSONString(transactionList));
    }

    public void setMiningInfo() {
        JSONObject json = requestWalletService.getMiningInfo();
        if (json != null) {
            MiningInfo miningInfo = new MiningInfo();
            miningInfo.setBlocks(json.getLong("blocks"));
            miningInfo.setDifficulty(json.getString("difficulty"));
            miningInfo.setNetworkhashps(json.getBigDecimal("networkhashps"));
            miningInfo.setPooledtx(json.getInteger("pooledtx"));
            realData.setMiningInfo(miningInfo);
        }
    }

    @Transactional
    public void calcAddressBalance() {

        try {
            CalcAddressBalance condition = new CalcAddressBalance();
            condition.setLimit(2000);
            List<CalcAddressBalance> calcList = calcAddressBalanceMapper.selectBatch(condition);

            //地址余额需要失效
            List<String> addressList = new ArrayList<>();
            List<Integer> calcIdList = new ArrayList<>();
            if (calcList != null && calcList.size() > 0) {
                for (CalcAddressBalance calc : calcList) {
                    addressList.add(calc.getAddress());
                    calcIdList.add(calc.getId());
                }

                //更新地址余额失效
                Balance balanceUpdate = new Balance();
                balanceUpdate.setAddressList(addressList);
                balanceUpdate.setIsValid(Constant.ADDRESS_INVALID);
                balanceMapper.updateValidBatch(balanceUpdate);

                //删除已计算的数据
                condition = new CalcAddressBalance();
                condition.setIdList(calcIdList);
                calcAddressBalanceMapper.deleteBatch(condition);
            }
        } catch (Exception e) {
            throw new RuntimeException("更新地址信息异常");
        }
    }

    public void syncBlock(Long blockNum) {

        //获取队列数据，请求
        List<BlockInfo> blockList = new ArrayList<>();
        List<BlockTransaction> transactionList = new ArrayList<>();
        List<TrxVout> trxVoutList = new ArrayList<>();
        List<TrxVin> trxVinList = new ArrayList<>();
        try {
            logger.info("【处理区块{}开始】", blockNum);
            String blockHash = requestWalletService.getBlockHash(blockNum);//获取区块hash
            if (StringUtils.isEmpty(blockHash)) {
                return;
            }
            JSONObject blockJson = requestWalletService.getBlock(blockHash); // 获取打包代理快
            if (blockJson == null) {
                return;
            }

            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setBlockNum(blockNum);
            blockInfo.setBlockSize(blockJson.getString("size"));
            Long time = blockJson.getLong("time");
            blockInfo.setBlockTime(TimeUtil.longToString(time * 1000 + 8 * 60 * 60 * 1000));
            blockInfo.setHash(blockJson.getString("hash"));
            blockInfo.setPreviousBlockHash(blockJson.getString("previousblockhash"));
            blockInfo.setNonce(blockJson.getString("nonce"));
            blockInfo.setDifficulty(blockJson.getString("difficulty"));
            blockInfo.setCreateTime(TimeUtil.getCurrentTime());

            JSONArray trxArray = blockJson.getJSONArray("tx");

            BigDecimal blockAmount = new BigDecimal(0);
            //查询交易
            if (trxArray != null && trxArray.size() > 0) {
                blockInfo.setTrxNum(trxArray.size());
                for (int i = 0; i < trxArray.size(); i++) {
                    String trxId = trxArray.getString(i);
                    JSONObject trxJson = requestWalletService.getRawTransaction(trxId);
                    //出块奖励交易
                    if (i == 0) {
                        BigDecimal trxAmount = new BigDecimal(0);
                        String signee = null;

                        //vout处理
                        JSONArray voutArray = trxJson.getJSONArray("vout");
                        if (voutArray != null && voutArray.size() > 0) {
                            for (int j = 0; j < voutArray.size(); j++) {

                                JSONObject voutJson = voutArray.getJSONObject(j);
                                JSONObject scriptPubKeyJson = voutJson.getJSONObject("scriptPubKey");
                                if (scriptPubKeyJson != null && !"nonstandard".equals(scriptPubKeyJson.getString("type"))
                                        && voutJson.getBigDecimal("value") != null && voutJson.getBigDecimal("value").compareTo(new BigDecimal(0)) > 0) {

                                    //生成vout记录
                                    TrxVout trxVout = new TrxVout();
                                    trxAmount = trxAmount.add(voutJson.getBigDecimal("value"));
                                    trxVout.setAmount(voutJson.getBigDecimal("value"));
                                    trxVout.setBlockNum(blockInfo.getBlockNum());
                                    trxVout.setTrxId(trxId);
                                    trxVout.setIsUse(Constant.VOUT_UNUSE);
                                    trxVout.setVout(voutJson.getInteger("n"));
                                    trxVout.setCreateTime(TimeUtil.getCurrentTime());

                                    String voutAddress = scriptPubKeyJson.getJSONArray("addresses").getString(0);
                                    trxVout.setAddress(voutAddress);

                                    trxVoutList.add(trxVout);

                                    //生成用于显示的矿工地址
                                    if (StringUtils.isEmpty(signee)) {
                                        signee = voutAddress;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }

                        blockAmount = blockAmount.add(trxAmount);
                        blockInfo.setSignee(signee);
                        blockInfo.setReward(trxAmount);

                        //生成交易信息
                        BlockTransaction transaction = new BlockTransaction();
                        transaction.setBlockHash(trxJson.getString("blockhash"));
                        transaction.setBlockNum(blockInfo.getBlockNum());
                        transaction.setHash(trxJson.getString("hash"));
                        transaction.setTrxId(trxId);
                        transaction.setTrxTime(TimeUtil.longToString(trxJson.getLong("time") * 1000 + 8 * 60 * 60 * 1000));
                        transaction.setTrxType(Constant.TRX_REWARD_TYPE);
                        transaction.setCreatedAt(TimeUtil.getCurrentTime());
                        transaction.setToAddress(signee);
                        transaction.setFromAddress("");
                        transaction.setFee(new BigDecimal(0));
                        transaction.setAmount(trxAmount);

                        //解析coinbase的值
                        JSONArray vinArray = trxJson.getJSONArray("vin");
                        if (vinArray != null) {
                            transaction.setCoinbase(vinArray.getJSONObject(0).getString("coinbase"));
                        }

                        transactionList.add(transaction);

                    } else {//普通转账

                        //生成交易信息
                        BlockTransaction transaction = new BlockTransaction();
                        transaction.setBlockHash(trxJson.getString("blockhash"));
                        transaction.setBlockNum(blockInfo.getBlockNum());
                        transaction.setHash(trxJson.getString("hash"));
                        transaction.setTrxId(trxId);
                        transaction.setTrxTime(TimeUtil.longToString(trxJson.getLong("time") * 1000 + 8 * 60 * 60 * 1000));
                        transaction.setTrxType(Constant.TRX_TRANSFER_TYPE);
                        transaction.setCreatedAt(TimeUtil.getCurrentTime());

                        //先处理vin
                        JSONArray vinArray = trxJson.getJSONArray("vin");

                        BigDecimal vinTotal = new BigDecimal(0);
                        BigDecimal voutTotal = new BigDecimal(0);
                        if (vinArray != null && vinArray.size() > 0) {

                            for (int j = 0; j < vinArray.size(); j++) {

                                JSONObject vinJson = vinArray.getJSONObject(j);
                                //查询vin的详细信息
                                int vout = vinJson.getInteger("vout");
                                String vinTrxId = vinJson.getString("txid");

                                TrxVin trxVin = new TrxVin();
                                trxVin.setTrxId(trxId);
                                trxVin.setBlockNum(blockInfo.getBlockNum());
                                trxVin.setSourceTrxId(vinTrxId);
                                trxVin.setVout(vout);
                                trxVin.setCreateTime(TimeUtil.getCurrentTime());

                                JSONObject vinJsonTrx = requestWalletService.getRawTransaction(vinTrxId);
                                if (vinJsonTrx == null) {
                                    continue;
                                }
                                JSONArray vinJsonTrxArray = vinJsonTrx.getJSONArray("vout");
                                if (vinJsonTrxArray != null && vinJsonTrxArray.size() > 0) {
                                    for (int k = 0; k < vinJsonTrxArray.size(); k++) {
                                        JSONObject voutJson = vinJsonTrxArray.getJSONObject(k);
                                        if (voutJson.getInteger("n") == vout) {
                                            //vin累加
                                            vinTotal = vinTotal.add(voutJson.getBigDecimal("value"));
                                            trxVin.setAmount(voutJson.getBigDecimal("value"));

                                            String vinAddress = voutJson.getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                                            trxVin.setAddress(vinAddress);

                                            //设置显示的转出地址
                                            if (StringUtils.isEmpty(transaction.getFromAddress())) {
                                                transaction.setFromAddress(vinAddress);
                                            }
                                        }
                                    }
                                }

                                trxVinList.add(trxVin);

                            }
                            //累加块发送总金额
                            blockAmount = blockAmount.add(vinTotal);
                        }


                        //处理vout
                        JSONArray voutArray = trxJson.getJSONArray("vout");
                        if (voutArray != null && voutArray.size() > 0) {
                            for (int j = 0; j < voutArray.size(); j++) {

                                JSONObject voutJson = voutArray.getJSONObject(j);
                                if (voutJson.getBigDecimal("value") != null && voutJson.getBigDecimal("value").compareTo(new BigDecimal(0)) > 0) {
                                    TrxVout trxVout = new TrxVout();
                                    trxVout.setBlockNum(blockInfo.getBlockNum());
                                    trxVout.setTrxId(trxId);
                                    trxVout.setIsUse(Constant.VOUT_UNUSE);
                                    trxVout.setAmount(voutJson.getBigDecimal("value"));
                                    trxVout.setVout(voutJson.getInteger("n"));

                                    voutTotal = voutTotal.add(voutJson.getBigDecimal("value"));

                                    String voutAddress = voutJson.getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                                    trxVout.setAddress(voutAddress);
                                    trxVout.setCreateTime(TimeUtil.getCurrentTime());

                                    trxVoutList.add(trxVout);
                                    if (StringUtils.isEmpty(transaction.getToAddress())) {
                                        transaction.setToAddress(voutAddress);
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                        //计算总金额金额和手续费
                        transaction.setAmount(voutTotal);//转出，
                        transaction.setFee(vinTotal.subtract(voutTotal));//手续费
                        transactionList.add(transaction);
                    }
                }
            }
            blockInfo.setAmount(blockAmount);
            blockList.add(blockInfo);

            logger.info("【处理区块{}结束】", blockNum);
        } catch (Exception e) {
            logger.error("{}块同步数据出错", blockNum, e);
        } finally {
            //保存批量数据
            blockService.batchInsertSyncData2(blockList, transactionList, trxVoutList, trxVinList);
        }
    }

}
