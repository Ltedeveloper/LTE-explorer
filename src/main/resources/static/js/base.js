function getbtcvalue(a,x) {
//;
    if(x){
        var a = getxbit(a,x)
    }
    neg = !1;
    a = String(a);
    "-" == a[0] && (neg = !0, a = a.substring(1));
    for (a = 8 < a.length ? a.substring(0, a.length - 8) + "." + a.substring(a.length - 8) : "0." + "0000000".substring(0, 8 - a.length) + a; 0 < a.length; )
        if ("0" == a.charAt(a.length - 1))
            a = a.substring(0, a.length - 1);
        else {
            "." == a.charAt(a.length - 1) && (a = a.substring(0, a.length - 1));
            break
        }
    neg && (a = "-" + a);
    return a;
}
function getxbit(a,x){
    var mod = a%bigNumPow(10,x);
    return a-mod;
}

function bigNumMulti(a,b){
    var p = a.match(/\d{1,4}/g).reverse();
    var q = b.match(/\d{1,4}/g).reverse();
    var f1 = 0;
    var result = "0";

    for(var i = 0; i < p.length; i++){
        var f2 = 0;
        for(var j = 0; j < q.length; j++){
            var t = (p[i]|0)*(q[j]|0);
            t += new Array(f1+f2+1).join("0");
            result = bigNumAdd(result, t);
            f2 += q[j].length;
        }
        f1 += p[i].length;
    }
    return result;
}
function bigNumAdd(a,b){
    var m = a.split('').reverse();
    var n = b.split('').reverse();
    var ret = [];
    var s = 0;

    for(var i = 0; i < a.length || i < b.length; i++){
        var t = (m[i]|0) + (n[i]|0) + s;

        ret.push(t%10);
        s = (t/10)|0;
    }
    if(s){
        ret.push(s);
    }
    return ret.reverse().join('');
}

function bigNumPow(a,b){
    var ret = "1";
    for(var i = 0; i < b; i++){
        ret = bigNumMulti(ret,a.toString());
    }
    return ret;
}

$(".coin-box").mouseover(function(){
    $(".select").show();
}).mouseout(function(){
    $(".select").hide();
})


function nowTime(op){
    var myHours="";
    var myMinutes="";
    var mySeconds="";
    mydate=new Date();
    myHours = mydate.getHours();
    myMinutes = parseInt(mydate.getMinutes())<10?"0"+mydate.getMinutes():mydate.getMinutes();
    mySeconds = parseInt(mydate.getSeconds())<10?"0"+mydate.getSeconds():mydate.getSeconds();
    $("#nowtime").html(myHours+"："+myMinutes+"："+mySeconds)
    get_count()
    t = setTimeout("nowTime()",1000);
}

function format (num) {
    return (num.toFixed(0) + '').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$& , ');
}

function get_count(){
    $.getJSON("http://open.qukuai.com/info/txs?key=567i3rfqsn4fNoya8Y3GnQfefqwefqwefwqf4&callback=?",function(data){
        $("#map_tx").text(format(data.tx_count))
    })
}

function preload(files){
    if(Object.prototype.toString.call(files) !== "[object Array]"){return;}
    var obj = null,
        ie  = '\v'=='v';

    for(var i = 0, l = files.length; i < l; i ++){
        obj = document.createElement('script');
        obj.src = files[i];
        document.body.appendChild(obj);
    }
}
