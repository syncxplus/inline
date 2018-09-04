# Authorization #
To access the API, use Authorization header for [BASIC AUTH](https://en.wikipedia.org/wiki/Basic_access_authentication#Client_side)
- username: `user`
- password: set at runtime

# API #
* [用户列表](#用户列表)
* [创建用户](#创建用户)
* [删除用户](#删除用户)
* [修改用户名称](#修改用户名称)

### 用户列表 ###
```GET /outline```

### 创建用户 ###
```POST /outline```

### 删除用户 ###
```DELETE /outline/{id}```
- 路径参数`id`: userId

### 修改用户名称 ###
```PUT /outline/{id}/name```
- 路径参数`id`: userId
- 请求参数`name`: 新用户名称

# Example #
```
创建用户：curl -u user:123456 -X POST http://127.0.0.1:8080/outline/
返回：{"id":"19","name":"","password":"","port":,"method":"","accessUrl":""}
修改用户名称：curl -u user:123456 -X PUT -d 'name=大卫' http://127.0.0.1:8080/outline/19/name
返回：true
删除用户：curl -u user:123456 -X DELETE http://127.0.0.1:8080/outline/19
返回：true
用户列表：curl -u user:123456 http://127.0.0.1:8080/outline/
返回：{"accessKeys":[{"id":"0","name":"","password":"","port":,"method":"","accessUrl":""}, ... ], "users":[]}
```
