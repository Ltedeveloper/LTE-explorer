package com.lte.service;

import com.lte.dao.entity.TrxVin;
import com.lte.dao.mapper.TrxVinMapper;
import com.lte.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mayaki on 2018/1/19 0019.
 */
@Service
public class TrxVinService {

    @Autowired
    private TrxVinMapper trxVinMapper;

    /**
     * 批量插入设置，list长度过大，mysql不支持
     * @param trxVinList
     */
    public void insertBatch(List<TrxVin> trxVinList){
        for(int j=0;j<trxVinList.size();j=j+ Constant.BATCH_LENGTH){
            List<TrxVin> sub = null;
            if(j+Constant.BATCH_LENGTH>trxVinList.size()){
                sub=trxVinList.subList(j,trxVinList.size());
            }else{
                sub = trxVinList.subList(j,j+Constant.BATCH_LENGTH);
            }
            trxVinMapper.insertBatch(sub);
        }
    }
}
