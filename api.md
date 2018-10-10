# Authorization #
To access the API, use Authorization header for [BASIC AUTH](https://en.wikipedia.org/wiki/Basic_access_authentication#Client_side)

# API #
* [用户列表](#用户列表)
* [创建用户](#创建用户)
* [删除用户](#删除用户)
* [修改用户名称](#修改用户名称)
* [创建限速用户](#创建限速用户)
* [删除限速用户](#删除限速用户)

### 用户列表 ###
```GET /outline```

### 创建用户 ###
```POST /outline```
- location: user country

### 删除用户 ###
```DELETE /outline/{id}```
- 路径参数`id`: userId

### 修改用户名称 ###
```PUT /outline/{id}/name```
- 路径参数`id`: userId
- 请求参数`name`: 新用户名称

### 创建限速用户 ###
```POST /outline/rate/{rate}```
- location: user country

rate | limit
---- | ----
1 | 1Mb
2 | 2Mb
3 | 3Mb
5 | 5Mb
8 | 8Mb
10 | 10Mb
20 | 20Mb

### 删除限速用户 ###
```DELETE /outline/{id}/port/{port}/rate/{rate}```

# Example #
```
创建用户：curl -u user:123456 -X POST http://127.0.0.1:8080/outline/
返回：{"status":true,"id":"19","name":"","password":"","port":,"method":"","accessUrl":""}

修改用户名称：curl -u user:123456 -X PUT -d 'name=大卫' http://127.0.0.1:8080/outline/19/name
返回：{"status":true}

删除用户：curl -u user:123456 -X DELETE http://127.0.0.1:8080/outline/19
返回：{"status":true}

用户列表：curl -u user:123456 http://127.0.0.1:8080/outline/
返回：{"status":true,"accessKeys":[{"id":"0","name":"","password":"","port":,"method":"","accessUrl":""}, ... ], "users":[]}

创建限速用户：curl -u user:123456 -X POST http://45.76.213.221:8080/outline/rate/5
删除限速用户：curl -u user:123456 -X DELETE http://127.0.0.1:8080/outline/21/port/12384/rate/5
```
