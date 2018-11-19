
coin = $.trim($("#nowcoin").text().toLowerCase());
coin_flag = coin.toUpperCase() == "BTC";
url = coin_flag ? "/search" : "/search/ltc";

function loadBlock() {
    $.ajax({
        type: "POST",
        url: "getNewBlockInfo",
        dataType: "json",
        success: function(data) {
            $("#blk_tb").html('<thead><tr><th class="t0">&nbsp;</th><th class="t1">区块高度</th><th class="t2">包含交易数</th><th class="t6">总发送额</th>'+
                '<th class="t3">数据量 (kB)</th><th class="t4">挖出方</th><th class="t5">生成时间</th><th class="t0">&nbsp;</th></tr></thead>');
            for(var i=0;i<data.length;i++) {
                $("#blk_tb").append('<tr><td class="t0">&nbsp;</td><td class="t1"><a href="blockInfo/'+data[i].blockNum+'">'+data[i].blockNum+'</a></td><td class="t2">'+data[i].trxNum+'</td><td class="t6">'+data[i].amount+ ' '+'LTE</td><td class="t3">'+data[i].blockSize/1000+'</td><td class="t4"><div>'+data[i].signee+'<div></td><td class="t5">'+data[i].blockTime+'</td><td class="t0">&nbsp;</td></tr>');
            }
            $('#blk_tb tr:last-child td').css("border","none");
            $("#blk_hd").css('display', 'block');
        }
    });
}

function loadTransaction() {
    $.ajax({
        type: "POST",
        url: "getNewTransactionInfo",
        dataType: "json",
        success: function(data) {
            if(data!=null && data.length>0){
                $("#tab-tx").html("");
                for(var i=0;i<data.length;i++)
                {
                    var fromAddress = null;
                    if(data[i].trxType!=0){
                        fromAddress=data[i].fromAddress
                    }else{
                        fromAddress='区块奖励';
                    }
                    var tx_info = $('<tr><td class="t0">&nbsp;</td><td class="t7"><div class="resp-text-hash">'+fromAddress+'</div></td><td class="t8"><img src="images/in.png" /></td><td class="t7-1"><div class="resp-text-hash">'+data[i].toAddress+'</div></td><td class="t9">'+data[i].trxTime+'</td><td class="t10">'+data[i].amount+ ' '+'LTE</td><td class="t11"><a href="transactionInfo/' + data[i].trxId + '">'+('详情')+'</a></td><td class="t00">&nbsp;</td></tr>').click(function(){
                       // var href = $(this).parent().parent().find('a').attr('href');
                       // window.location.href = href;
                    })
                    $("#tab-tx").append(tx_info);
                }
            }
        }
    });
}

function loadMiningInfo() {
    $.ajax({
        type: "POST",
        url: "miningInfo",
        dataType: "json",
        success: function(data) {
            if(data!=null){
                $("#blocks").html(data.blocks);
                $("#networkhashps").html(data.networkhashps);
                $("#pooledtx").html(data.pooledtx);
                $("#difficulty").html(data.difficulty);
            }
        }
    });
}

$(document).ready(function(){
    $('.search').focus(function(){
        $('.search-error').remove()
    })
    loadBlock();
    loadTransaction();
    loadMiningInfo();
    setInterval(function() {
        loadTransaction();
    }, 5000)
    setInterval(function() {
        loadBlock();
    }, 20000)
    setInterval(function() {
        loadMiningInfo();
    }, 10000)
});
