
function loadtx(blockNum, start) {
    $.getJSON("../blocktrx/" + blockNum + "?start=" + start,function(blocktx){
        if(start > 0){
            $("#more").remove()
        }
        for(var i=0;i<blocktx.length;i++) {
            var tx_head = '<div class="tx-hash"><div class="hash-left"><a href="../transactionInfo/' + blocktx[i]["trxId"] + '">' + blocktx[i]["trxId"]
                + '</a></div><div class="tx-time hidden-xs">' + blocktx[i]["trxTime"] + '</div></div>';
            var tx_html = $('<div class="tx-boxs"></div>').append(tx_head);
            var tx_info_row = $('<div class="addr-addrs"></div>');

            var tx_vi_info = $('<div class="addrs-left"></div>');
            var tx_vi_top5 = $('<div class="center-l"></div>');
            var tx_vi_more = $('<div class="tx-mord-box"></div>');
            var x = 0;
            if (blocktx[i].trxType==0) {
                tx_vi_top5.append('<div class="tx-addr">'+'出块奖励'+'</div>');
            }
            else{
                var vinList = blocktx[i].vinList;
                for(var j=0;j<vinList.length;j++) {
                    var tx_vi = "";
                    var tx_span_vi_a = '<div class="tx-addr"><div class="subaddr-p"><a href="../addressInfo/' + vinList[j].address + '">' + vinList[j].address + '</a></div></div>';
                    tx_vi = tx_vi + tx_span_vi_a;
                    var tx_val = '<div class="tx-num">' +vinList[j].amount +' ' +'LTE</div>';
                    tx_vi = tx_vi + tx_val;
                    if(x < 5){
                        tx_vi_top5.append(tx_vi);
                    }else{
                        tx_vi_more.append(tx_vi);
                    }
                    x++;
                }
            }

            var tx_vi_top5_wrap = $('<div class="left-top"></div>');
            tx_vi_top5_wrap.append(tx_vi_top5);
            tx_vi_info.append(tx_vi_top5_wrap);
            if(x > 5){
                tx_vi_info.append(tx_vi_more);
                tx_vi_info.append('<div class="more-box"><div class="tx-addr"><a href="javascript:;" class="tx-more">'+'更多'+'</a></div></div>');
            }
            tx_info_row.append(tx_vi_info);
            $('<div class="addrs-img"><img src="../images/tx_jt.png"></div>').appendTo(tx_info_row);
            var tx_vo_info = $('<div class="addrs-right"></div>');
            var tx_vo_top5 = $('<div class="center-r"></div>');
            var tx_vo_more = $('<div class="tx-mord-box"></div>');
            x = 0

            var voutList = blocktx[i].voutList;
            for(var j=0;j<voutList.length;j++) {
                var tx_vo = "";
                var tx_span_vo_a = '<div class="tx-addr"><div class="subaddr-p"><a href="../addressInfo/' + voutList[j].address + '">' + voutList[j].address + '</a></div></div>';
                tx_vo = tx_vo + tx_span_vo_a;

                var tx_val = '';
                if(voutList[j].isUse == 0){
                    tx_val = '<div class="tx-num in fwb">' +voutList[j].amount +' ' +'LTE <span style="color:#000000">&nbsp;&nbsp;(未使用)</span></div>';
                }else{
                    tx_val = '<div class="tx-num in fwb">' +voutList[j].amount +' ' +'LTE <span style="color:#000000">&nbsp;&nbsp;(已使用)</span></div>';
                }

                tx_vo = tx_vo + tx_val;
                if(x < 5){
                    tx_vo_top5.append(tx_vo);
                }else{
                    tx_vo_more.append(tx_vo);
                }
                x++;
            }

            var tx_vo_top5_wrap = $('<div class="right-top"></div>');
            tx_vo_top5_wrap.append(tx_vo_top5);
            tx_vo_info.append(tx_vo_top5_wrap);
            if(x > 5){
                tx_vo_info.append(tx_vo_more);
                tx_vo_info.append('<div class="more-box"><div class="tx-addr"><a href="javascript:;" class="tx-more">'+'更多'+'</a></div></div>');
            }
            tx_info_row.append(tx_vo_info);
            var addr_wrap = $('<div class="addr-box"></div>').append(tx_info_row);
            addr_wrap.append('<div class="tx-time-p visible-xs">' + blocktx[i]["trxTime"] + '</div>')
            tx_html.append(addr_wrap);
            var tx_wrap = $('<div class="addrs mt20"></div>').append(tx_html);
            $("#block_tx").append(tx_wrap);
            $("#block_tx").append('<div class="clearfix"></div>');
        }
        if(blocktx.length == 25) {
            $("#block_tx").append('<a id="more" class="more-btn">'+'更多'+'</a>');
            $("#more").click(function(){
                $("#more").text('加载中'+"...");
                start = start + 25;
                loadtx(blockNum, start);
            });
            $("#more").mouseover(function(){
                $("#more").css({"cursor":"pointer","background-color":"#eeeeee"});
            }).mouseout(function(){
                $("#more").css("background-color","#f9f9f9");
            });
        }
    }).done(function(){ adjustCSS(); });
}


