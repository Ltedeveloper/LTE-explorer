package com.lte.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lte.config.RealData;
import com.lte.dao.entity.*;
import com.lte.dao.mapper.BlockTransactionMapper;
import com.lte.service.BlockService;
import com.lte.service.RequestWalletService;
import com.lte.util.Constant;
import com.lte.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mayakui on 2018/1/11 0011.
 * 钱包获取区块信息定时任务
 */
@Component
public class ObtainBlockTask {

    private static Logger logger = LoggerFactory.getLogger(ObtainBlockTask.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private RequestWalletService requestWalletService;

    @Autowired
    private BlockTransactionMapper blockTransactionMapper;

    @Autowired
    private RealData realData;

    @Scheduled(cron="0/5 * * * * ?")
    public void syncBlockData(){

        logger.info("【同步数据开始】");
        //查询本地数据库最大块号
        Long blockNum = blockService.queryMaxBlockNum();
        if(null == blockNum){
            blockNum = 0L;
        }
        Long total = requestWalletService.getBlockCount();
        //设置当前区块
        realData.setBlockNum(total);

        //分配任务数
        Long count = total - blockNum;
        if(count<=0){
            //同步未确认的交易
            syncUnconfirmTrx();
            return;
        }
        if(count>200){
            count = 200L;
        }

        BlockingQueue<Long> queue = new LinkedBlockingQueue<Long>();
        for(int i=1;i<=count;i++){
            SyncOneBlock(blockNum+i);
            logger.info("【同步{}块结束】",blockNum+i);
        }
        logger.info("【同步数据结束】");
    }

    private void SyncOneBlock(Long blockNum){
        List<BlockInfo> blockList = new ArrayList<>();
        List<BlockTransaction> transactionList = new ArrayList<>();
        List<TrxVout> trxVoutList = new ArrayList<>();
        List<TrxVin> trxVinList = new ArrayList<>();

        String blockHash = requestWalletService.getBlockHash(blockNum);//获取区块hash
        if(StringUtils.isEmpty(blockHash)){
            return;
        }
        JSONObject blockJson = requestWalletService.getBlock(blockHash); // 获取打包代理快
        if(blockJson == null){
            return;
        }

        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setBlockNum(blockNum);
        blockInfo.setBlockSize(blockJson.getString("size"));
        Long time = blockJson.getLong("time");
        blockInfo.setBlockTime(TimeUtil.longToString(time*1000));
        blockInfo.setHash(blockJson.getString("hash"));
        blockInfo.setPreviousBlockHash(blockJson.getString("previousblockhash"));
        blockInfo.setNonce(blockJson.getString("nonce"));
        blockInfo.setDifficulty(blockJson.getString("difficulty"));
        blockInfo.setCreateTime(TimeUtil.getCurrentTime());

        JSONArray trxArray = blockJson.getJSONArray("tx");

        BigDecimal blockAmount = new BigDecimal(0);
        //查询交易
        if(trxArray!=null && trxArray.size()>0){
            blockInfo.setTrxNum(trxArray.size());
            for(int i=0;i<trxArray.size();i++){
                String trxId = trxArray.getString(i);
                JSONObject trxJson = requestWalletService.getRawTransaction(trxId);
                //出块奖励交易
                if(i==0){
                    BigDecimal trxAmount = new BigDecimal(0);
                    String signee = null;

                    //vout处理
                    JSONArray voutArray = trxJson.getJSONArray("vout");
                    if(voutArray!=null && voutArray.size()>0){
                        for(int j=0;j<voutArray.size();j++){

                            JSONObject voutJson = voutArray.getJSONObject(j);
                            JSONObject scriptPubKeyJson = voutJson.getJSONObject("scriptPubKey");
                            if(scriptPubKeyJson!=null && !"nonstandard".equals(scriptPubKeyJson.getString("type"))
                                    && voutJson.getBigDecimal("value")!=null && voutJson.getBigDecimal("value").compareTo(new BigDecimal(0))>0){

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
                                if(StringUtils.isEmpty(signee)){
                                    signee= voutAddress;
                                }
                            }else{
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
                    transaction.setTrxTime(TimeUtil.longToString(trxJson.getLong("time")*1000));
                    transaction.setTrxType(Constant.TRX_REWARD_TYPE);
                    transaction.setCreatedAt(TimeUtil.getCurrentTime());
                    transaction.setToAddress(signee);
                    transaction.setFromAddress("");
                    transaction.setFee(new BigDecimal(0));
                    transaction.setAmount(trxAmount);

                    //解析coinbase的值
                    JSONArray vinArray = trxJson.getJSONArray("vin");
                    if(vinArray!=null){
                        transaction.setCoinbase(vinArray.getJSONObject(0).getString("coinbase"));
                    }

                    transactionList.add(transaction);

                }else{//普通转账

                    //生成交易信息
                    BlockTransaction transaction = new BlockTransaction();
                    transaction.setBlockHash(trxJson.getString("blockhash"));
                    transaction.setBlockNum(blockInfo.getBlockNum());
                    transaction.setHash(trxJson.getString("hash"));
                    transaction.setTrxId(trxId);
                    transaction.setTrxTime(TimeUtil.longToString(trxJson.getLong("time")*1000));
                    transaction.setTrxType(Constant.TRX_TRANSFER_TYPE);
                    transaction.setCreatedAt(TimeUtil.getCurrentTime());

                    //先处理vin
                    JSONArray vinArray = trxJson.getJSONArray("vin");

                    BigDecimal vinTotal = new BigDecimal(0);
                    BigDecimal voutTotal = new BigDecimal(0);
                    if(vinArray!=null && vinArray.size()>0){

                        for(int j=0;j<vinArray.size();j++){

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
                            if(vinJsonTrx==null){
                                continue;
                            }
                            JSONArray vinJsonTrxArray = vinJsonTrx.getJSONArray("vout");
                            if(vinJsonTrxArray!=null && vinJsonTrxArray.size()>0){
                                for(int k=0;k<vinJsonTrxArray.size();k++){
                                    JSONObject voutJson = vinJsonTrxArray.getJSONObject(k);
                                    if(voutJson.getInteger("n") == vout){
                                        //vin累加
                                        vinTotal = vinTotal.add(voutJson.getBigDecimal("value"));
                                        trxVin.setAmount(voutJson.getBigDecimal("value"));

                                        String vinAddress = voutJson.getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                                        trxVin.setAddress(vinAddress);

                                        //设置显示的转出地址
                                        if(StringUtils.isEmpty(transaction.getFromAddress())){
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
                    if(voutArray!=null && voutArray.size()>0){
                        for(int j=0;j<voutArray.size();j++){

                            JSONObject voutJson = voutArray.getJSONObject(j);
                            if(voutJson.getBigDecimal("value")!=null && voutJson.getBigDecimal("value").compareTo(new BigDecimal(0))>0){
                                TrxVout trxVout = new TrxVout();
                                trxVout.setBlockNum(blockInfo.getBlockNum());
                                trxVout.setTrxId(trxId);
                                trxVout.setIsUse(Constant.VOUT_UNUSE);
                                trxVout.setAmount(voutJson.getBigDecimal("value"));
                                trxVout.setVout(voutJson.getInteger("n"));

                                voutTotal = voutTotal.add(voutJson.getBigDecimal("value"));

                                JSONObject scriptPubKeyJson = voutJson.getJSONObject("scriptPubKey");
                                JSONArray addresses = scriptPubKeyJson.getJSONArray("addresses");
                                if(addresses==null || addresses.size()==0){
                                    continue;
                                }
                                String voutAddress = addresses.getString(0);
                                trxVout.setAddress(voutAddress);
                                trxVout.setCreateTime(TimeUtil.getCurrentTime());

                                trxVoutList.add(trxVout);
                                if(StringUtils.isEmpty(transaction.getToAddress())){
                                    transaction.setToAddress(voutAddress);
                                }
                            }else{
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

        blockService.batchInsertSyncData(blockList,transactionList,trxVoutList,trxVinList);
    }


    /**
     * 同步未确认交易
     */
    private void syncUnconfirmTrx(){
        JSONArray trxArray = requestWalletService.getRawMempool();
        if(trxArray!=null){
            realData.setUnconfirms(trxArray.size());
        }else{
            realData.setUnconfirms(0);
        }
        //查询交易
        if(trxArray!=null && trxArray.size()>0){
            List<BlockTransaction> transactionList = new ArrayList<>();
            List<TrxVout> trxVoutList = new ArrayList<>();
            List<TrxVin> trxVinList = new ArrayList<>();
            for(int i=0;i<trxArray.size();i++){
                String trxId = trxArray.getString(i);
                //查询是否已经更新数据
                BlockTransaction record = blockTransactionMapper.selectByPrimaryKey(trxId);
                if(record != null){
                    continue;
                }
                JSONObject trxJson = requestWalletService.getRawTransaction(trxId);

                //生成交易信息
                BlockTransaction transaction = new BlockTransaction();
                transaction.setBlockHash(trxJson.getString("blockhash"));
                transaction.setBlockNum(0L);
                transaction.setHash(trxJson.getString("hash"));
                transaction.setTrxId(trxId);
                transaction.setTrxTime(TimeUtil.getCurrentTime());
                transaction.setTrxType(Constant.TRX_TRANSFER_TYPE);
                transaction.setCreatedAt(TimeUtil.getCurrentTime());

                //先处理vin
                JSONArray vinArray = trxJson.getJSONArray("vin");

                BigDecimal vinTotal = BigDecimal.ZERO;
                BigDecimal voutTotal = BigDecimal.ZERO;
                if(vinArray!=null && vinArray.size()>0){

                    for(int j=0;j<vinArray.size();j++){

                        JSONObject vinJson = vinArray.getJSONObject(j);
                        //查询vin的详细信息
                        int vout = vinJson.getInteger("vout");
                        String vinTrxId = vinJson.getString("txid");

                        TrxVin trxVin = new TrxVin();
                        trxVin.setTrxId(trxId);
                        trxVin.setBlockNum(0L);
                        trxVin.setSourceTrxId(vinTrxId);
                        trxVin.setVout(vout);
                        trxVin.setCreateTime(TimeUtil.getCurrentTime());

                        JSONObject vinJsonTrx = requestWalletService.getRawTransaction(vinTrxId);
                        JSONArray vinJsonTrxArray = vinJsonTrx.getJSONArray("vout");
                        if(vinJsonTrxArray!=null && vinJsonTrxArray.size()>0){
                            for(int k=0;k<vinJsonTrxArray.size();k++){
                                JSONObject voutJson = vinJsonTrxArray.getJSONObject(k);
                                if(voutJson.getInteger("n") == vout){
                                    //vin累加
                                    vinTotal = vinTotal.add(voutJson.getBigDecimal("value"));
                                    trxVin.setAmount(voutJson.getBigDecimal("value"));

                                    JSONObject scriptPubKeyJson = voutJson.getJSONObject("scriptPubKey");
                                    JSONArray addresses = scriptPubKeyJson.getJSONArray("addresses");
                                    if(addresses==null || addresses.size()==0){
                                        continue;
                                    }
                                    String vinAddress = addresses.getString(0);
                                    trxVin.setAddress(vinAddress);

                                    //设置显示的转出地址
                                    if(StringUtils.isEmpty(transaction.getFromAddress())){
                                        transaction.setFromAddress(vinAddress);
                                    }
                                }
                            }
                        }

                        trxVinList.add(trxVin);

                    }
                }


                //处理vout
                JSONArray voutArray = trxJson.getJSONArray("vout");
                if(voutArray!=null && voutArray.size()>0){
                    for(int j=0;j<voutArray.size();j++){

                        TrxVout trxVout = new TrxVout();
                        trxVout.setBlockNum(0L);
                        trxVout.setTrxId(trxId);
                        trxVout.setIsUse(Constant.VOUT_UNUSE);

                        JSONObject voutJson = voutArray.getJSONObject(j);
                        if(voutJson.getBigDecimal("value")!=null && voutJson.getBigDecimal("value").compareTo(new BigDecimal(0))>0){
                            trxVout.setAmount(voutJson.getBigDecimal("value"));
                            voutTotal = voutTotal.add(voutJson.getBigDecimal("value"));

                            trxVout.setVout(voutJson.getInteger("n"));

                            String voutAddress = voutJson.getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                            trxVout.setAddress(voutAddress);
                            trxVout.setCreateTime(TimeUtil.getCurrentTime());

                            if(StringUtils.isEmpty(transaction.getToAddress())){
                                transaction.setToAddress(voutAddress);
                            }
                        }
                        trxVoutList.add(trxVout);
                    }
                }
                //计算总金额金额和手续费
                transaction.setAmount(voutTotal);//转出，
                transaction.setFee(vinTotal.subtract(voutTotal));//手续费
                transactionList.add(transaction);
            }

            blockService.addUnconfirmTrx(transactionList,trxVoutList,trxVinList);
        }
    }

}
