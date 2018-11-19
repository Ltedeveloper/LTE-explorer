package com.lte.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;
import com.lte.config.MiningInfo;
import com.lte.config.RealData;
import com.lte.dao.entity.BlockInfo;
import com.lte.dao.entity.BlockTransaction;
import com.lte.service.BlockService;
import com.lte.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by mayakui on 2018/1/15 0015.
 */
@Controller
public class IndexController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private RealData realData;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("")
    public String welcome() {
        return "index";
    }

    /**
     * 区块页面跳转
     *
     * @param blockNum
     * @param model
     * @return
     */
    @RequestMapping("blockInfo/{blockNum}")
    public String blockInfo(@PathVariable Long blockNum, Model model) {
        model.addAttribute("blockNum", blockNum);
        return "blockInfo";
    }

    /**
     * 查询最新区块数据
     *
     * @return
     */
    @RequestMapping("getNewBlockInfo")
    @ResponseBody
    public String getNewBlockInfo() {
        return realData.getBlockInfo();
    }

    /**
     * 查询最新交易数据
     *
     * @return
     */
    @RequestMapping("getNewTransactionInfo")
    @ResponseBody
    public String getNewTransactionInfo() {
        return realData.getTransactionInfo();
    }

    /**
     * 查询区块信息
     *
     * @param blockNum
     * @return
     */
    @RequestMapping("block/{blockNum}")
    @ResponseBody
    public String block(@PathVariable Long blockNum) {
        BlockInfo blockInfo = blockService.queryBlockInfoByNum(blockNum);
        return JSONObject.toJSONString(blockInfo);
    }

    /**
     * 根据块号查询交易信息
     *
     * @param blockNum
     * @param start
     * @return
     */
    @RequestMapping("blocktrx/{blockNum}")
    @ResponseBody
    public List<BlockTransaction> blocktrx(@PathVariable Long blockNum, @RequestParam("start") int start) {
        List<BlockTransaction> transactionList = transactionService.selectTransactionByBlockNum(blockNum, start);
        return transactionList;
    }

    /**
     * 跳转交易页面
     *
     * @param trxId
     * @param model
     * @return
     */
    @RequestMapping("transactionInfo/{trxId}")
    public String transactionInfo(@PathVariable String trxId, Model model) {
        model.addAttribute("trxId", trxId);
        return "transactionInfo";
    }

    /**
     * 根据交易id查询交易详情
     *
     * @param trxId
     * @return
     */
    @RequestMapping("trx/{trxId}")
    @ResponseBody
    public JSONObject trx(@PathVariable String trxId) {
        BlockTransaction transaction = transactionService.selectByPrimaryKey(trxId);
        JSONObject json = new JSONObject();
        if (transaction == null) {
            json.put("error", 404);
        } else {
            json.put("trx", transaction);
        }
        return json;
    }

    /**
     * 跳转地址页面
     *
     * @param address
     * @param model
     * @return
     */
    @RequestMapping("addressInfo/{address}")
    public String addressInfo(@PathVariable String address, Model model) {
        model.addAttribute("address", address);
        return "addressInfo";
    }

    /**
     * 获取地址信息
     *
     * @param address
     * @return
     */
    @RequestMapping("address/{address}")
    @ResponseBody
    public JSONObject address(@PathVariable String address) {
        JSONObject data = blockService.queryAddressDetail(address);
        return data;
    }

    /**
     * 根据地质获取交易
     *
     * @param address
     * @param start
     * @return
     */
    @RequestMapping("addresstrx/{address}")
    @ResponseBody
    public List<BlockTransaction> addresstrx(@PathVariable String address, @RequestParam("start") int start) {
        List<BlockTransaction> data = transactionService.queryTransactionByAddress(address, start);
        return data;
    }


    @RequestMapping("searchs")
    public String search(@RequestParam("search") String search) {
        if (StringUtil.isEmpty(search)) {
            return "redirect:/";
        } else {
            search = search.trim();
        }
        if (search.matches("[0-9]{1,}")) {
            return "redirect:/blockInfo/" + search;
        }
        if (search.length() == 64) {
            return "redirect:/transactionInfo/" + search;
        }

        return "redirect:/addressInfo/" + search;
    }

    @RequestMapping("miningInfo")
    @ResponseBody
    public MiningInfo miningInfo() {
        return realData.getMiningInfo();
    }
}
