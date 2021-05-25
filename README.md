# springBoot 使用MongoDB 例子
## test目录下有测试的列子
### 添加依赖

`
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
`
### application.yml 配置

`
spring:
  data:
    mongodb:
      uri: mongodb://用户名:密码@地址:端口/数据库

`
