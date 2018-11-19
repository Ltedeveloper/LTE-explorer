package com.lte.util;

/**
 * Created by mayakui on 2018/1/12 0012.
 */
public class Constant {

    public static final Integer TRX_REWARD_TYPE = 0;
    public static final Integer TRX_TRANSFER_TYPE = 1;

    public static final Integer ADDRESS_TYPE_FROM = 1;
    public static final Integer ADDRESS_TYPE_TO = 2;

    //vout未使用
    public static final Integer VOUT_UNUSE = 0;
    //vout已使用
    public static final Integer VOUT_USE = 1;

    //批量处理最大数
    public static final Integer BATCH_LENGTH =10000;

    //钱包接口请求失败最大次数
    public static final Integer HTTP_RESNED_MAX = 3;

    public static final Integer ADDRESS_VALID = 1;
    public static final Integer ADDRESS_INVALID = 2;
}
