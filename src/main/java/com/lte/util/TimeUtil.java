package com.lte.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/11 0011.
 */
public class TimeUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String longToString(long time){
        SimpleDateFormat sdf= new SimpleDateFormat(DEFAULT_FORMAT);
        Date dt = new Date(time);
        return sdf.format(dt);
    }

    public static String getCurrentTime(){
        SimpleDateFormat sdf= new SimpleDateFormat(DEFAULT_FORMAT);
        return sdf.format(new Date());
    }

    public static void main(String[] args){
        System.out.println(longToString(1519300388000L));
    }
}
