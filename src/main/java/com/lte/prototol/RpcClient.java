package com.lte.prototol;

/**
 * Created by mayakui on 2018/1/9 0009.
 */
public class RpcClient {

    //钱包地址
    private String url;

    //钱包用户名
    private String name;

    //钱包密码
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
