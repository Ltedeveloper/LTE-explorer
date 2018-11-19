package com.lte.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18 0018.
 */
public class Test {

    public static void main(String[] args){
        List<Integer> list = new ArrayList<Integer>();
        for(int i=0;i<100;i++){
            list.add(i);
        }
        for(int j=0;j<list.size();j=j+10){
            List<Integer> sub = null;
            if(j+10>list.size()){
                sub=list.subList(j,list.size());
            }else{
                sub = list.subList(j,j+10);
            }
            System.out.println(sub);
        }
}
}
