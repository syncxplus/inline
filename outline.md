### Authorization ###
To access the API, use Authorization header for [BASIC AUTH](https://en.wikipedia.org/wiki/Basic_access_authentication#Client_side)

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

*示例(修改 id 为 0 的用户名称为 jibo):*
```
curl -u api:key -X PUT -d 'name=jibo' http://8.8.8.8:1234/outline/0/name
```
