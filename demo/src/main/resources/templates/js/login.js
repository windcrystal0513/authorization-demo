function getDate() {
    var date = new Date(),
        nowYear = date.getFullYear(),
        nowMonth = date.getMonth() + 1,  //注意getMonth从0开始，getDay()也是(此时0代表星期日)
        nowDay = date.getDate(),
        nowHour = date.getHours(),
        nowMinute = date.getMinutes(),
        nowSecond = date.getSeconds(),
        weekday = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
        nowWeek = weekday[date.getDay()];
    return nowYear + '年' + nowMonth + '月' + nowDay + '日' + nowHour + '时' + nowMinute + '分' + nowSecond + '秒-' + nowWeek;
}


$(document).ready(function () {

    $('#register').click(function () {

        //注册
        $.ajax({
            type: "post",
            url: ip + "/user/register",
            data: JSON.stringify({
                userName: $("#username").val(),
                phoneNumber: $("#phonenumber").val(),
                email: $("#email").val(),
                password: $("#password").val()
            }),
            dataType: 'json',
            contentType: 'application/json',
            success: function (result) {
                if (result["code"] == 200) {
                    alert("注册成功");
                    window.location.href = "/index.html";
                }
            },
            error: function (result) {
                alert(result["responseJSON"]["msg"]);
            }
        });

    });


    $('#clientRegister').click(function () {
        //client注册
        $.ajax({
            type: "post",
            url: ip + "/oauth2.0/clientRegister",
            data: {
                clientName: $("#clientname").val(),
                redirectUri: $("#redirecturi").val(),
                description: $("#description").val()
            },
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                if (result["code"] == 200) {
                    alert("注册成功");
                    window.location.href = "/index.html";
                }
            },
            error: function (result) {
                alert(result["responseJSON"]["msg"]);
            }
        });

    });


    $('#login').click(function () {

        //登录验证
        $.ajax({
            type: "get",
            url: ip + "/user/login",
            data: {userName: $("#u").val(), password: $("#p").val()},
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                if (result["code"] == 200) {
                    //写cookie，记录
                    var JWT = {};
                    JWT.jwt = result["data"];
                    JWT = JSON.stringify(JWT);
                    setCookie("jwt", JWT, "h1");

                    // alert("点击确定将自动跳转，无跳转请自己打开原来的页面")

                    window.location.href = "/index.html";
                }

            },
            error: function (result) {
                alert("用户未注册或者密码错误");
            }
        });

    });

    $('#miaomiao').click(function () {

        //get authorizationcode
        $.ajax({
            type: "post",
            url: ip + "/oauth2.0/authorizePage",
            data: {
                redirectUri: "http%3A%2F%2Flocalhost%3A8080%2Foauth2.0%2Fauthorize%3Fclient_id%3D057ILIhKGv6rnKNsB68aEHfb%26scope%3Dbasic%26response_type%3Dcode%26state%3DAB1357%26redirect_uri%3Dhttp%3A%2F%2Flocalhost%3A8080%2FgetAuthorizationCode.html"
                , client_id: "057ILIhKGv6rnKNsB68aEHfb"
                , scope: "basic"
            },
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                return;
            },
            error: function (result) {

            }
        });

    });

    $('#perpareToEat').click(function () {
        //get access token
        var reg = new RegExp("(^|&)" + "code" + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var context = "";
        if (r != null)
            context = r[2];
        reg = null;
        r = null;
        context == null || context == "" || context == "undefined" ? "" : context;
        $.ajax({
            type: "post",
            url: ip + "/oauth2.0/token",
            data: {
                grant_type: "authorization_code"
                , code: context
                , client_id: "057ILIhKGv6rnKNsB68aEHfb"
                , client_secret: "vzMJW2HiFADbqXbKW5PHzIO8beFyKH6d"
                , redirect_uri: "http://localhost:8080/getAccessToken.html"
            },
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                if (result["code"] == 200) {
                    window.location.href = "/getAccessToken.html?access_token=" + result["data"]["access_token"] + "&refresh_token=" + result["data"]["refresh_token"]
                        + "&expires_in=" + result["data"]["expires_in"] + "&scope=" + result["data"]["scope"];
                }
            },
            error: function (result) {
                alert("You get an error!");
            }
        });

    });


    $('#Eat').click(function () {
        //get user info
        var reg = new RegExp("(^|&)" + "access_token" + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var context = "";
        if (r != null)
            context = r[2];
        reg = null;
        r = null;
        context == null || context == "" || context == "undefined" ? "" : context;
        $.ajax({
            type: "get",
            url: ip + "/api/users/getInfo",
            data: {access_token: context},
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                alert("You get a fish! The name is:" + result["username"] + ", the mobile is:" + result["mobile"] + ".");
            },
            error: function (result) {
                alert("You get an error!");
            }
        });

    });

    $('#refresh').click(function () {
        //get user info
        var reg = new RegExp("(^|&)" + "refresh_token" + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var context = "";
        if (r != null)
            context = r[2];
        reg = null;
        r = null;
        context == null || context == "" || context == "undefined" ? "" : context;
        $.ajax({
            type: "get",
            url: ip + "/oauth2.0/refreshToken",
            data: {refresh_token: context},
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
                if (result["code"] == 200) {
                    window.location.href = "/getAccessToken.html?access_token=" + result["data"]["access_token"] + "&refresh_token="
                        + result["data"]["refresh_token"] + "&expires_in=" + result["data"]["expires_in"] + "&scope=" + result["data"]["scope"];
                }
            },
            error: function (result) {
                alert("You get an error!");
            }
        });

    });


});


function setCookie(name, value, time) {
    var strsec = getsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strsec * 1);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
}

function getsec(str) {
    // alert(str);
    var str1 = str.substring(1, str.length) * 1;
    var str2 = str.substring(0, 1);
    if (str2 == "s") {
        return str1 * 1000;
    } else if (str2 == "h") {
        return str1 * 60 * 60 * 1000;
    } else if (str2 == "d") {
        return str1 * 24 * 60 * 60 * 1000;
    }
}

//这是有设定过期时间的使用示例：
//s20是代表20秒
//h是指小时，如12小时则是：h12
//d是天数，30天则：d30
// setCookie("name","hayden","s20");
// 
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
        return unescape(arr[2]);
    } else
        return "";
}