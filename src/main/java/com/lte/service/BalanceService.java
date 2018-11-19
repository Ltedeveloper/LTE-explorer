package com.lte.service;

import com.lte.dao.entity.Balance;
import com.lte.dao.entity.CalcAddressBalance;
import com.lte.dao.entity.TrxVin;
import com.lte.dao.entity.TrxVout;
import com.lte.dao.mapper.BalanceMapper;
import com.lte.dao.mapper.CalcAddressBalanceMapper;
import com.lte.dao.mapper.TrxVinMapper;
import com.lte.dao.mapper.TrxVoutMapper;
import com.lte.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mayakui on 2018/1/17 0017.
 */
@Service
public class BalanceService {
    private static Logger logger = LoggerFactory.getLogger(BalanceService.class);

    @Autowired
    private CalcAddressBalanceMapper calcAddressBalanceMapper;

    @Autowired
    private TrxVoutMapper trxVoutMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    /**
     * 同步交易完成后，汇总金额
     * @param trxVoutList
     * @param trxVinList
     */
    public void deal(List<TrxVin> trxVinList,List<TrxVout> trxVoutList){

        //需要重新统计余额的地址
        Set<String> addressSet = new HashSet<String>();

        //根据vin改变vout
        if(trxVinList!=null && trxVinList.size()>0){
            for(TrxVin trxVin:trxVinList){
                //查询对应的vout
                TrxVout condition = new TrxVout();
                condition.setTrxId(trxVin.getSourceTrxId());
                condition.setVout(trxVin.getVout());

                TrxVout trxVout = trxVoutMapper.selectOneByCondition(condition);
                if(trxVout!=null){
                    condition = new TrxVout();
                    condition.setId(trxVout.getId());
                    condition.setIsUse(Constant.VOUT_USE);

                    trxVoutMapper.updateByPrimaryKeySelective(condition);

                    addressSet.add(trxVout.getAddress());
                }
            }
        }

        if(trxVoutList!=null && trxVoutList.size()>0){
            for(TrxVout trxVout:trxVoutList){
                addressSet.add(trxVout.getAddress());
            }
        }

        List<CalcAddressBalance> calcList = new ArrayList<CalcAddressBalance>();
        for(String address:addressSet){
            CalcAddressBalance calc = new CalcAddressBalance();
            calc.setAddress(address);

            calcList.add(calc);
        }
        //需要计算余额的地址插入待计算表里
        if(calcList.size()>0){
            calcAddressBalanceMapper.insertBatch(calcList);
            //logger.info("【批量插入待更新余额的地址：{}】",calcList.size());
        }
    }

    /**
     * 根据地址查询余额
     * @param address
     * @return
     */
    public Balance queryBalance(String address){
        Balance balance = balanceMapper.selectByPrimaryKey(address);

        //重新查询
        if(balance == null){
            TrxVout trxVout = trxVoutMapper.getNewBanlance(address);
            balance = new Balance();
            if(trxVout == null){
                balance.setAddress(address);
                balance.setBalance(BigDecimal.ZERO);
            }else{
                balance.setAddress(address);
                balance.setBalance(trxVout.getAmount());
            }
            balance.setIsValid(Constant.ADDRESS_VALID);
            balanceMapper.insert(balance);
            //结果插入余额记录
        }else if(balance.getIsValid()==Constant.ADDRESS_INVALID){
            TrxVout trxVout = trxVoutMapper.getNewBanlance(address);
            balance = new Balance();
            if(trxVout == null){
                balance.setAddress(address);
                balance.setBalance(BigDecimal.ZERO);
            }else{
                balance.setAddress(address);
                balance.setBalance(trxVout.getAmount());
            }
            balance.setIsValid(Constant.ADDRESS_VALID);
            balanceMapper.updateByPrimaryKeySelective(balance);
        }
        return balance;
    }
}
