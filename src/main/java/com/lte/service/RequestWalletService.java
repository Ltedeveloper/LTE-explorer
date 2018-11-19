package com.lte.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lte.config.Wallet;
import com.lte.prototol.WalletException;
import com.lte.task.ObtainBlockTask;
import com.lte.util.Constant;
import com.lte.util.HttpUtil;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayakui on 2018/1/11 0011.
 */
@Service
public class RequestWalletService {

    private static Logger logger = LoggerFactory.getLogger(RequestWalletService.class);

    @Autowired
    private Wallet wallet;

    public String requestWallet(String method, List<Object> params) {

        JSONObject requestJson = new JSONObject();
        requestJson.put("jsonrpc", "2.0");
        requestJson.put("id", 1);
        requestJson.put("method", method);
        requestJson.put("params", params);
        JSONObject resultJson = null;
        String resultStr = null;
        String request = null;

        for (int i = 0; i < Constant.HTTP_RESNED_MAX; i++) {

            try {
                request = requestJson.toJSONString();
                logger.info("【请求钱包发送数据】:{}", request);
                String result = HttpUtil.post(wallet, request);
                //为空重新查询
                if (StringUtils.isEmpty(result)) {
                    continue;
                }
               // logger.info("【请求钱包返回数据】:{}", result);
                resultJson = JSONObject.parseObject(result);
                if (resultJson != null) {
                    JSONObject errorJson = resultJson.getJSONObject("error");
                    if (errorJson != null) {
                        throw new WalletException(errorJson.toJSONString());
                    } else {
                        resultStr = resultJson.getString("result");
                        if(StringUtils.isEmpty(resultStr)){
                            logger.error("【请求钱包发送数据】:{}", result);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("请求钱包处理失败", e);
                throw new WalletException("请求钱包处理失败");
            }

        }

        return resultStr;
    }


    /**
     * 获取当前块
     *
     * @return
     */
    public Long getBlockCount() {
        List<Object> params = new ArrayList<>();
        String result = requestWallet("getblockcount", params);
        return Long.valueOf(result);
    }

    /**
     * 根据块好查询哈希
     *
     * @param blockNum
     * @return
     */
    public String getBlockHash(Long blockNum) {
        List<Object> params = new ArrayList<>();
        params.add(blockNum);
        String result = requestWallet("getblockhash", params);
        return result;
    }

    /**
     * 根据块hash查询块头信息
     *
     * @param hash
     * @return
     */
    public JSONObject getBlock(String hash) {
        List<Object> params = new ArrayList<>();
        params.add(hash);
        String result = requestWallet("getblock", params);
        return JSONObject.parseObject(result);
    }

    /**
     * 根据交易id查询交易信息
     *
     * @param trxId
     * @return
     */
    public JSONObject getRawTransaction(String trxId) {
        List<Object> params = new ArrayList<>();
        params.add(trxId);
        params.add(2);
        String result = requestWallet("getrawtransaction", params);
        return JSONObject.parseObject(result);
    }
    public JSONObject getRawTransaction(String trxId,HttpURLConnection httpConnection,OutputStreamWriter outputStreamWriter) {
        List<Object> params = new ArrayList<>();
        params.add(trxId);
        params.add(2);
        String result = requestWallet("getrawtransaction", params);
        return JSONObject.parseObject(result);
    }


    public JSONObject getTransaction(String trxId) {
        List<Object> params = new ArrayList<>();
        params.add(trxId);
        String result = requestWallet("gettransaction", params);
        return JSONObject.parseObject(result);
    }

    public JSONArray getRawMempool() {
        List<Object> params = new ArrayList<>();
        String result = requestWallet("getrawmempool", params);
        if (StringUtils.isEmpty(result)) {
            return null;
        } else {
            return JSONArray.parseArray(result);
        }
    }

    public JSONObject getMiningInfo() {
        List<Object> params = new ArrayList<>();
        String result = requestWallet("getmininginfo", params);
        return JSONObject.parseObject(result);
    }

    public HttpURLConnection getHttpURLConnection(){
        HttpURLConnection httpConnection = null;
        try {
            URL url;
            url = new URL(wallet.getUrl());
            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type",
                    "application/json");
            httpConnection.setRequestProperty("Connection", "close");
            httpConnection.setConnectTimeout(10000);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            String namepasswd = wallet.getUsername()+":"+wallet.getPassword();
            String authStr = new String(Base64.encode(namepasswd.getBytes(Charset.forName("ISO8859-1"))));
            httpConnection.setRequestProperty("Authorization", "Basic " + authStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpConnection;
    }

    public OutputStreamWriter getOutputStreamWriter(HttpURLConnection httpConnection){
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(httpConnection.getOutputStream(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStreamWriter;
    }

}
