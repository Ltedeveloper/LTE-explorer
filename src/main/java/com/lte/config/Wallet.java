package com.lte.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mayakui on 2018/1/11 0011.
 *  钱包配置
 */
@Configuration
@ConfigurationProperties(prefix = "wallet",ignoreUnknownFields = false)
public class Wallet {

    private String url;

    //钱包用户名
    private String username;

    //钱包密码
    private String password;

    private int timeout;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
