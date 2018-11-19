//写cookies
//

function setCookie(name,value)
{
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();

}

//读取cookies
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

    if(arr=document.cookie.match(reg))

        return (arr[2]);
    else
        return null;
}

//删除cookies
function delCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null)
        document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}

function getsec(str)
{
    var str1=str.substring(1,str.length)*1;
    var str2=str.substring(0,1);
    if (str2=="s")
    {
        return str1*1000;
    }
    else if (str2=="h")
    {
        return str1*60*60*1000;
    }
    else if (str2=="d")
    {
        return str1*24*60*60*1000;
    }
}
function decode(input) {
    var output = '';
    var i = 0;
    var c, c2, c3;
    c = c2 = c3 = 0;

    while (i < input.length) {
        c = input.charCodeAt(i);

        if (c < 128) {
            output += String.fromCharCode(c);
            i++;
        }
        else if ((c > 191) && (c < 224)) {
            c2 = input.charCodeAt(i + 1);
            uni = ((c & 31) << 6) | (c2 & 63);
            if((uni > 19968) && (uni < 40869)){
                output += String.fromCharCode(uni);
            } else {
                output += '○';
            }

            i += 2;
        }
        else {
            c2 = input.charCodeAt(i + 1);
            c3 = input.charCodeAt(i + 2);
            uni = ((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63);
            if((uni > 19968) && (uni < 40869)){
                output += String.fromCharCode(uni);
            } else {
                output += '○';
            }
            i += 3;
        }
    }

    return output;
}

function br64(str) {
    newstr = '';
    cuts = str.length/64;
    for(var l=0;l<cuts;l++) {
        newstr = newstr + str.substring(l*64,(l+1)*64) +'<br>';
    }
    newstr = newstr + str.substring(cuts*64);
    return newstr;
}

function hex2a(hex) {
    var str = '';
    for (var i = 0; i < hex.length; i += 2) {
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
    }
    return str;
}

function ch(){
    var width = $(window).width();
    var numl = $(".center-l .tx-addr").length;
    var numr = $(".center-r .tx-addr").length;
    if(width > 983){
        $(".left-top").css("height",100);
        $(".right-top").css("height",100);
        var lh = 0 - numl * 10 ;
        $(".center-l").css("margin-top",lh);

        var rh = 0 - numr * 10 ;
        $(".center-r").css("margin-top",rh);
    }else{
        $(".center-l").css("margin-top",0);
        $(".center-r").css("margin-top",0);
        $(".left-top").css("height",numl*20);
        $(".right-top").css("height",numr*20);
    }
}



function loadtx(data) {

    var tx_span_vi = '<div class="left-top"><div class="center-l" id="left_info"></div></div><div class="tx-mord-box" id="left_more"></div>'
    $("#left").html(tx_span_vi);

    if (data.trxType==0) {
        $("#left_info").html('<div class="tx-addr">'+'新区块奖励'+'</div>')
    }
    else{
        var x = 0;
        var vinList = data.vinList;

        var tx_span_vi_a = '';
        if(vinList == null) {
            tx_span_vi_a = '<div class="tx-addr out">'+'无法获取地址'+'</div>';
            $("#left_info").append(tx_span_vi_a);
            x++;
        }
        else if(vinList.length == 1) {
            tx_span_vi_a = '<div class="tx-addr"><div class="subaddr"><a href="../addressInfo/' + vinList[0].address + '">' + vinList[0].address + '</a></div><span class="details-word"></span></div><div class="tx-num">' + vinList[0].amount + ' '+'LTE</div>';
            x++;
        }else if(vinList.length > 1) {
            //tx_span_vi_a = '<div class="tx-addr"><div style="float:left"></div> '
            tx_span_vi_a = ''
            for(var k in vinList) {
                tx_span_vi_a = tx_span_vi_a + '<div class="tx-addr"> <div style="float:left"></div><div class="subaddr"><a href="'+'../addressInfo/' + vinList[k].address + '">' + vinList[k].address + '</a></div>';
                tx_span_vi_a = tx_span_vi_a + ' &nbsp;</div><div class="tx-num">' + vinList[k].amount + ' '+'LTE</div>';
                if(x < 5){
                    $("#left_info").append(tx_span_vi_a);
                }else{
                    $("#left_more").append(tx_span_vi_a);
                }
                x++;

                //var tx_span_vi_a = '<div class="tx-addr">';
                var tx_span_vi_a = '';
            }
        }
        if(x < 5){
            $("#left_info").append(tx_span_vi_a);
        }else{
            $("#left_more").append(tx_span_vi_a);
        }
    }

    if(x > 5){
        $("#left").append('<div class="more-box"><div class="tx-addr"><a href="javascript:;" class="tx-more">'+'更多'+'</a></div></div>');
    }

    var tx_span_vo = '<div class="right-top"><div class="center-r" id="right_info"></div></div><div class="tx-mord-box" id="right_more"></div>'
    $("#right").html(tx_span_vo);
    var x = 0;
    var voutList = data.voutList;

        var tx_span_vo_a = '';

        if(voutList == null || voutList.length == 0) {
            tx_span_vo_a = '<div class="tx-addr out">'+'无法解析的地址'+ '</div>';
        }
        else if(voutList.length == 1) {
            // if(voutList[0].isUse==0){
            //     tx_span_vo_a = '<div class="tx-addr"><div class="subaddr"><a href="../addressInfo/' + voutList[0].address + '">' + voutList[0].address + '</a></div>'
            //         +'<div class="tx-num fwb in">' +voutList[0].amount + ' '+'LTE<span style="color:#000000">&nbsp;&nbsp;(未使用)</span></div>';
            // }else{
            //     tx_span_vo_a = '<div class="tx-addr"><div class="subaddr"><a href="../addressInfo/' + voutList[0].address + '">' + voutList[0].address + '</a></div>'
            //         +'<div class="tx-num fwb in">' +voutList[0].amount + ' '+'LTE<span style="color:#000000">&nbsp;&nbsp;(已使用)</span></div>';
            // }
            tx_span_vo_a = '<div class="tx-addr"><div class="subaddr"><a href="../addressInfo/' + voutList[0].address + '">' + voutList[0].address + '</a></div>'
                +'<div class="tx-num fwb in">' +voutList[0].amount + ' '+'LTE</div>';
        }
        else if(voutList.length > 1) {

            tx_span_vo_a = ''
            for(var k in voutList) {
                tx_span_vo_a = tx_span_vo_a + '<div class="tx-addr"> <div style="float:left"></div><div class="subaddr"><a href="../addressInfo/' + voutList[k].address + '">' + voutList[k].address + '</a></div></div>'
                if(voutList[k].isUse==0){
                    tx_span_vo_a = tx_span_vo_a+'<div class="tx-num fwb"><span class="in">' +voutList[k].amount + ' LTE</span><span style="color:#000000">&nbsp;&nbsp;(未使用)</span></div>';
                }else{
                    tx_span_vo_a = tx_span_vo_a+'<div class="tx-num fwb"><span class="in">' +voutList[k].amount +' LTE</span><span style="color:#000000">&nbsp;&nbsp;(已使用)</span></div>';
                }

                if(x < 5){
                    $("#right_info").append(tx_span_vo_a);
                }else{
                    $("#right_more").append(tx_span_vo_a);
                }
                x++;
                var tx_span_vo_a = '';
            }
        }

        if(x < 5){
            $("#right_info").append(tx_span_vo_a);
        }else{
            $("#right_more").append(tx_span_vo_a);
        }
        x++;

    if(x > 5){
        $("#right").append('<div class="more-box"><div class="tx-addr"><a href="javascript:;" class="tx-more">'+'更多'+'</a></div></div>');
    }

    ch();
    $(".tx-more").click(function(){
        var dis = $(".tx-mord-box").css('display');
        if(dis == 'none'){
            $(".tx-more").html('收起')
        }else{
            $(".tx-more").html('更多')
        }
        $(".tx-mord-box").slideToggle(200);
    })
    //$("#tx_tx").append(tx_span_vo);
}

$(document).ready(function(){
    $.getJSON("../trx/" + $("#trxId").text(), function(data) {
        if(data.error && data.error==404) {
            $("#all").html('<div class="no-tx"><img src="../images/no_tx.png"><div class="no-title">'+'交易未发现'+'</div><div class="no-tis">'+'Can_Be_Later_T'+'<a href="javascript:location.reload();void(0);">'+'刷新'+'</a>'+'Try_again'+'</div></div>');
            return;
        }
        var trx = data.trx;
        var tx_info = $('#tx_info');
        tx_info.append('<div class="info-left">'+'交易id'+'</div><div class="info-right ddd">' + trx.trxId +'</div>')
        tx_info.append('<div class="info-left">'+'转出总额'+'</div><div class="info-right fwb">'+ trx.amount + " "+'LTE</div>')
        tx_info.append('<div class="info-left">'+'手续费'+'</div><div class="info-right fwb">'+ trx.fee + " "+'LTE</div>')

        if(trx.blockNum!=null && trx.blockNum!=0) {
            var inblock = '<a href="../blockInfo/' + trx.blockNum + '">' + trx.blockNum + '</a> ';
        }
        else {
            var	inblock = '未发现';
        }
        tx_info.append('<div class="info-left">'+'包含区块'+'</div><div class="info-right">'+inblock+'</div>');

        if(trx.confirms == 0) {
            var ok_img = '../images/tx_alert.png';
        }else {
            var ok_img = '../images/tx_ok.png';
        }

        tx_info.append('<div class="info-left">'+'接收时间'+'</div><div class="info-right">' + trx.trxTime + '</div>')
        tx_info.append('<div class="info-left">'+'确认次数'+'</div><div class="info-right fwb"><div class="ok_num">'+trx.confirms+'</div> <img src="'+ok_img+'" class="ok_img"></div>')
        if(trx.trxType==0){
            tx_info.append('<div class="info-left">Coinbase</div><div class="info-right backline">' + trx.coinbase + '</div>')
        }

        loadtx(trx);

    });
});

$(window).resize(function() {
    ch()
})
