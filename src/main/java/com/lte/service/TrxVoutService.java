package com.lte.service;

import com.lte.dao.entity.TrxVout;
import com.lte.dao.mapper.TrxVoutMapper;
import com.lte.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by myakui on 2018/1/19 0019.
 */
@Service
public class TrxVoutService {

    @Autowired
    private TrxVoutMapper trxVoutMapper;

    /**
     * 批量插入设置，list长度过大，mysql不支持
     * @param trxVoutList
     */
    public void insertBatch(List<TrxVout> trxVoutList){
        for(int j=0;j<trxVoutList.size();j=j+ Constant.BATCH_LENGTH){
            List<TrxVout> sub = null;
            if(j+Constant.BATCH_LENGTH>trxVoutList.size()){
                sub=trxVoutList.subList(j,trxVoutList.size());
            }else{
                sub = trxVoutList.subList(j,j+Constant.BATCH_LENGTH);
            }
            trxVoutMapper.insertBatch(sub);
        }
    }
}
