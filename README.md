# authorization-demo
springboot+springdata jpa+jwt实现注册登录以及模拟oauth2.0流程

自己手动实现的注册登录OAuth2.0授权的项目。

技术依赖
Spring Boot：项目基础架构

thymeleaf：用于构建测试页面模板

gradlew：依赖管理

环境依赖

JDK11+

MySQL8.0+

Redis集群


用户注册相关接口：

（1）用户注册：

注册页面：http://http://localhost:8080/register.html

接口地址：http://locahost:8080/user/register

请求header：Content-Type: application/json;charset=UTF-8

请求body：
{"userName":"wind","phoneNumber":"13456788765","email":"wind@miaomiao.com","password":"88888888"}

（2）登录地址：http://locahost:8080/login
![Image text](https://github.com/windcrystal0513/picture/blob/master/login.png)
接口地址：http://localhost:8080/user/login?userName=wind&password=88888888

登录成功后跳转到http://localhost:8080/index.html
![Image text](https://github.com/windcrystal0513/picture/blob/master/index.png)

OAuth2.0授权相关接口：

（1）客户端注册接口：

注册页面：http://http://localhost:8080/ClientRegister.html
![Image text](https://github.com/windcrystal0513/picture/blob/master/clientRegister.png)
接口地址：http://locahost:8080/oauth2.0/clientRegister?clientName=mimi的客户端&redirectUri=http://locahost:8080/index.html&description=这是mimi的客户端服务

（2）授权页面：http://localhost:8080/oauth2.0/authorizePage?redirectUri=http%3A%2F%2Flocalhost%3A8080%2Foauth2.0%2Fauthorize%3Fclient_id%3D057ILIhKGv6rnKNsB68aEHfb%26scope%3Dbasic%26response_type%3Dcode%26state%3DAB1357%26redirect_uri%3Dhttp%3A%2F%2Flocalhost%3A8080%2FgetAuthorizationCode.html&client_id=057ILIhKGv6rnKNsB68aEHfb&scope=basic
![Image text](https://github.com/windcrystal0513/picture/blob/master/authorizePage.png)

登录成功后在index页面点击图片，跳转到授权页面

点击授权后，触发接口http://localhost:8080/oauth2.0/agree

请求header：Content-Type: application/json;charset=UTF-8

请求body：
{"client_id":"057ILIhKGv6rnKNsB68aEHfb","scope":"basic"}

（3）获取Authorization Code：

接口地址：http://localhost:8080/oauth2.0/authorize?client_id=057ILIhKGv6rnKNsB68aEHfb&scope=basic&response_type=code&state=AB1357&redirect_uri=http://localhost:8080/getAuthorizationCode.html

获取到的authorization code会显示在跳转页面的url里
![Image text](https://github.com/windcrystal0513/picture/blob/master/getAuthorizationCode.png)


（4）通过Authorization Code获取Access Token：

点击网页上的想要吃鱼

接口地址：http://localhost:8080/oauth2.0/token?grant_type=authorization_code&code=ee2a52564ca0054e589d07e8c7f3faf6e52b336c&client_id=057ILIhKGv6rnKNsB68aEHfb&client_secret=vzMJW2HiFADbqXbKW5PHzIO8beFyKH6d&redirect_uri=http://localhost:8080/getAccessToken.html


返回如下：
{
    "code": 200,
    "msg": "成功",
    "data": {
        "access_token": "1.f3383b007a9e4ed19f788a6a75386756adaa54a9.2592000.1598080699",
        "refresh_token": "2.85a518c4e31d30a8874b87d98352a5b62f0c2942.31536000.1627024699",
        "expires_in": 2592000,
        "scope": "basic"
    }
}

页面跳转到http://http://localhost:8080/getAccessToken.html，
获取的access_token和refresh_token会显示在url里
![Image text](https://github.com/windcrystal0513/picture/blob/master/getAccessToken.png)


（5）通过Refresh Token刷新Access Token：

在页面点击换牌

接口地址：http://localhost:8080/oauth2.0/refreshToken?refresh_token=2.85a518c4e31d30a8874b87d98352a5b62f0c2942.31536000.1627024699

返回如下：
{
    "code": 200,
    "msg": "成功",
    "data": {
        "access_token": "1.cc7c96be2ee3fdd65add2baf2d0b138144b40c37.2592000.1598080848",
        "refresh_token": "2.85a518c4e31d30a8874b87d98352a5b62f0c2942.31536000.1627024699",
        "expires_in": 2592000,
        "scope": "basic"
    }
}

页面停留在http://http://localhost:8080/getAccessToken.html，url里的access_token等信息会进行更换

（6）通过Access Token获取用户信息：

点击页面上的吃鱼

接口地址：http://localhost:8080/api/users/getInfo?access_token=1.24f19bd83f85487b4dd7b400d59243c787fb8c60.2592000.1598080961

返回获取的用户信息，网页alert出返回的用户name以及mobile。
![Image text](https://github.com/windcrystal0513/picture/blob/master/GetFish.png)
