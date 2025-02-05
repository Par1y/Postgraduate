# 规划你如何逃离大学的系统后端

## RestfulAPI

### /v1/user

1. GET /{**`uid`**} 查询用户  <br>  CommonResult\<UserVO\>

2. POST / 用户登录 （**`uid`** **`password`**）  <br>  CommonResult\<UserVO\>

3. POST / 用户注册 （**`username`** **`password`**）  <br>  CommonResult\<UserVO\>

4. POST /{**`uid`**} 用户修改 （`username` `password`）  <br>  CommonResult\<UserVO\>

5. DELETE /{**`uid`**} 用户删除  <br>  CommonResult\<UserVO\>

---

### /v1/plan

1. GET /{**`pid`**} 查询单计划  <br>  CommonResult\<Plan\>

2. GET / 按条件查询计划 （`uid` `beginDate` `endDate`）  <br>  CommonResult\<List\<Plan\>\>

3. POST / 新建计划 （**`uid`** **`date`** **`content`**）  <br>  CommonResult\<Plan\>

4. POST /{**`pid`**} 计划修改 （`date` `content`）  <br>  CommonResult\<Plan\>

5. DELETE /{**`uid`**} 计划删除  <br>  CommonResult\<Plan\>

---

### /v1/dynamic

1. GET /{**`did`**} 获取动态

2. GET / 按条件获取动态 （`uid` `beginDate` `endDate` `page`）

3. POST / 发送动态 （**`uid`** **`date`** **`content`** `replyId`）

4. DELETE /{**`pid`**} 删除动态 