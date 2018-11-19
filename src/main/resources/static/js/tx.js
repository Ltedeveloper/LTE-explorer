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



function loadtx(data, raw) {


    var tx_span_vi = '<div class="left-top"><div class="center-l" id="left_info"></div></div><div class="tx-mord-box" id="left_more"></div>'
    $("#left").html(tx_span_vi);

    if (data.cb) {
        $("#left_info").html('<div class="tx-addr">'+'Newly_Generated_Coins'+'</div>')
    }
    else{
        var x = 0;
        for(var j in data.vi) {
            if(data.vi[j]) {
                var tx_span_vi_a = '';
                if(data.vi[j].a == null) {
                    tx_span_vi_a = '<div class="tx-addr out">'+'无法获取地址'+'</div>';
                    $("#left_info").append(tx_span_vi_a);
                }
                else if(data.vi[j].a.length == 1) {
                    tx_span_vi_a = '<div class="tx-addr"><div class="subaddr"><a href="../address/' + data.vi[j].a[0] + '">' + data.vi[j].a[0] + '</a></div><span class="details-word">（<span class="in"><a class="in" href="../tx/' + data.vi[j].t + '">'+'Output'+'</a></span>）</span></div><div class="tx-num">' + getbtcvalue(data.vi[j].v) + ' '+coin_unit+'</div>';
                }
                else if(data.vi[j].a.length > 1) {
                    tx_span_vi_a = '<div class="tx-addr"><div style="float:left">[&nbsp;</div> '
                    for(var k in data.vi[j].a) {
                        tx_span_vi_a = tx_span_vi_a + '<div class="subaddr"><a href="'+coin_url+'/address/' + data.vi[j].a[k] + '">' + data.vi[j].a[k] + '</a></div>';
                        if(k != data.vi[j].a.length - 1) {
                            tx_span_vi_a = tx_span_vi_a + "</div>";
                        }else{

                            tx_span_vi_a = tx_span_vi_a + ' &nbsp;]<span class="details-word">（<span class="in"><a class="in" href="'+coin_url+'/tx/' + data.vi[j].t + '">'+'Output'+'</a></span>）</span></div><div class="tx-num">' + getbtcvalue(data.vi[j].v) + ' '+coin_unit+'</div>'
                        }

                        if(x < 5){
                            $("#left_info").append(tx_span_vi_a);
                        }else{
                            $("#left_more").append(tx_span_vi_a);
                        }
                        x++;
                        var tx_span_vi_a = '<div class="tx-addr">';
                    }
                }
                if(data.vi[j].a != null) {
                    if(data.vi[j].a.length < 2) {
                        if(x < 5){
                            $("#left_info").append(tx_span_vi_a);
                        }else{
                            $("#left_more").append(tx_span_vi_a);
                        }
                        x++;
                    }
                }
            }
        }
    }

    if(x > 5){
        $("#left").append('<div class="more-box"><div class="tx-addr"><a href="javascript:;" class="tx-more">'+'更多'+'</a></div></div>');
    }


    var tx_span_vo = '<div class="right-top"><div class="center-r" id="right_info"></div></div><div class="tx-mord-box" id="right_more"></div>'
    $("#right").html(tx_span_vo);
    var x = 0;
    //console.log(data.vo);
    for(var j in data.vo) {
        var tx_span_vo_a = '';
        var tx_r_info = "";
        if(data.vo[j].tt) {
            tx_r_info = '<span class="details-word">（<span class="out"><a class="out" href="'+coin_url+'/tx/' + data.vo[j].tt + '">'+'Spent'+'</a></span>）</span></div><div class="tx-num fwb in">' +getbtcvalue(data.vo[j].v) + ' '+coin_unit+'</div>';
        }
        else if(data.vo[j].tts) {
            tx_r_info = '<span class="details-word">（<span class="out">';
            for(var k=0;k<data.vo[j].tts.length;k++) {
                if(k>0) {
                    tx_r_info = tx_r_info + ','
                }
                tx_r_info = tx_r_info + '<a class="out" href="'+coin_url+'/tx/' + data.vo[j].tts[k] + '">'+'Spent'+'</a>';
            }
            tx_r_info = tx_r_info + '</span>）</span></div><div class="tx-num fwb in">' +getbtcvalue(data.vo[j].v) + ' '+coin_unit+'</div>';
        }
        else{
            tx_r_info = '<span class="details-word">（<span class="in">'+'Unspent'+'</span>）</span></div><div class="tx-num fwb in">' +getbtcvalue(data.vo[j].v) + ' '+coin_unit+'</div>';
        }

        if(data.vo[j].a == null || data.vo[j].a.length == 0) {
            tx_span_vo_a = '<div class="tx-addr out">'+'Cannot_address'+ '<span style="color:#93a3a2">' + " - " +'Decoded'+ ' ' + decode(hex2a(data.vo[j].asm)) +'</span></div>';
        }
        else if(data.vo[j].a.length == 1) {
            tx_span_vo_a = '<div class="tx-addr"><div class="subaddr"><a href="'+coin_url+'/address/' + data.vo[j].a[0] + '">' + data.vo[j].a[0] + '</a></div>' + tx_r_info;
        }
        else if(data.vo[j].a.length > 1) {

            tx_span_vo_a = '<div class="tx-addr"> <div style="float:left">[&nbsp;</div>'
            //console.log(data.vo[j].a);
            for(var k in data.vo[j].a) {
                tx_span_vo_a = tx_span_vo_a + '<div class="subaddr"><a href="'+coin_url+'/address/' + data.vo[j].a[k] + '">' + data.vo[j].a[k] + '</a></div>';
                if(k != data.vo[j].a.length - 1) {
                    tx_span_vo_a = tx_span_vo_a + "</div>";
                }else{

                    tx_span_vo_a = tx_span_vo_a +' &nbsp;]'+ tx_r_info ;
                }
                if(x < 5){
                    $("#right_info").append(tx_span_vo_a);
                }else{
                    $("#right_more").append(tx_span_vo_a);
                }
                x++;
                var tx_span_vo_a = '<div class="tx-addr">';
            }
        }
        if(data.vo[j].a && data.vo[j].a.length < 2) {
            if(x < 5){
                $("#right_info").append(tx_span_vo_a);
            }else{
                $("#right_more").append(tx_span_vo_a);
            }
            x++;
        }else{
            if(x < 5){
                $("#right_info").append(tx_span_vo_a);
            }else{
                $("#right_more").append(tx_span_vo_a);
            }
            x++;
        }
    }
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
    $.getJSON("http://api.qukuai.com:8090/tx/" + $("#tx_url").text() + "&callback=?", function(data) {
        if(data.error && data.error==404) {
            if(data.block) {
                top.location.href="../block/"+data.block;
                return;
            }
            else {
                $("#all").html('<div class="no-tx"><img src="../images/no_tx.png"><div class="no-title">'+'交易未发现'+'</div><div class="no-tis">'+'Can_Be_Later_T'+'<a href="javascript:location.reload();void(0);">'+'刷新'+'</a>'+'Try_again'+'</div></div>');
                return;
            }
        }
            coin_name = 'Ltc';
            coin_unit = 'LTC';
            coin_url = '/search/ltc';

        var tx_info = $('#tx_info');
        tx_info.append('<div class="info-left">'+'哈希'+'</div><div class="info-right ddd">' + data.id +'</div>')
        if(data.tg) {
            var tgs = [];
            for(var i=0;i<data.tg.length;i++) {
                var result = data.tg[i].tgurl.split("/");
                //console.log(result);
                tx_info.append('<div class="info-left">'+'注意'+'</div><div class="info-right"><a href="' + result[0] + '//' + result[2] + '/search/tag' + '" target="_blank">'+ data.tg[i].tg +'</a>（'+'By'+'<a href="/search/address/'+ data.tg[i].a +'">'+ data.tg[i].a +'</a>'+'Add'+'，'+'Authenticated'+'<a href="javascript:;" id="sign'+i+'">'+'Sign'+'</a><img src="../images/tx_ok.png">）<div id="tgs'+i+'">'+data.tg[i].tgs+'</div></div>');
                sign_sel = "#sign"+i;
                tgs_sel = "#tgs"+i;
                $(tgs_sel).hide();
                $(tgs_sel).css("line-height","14px")
                $(sign_sel).click(function(){
                    $(tgs_sel).toggle();
                });
                $(sign_sel).mouseover(function(){
                    $(sign_sel).css("cursor","pointer");
                });
            }
        }
        tx_info.append('<div class="info-left">'+'转出总额'+'</div><div class="info-right fwb">'+ getbtcvalue(data.v) + " "+coin_unit+'</div>')
        if(!data.cb){
            tx_info.append('<div class="info-left">'+'手续费'+'</div><div class="info-right fwb">'+ getbtcvalue(data.vf) + " "+coin_unit+'</div>')
        }
        if(data.bh!=null && data.bh>=0) {
            var inblock = '<a href="../block/' + data.bhash + '">' + data.bh + '</a> (' + data.bt + ')';
        }
        else {
            var	inblock = '未发现';
        }
        tx_info.append('<div class="info-left">'+'包含区块'+'</div><div class="info-right">'+inblock+'</div>')
        if(data.cf == 0) {
            var ok_img = 'images/tx_alert.png';
        }
        else {
            var ok_img = 'images/tx_ok.png';
        }

        tx_info.append('<div class="info-left">'+'接收时间'+'</div><div class="info-right">' + data.r + '</div>')
        tx_info.append('<div class="info-left">'+'确认次数'+'</div><div class="info-right fwb"><div class="ok_num">'+data.cf+'</div> <img src="'+ok_img+'" class="ok_img"></div>')
        if(data.cb){
            coinbase = br64(data.cb);
            coinbase = coinbase + '（'+'UnCode'+'）' + br64(decode(hex2a(data.cb)));
            tx_info.append('<div class="info-left">Coinbase</div><div class="info-right backline">' + coinbase + '</div>')
        }


        var dataraw = {};
        $.extend(true, dataraw, data);

        if(!data.cb) {
            var vi;
            var sum
            for(var i=0;i<data.vi.length;i++) {
                if(data.vi[i]) {
                    for(var j=i+1;j<data.vi.length;j++) {
                        if(data.vi[j] && data.vi[j].a.length >0 && data.vi[j].a.length == data.vi[i].a.length) {
                            var match = true;
                            for(var k in data.vi[j].a) {
                                if(data.vi[j].a[k] != data.vi[i].a[k]) {
                                    match = false;
                                    break;
                                }
                            }
                            if(match) {

                                data.vi[i].v = data.vi[j].v + data.vi[i].v;
                                delete data.vi[j];
                            }
                        }
                    }
                }
            }
        }
        var isdetails= getCookie('details');
        if(isdetails == 'yes'){
            $("#det").html('Basic')
            var raw = true;
            loadtx(dataraw, raw);
            $(".details-word").show();
        }else{
            $("#det").html('Detail')
            var raw = false;
            loadtx(data, raw);
            $(".details-word").hide();
        }


        $("#det").click(function(){
            if(raw) {
                raw = false;
                loadtx(data, raw);
                $("#det").html('Detail')
                setCookie('details','')

                $(".details-word").hide()
            }
            else {
                raw = true;
                loadtx(dataraw, raw);
                $("#det").html('Basic')
                setCookie('details','yes')

                $(".details-word").show()
            }

        });

    });
});

$(window).resize(function() {
    ch()
})