function ch(){
    var width = $(window).width();
    if(width > 983){
        $(".center-l").each(function(){
            var numl = $(this).find('.tx-addr').length
            var lh = 0 - numl * 10 ;
            $(this).css("margin-top",lh);
            $(this).parent().css("height",100);
        });

        $(".center-r").each(function(){
            var numr = $(this).find('.tx-addr').length
            var rh = 0 - numr * 10 ;
            $(this).css("margin-top",rh);
            $(this).parent().css("height",100);
        });
    }else{
        $(".center-l").each(function(){
            var numl = $(this).find('.tx-addr').length
            $(this).css("margin-top",0);
            $(this).parent().css("height",numl*20);
        });
        $(".center-r").each(function(){
            var numr = $(this).find('.tx-addr').length
            $(this).css("margin-top",0);
            $(this).parent().css("height",numr*20);
        });
    }
}
function adjustCSS(){
    //adjust css
    ch();

    $(".tx-more").click(function(){
        var obj = $(this).parent().parent().parent().parent()
        var dis = obj.find(".tx-mord-box").css('display');
        if(dis == 'none'){
            obj.find(".tx-more").html('收起')
        }else{
            obj.find(".tx-more").html('更多')
        }
        obj.find(".tx-mord-box").slideToggle(200);
    })
}

$(document).ready(function(){

    $.ajax({
        type: "POST",
        url: "../block/"+$("#block_num").text(),
        dataType: "json",
        success: function(data) {
            var block_title = '<div class="all-title">'+'区块'+'</div>';
            var block_link = $('<div class="next-block"></div>');
            if(data.previousBlockHash !== undefined){
                if(data.blockNum>1){
                    block_link.append('<a href="../blockInfo/'+(data.blockNum-1)+'" id="link_prev_block">'+'上一个'+'</a>');
                }
            }
            block_link.append('<span id="block_height"></span>');
            $("#all div.all-title-box").append(block_title);
            $("#all div.all-title-box").append(block_link);

            $('#block_height').text(data.blockNum);

            $("#block_info").append("<div class='info-left'>"+'区块高度'+"</div><div class='info-right fwb'>" + data.blockNum + "<span class='kuo'></span></div>" );
            $("#block_info").append("<div class='info-left'>"+'交易总数'+"</div><div class='info-right'>" + data.trxNum + "</div>" );

            $("#block_info").append("<div class='info-left'>"+'难度'+"</div><div class='info-right'>" + data.difficulty + "</div>" );
            $("#block_info").append("<div class='info-left'>"+'随机数'+"</div><div class='info-right'>" + data.nonce + "</div>" );
            $("#block_info").append("<div class='info-left'>"+'哈希'+"</div><div class='info-right ddd '>" + data.hash + "</div>" );
            $("#block_info").append("<div class='info-left'>"+'时间'+"</div><div class='info-right'>" + data.blockTime + "</div>" );

            loadtx(data.blockNum, 0);
            $(window).resize(function() {
                ch()
            })
        }
    });

    adjustCSS();
});


