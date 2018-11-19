package com.lte.prototol;

/**
 * Created by mayakui on 2018/1/11 0011.
 */
public class WalletException extends RuntimeException{

    private String errCode;

    private String errMsg;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public WalletException(){

    }
    public WalletException(String errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public WalletException(String errMsg){
        this.errMsg = errMsg;
    }

}
