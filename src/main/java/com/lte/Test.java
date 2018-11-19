package com.lte;

/**
 * Created by Administrator on 2018/1/28 0028.
 */
public class Test {

    public static void main(String[] args) {

        String sql = "INSERT into block_trx (trx_id,block_num,from_address,to_address,hash,block_hash,coinbase,fee,amount,memo,trx_type,trx_time,created_at) SELECT * FROM block_transaction trx where _start<=trx.block_num and trx.block_num < _end;";

        //1360313
        for (int i = 1200000; i < 1400000; i = i + 100) {
            String result = sql.replace("_start", i + "");
            result = result.replace("_end", (i + 100) + "");
            System.out.println(result);
        }
    }
}
