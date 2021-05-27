# springBoot 使用MongoDB 例子
## test目录下有测试的列子
### 添加依赖

```xml
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
 
### application.yml 配置

```yaml
spring:
   data:
     mongodb:
       uri: mongodb://用户名:密码@地址:端口/数据库
```

### 添加dao接口
```java
package com.lzx.mongo.demomongo.dao;

import com.lzx.mongo.demomongo.mogonentity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StudentDao extends MongoRepository<Student,String> {


    List<Student> findByNameContains(String name);

}
```

## 测试
### 测试类

```java
package com.lzx.mongo.demomongo.dao;


import com.lzx.mongo.demomongo.mogonentity.Student;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linuxprobe.luava.json.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class StudentDaoTest {

    @Autowired
    StudentDao studentDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存测试
     */
    @Test
    public void save(){
        Student student = new Student();
//        @Id
//        private String id;
        //没有设置id时是新增
        student.setId("60acc0b7df06d613edf45f33");
        student.setName("关羽");
        student.setAge("500");
        student.setCountry("汉");
        student.setJob("将军");
        //student.setCDate(new Date());
        Student save = studentDao.save(student);



        log.info(JacksonUtils.toJsonString(save));
    }

    /**
     * 查询测试
     */
    @Test
    public void findAll(){
        //根据id查询
        Student all = studentDao.findById("60acc0b7df06d613edf45f33").orElse(null);
        log.info(JacksonUtils.toJsonString(all));

        //dao 自定义方法查询
        List<Student> findByNameContains = studentDao.findByNameContains("张");
        log.info(JacksonUtils.toJsonString(findByNameContains));

        //动态定义条件查询
        Criteria c = new Criteria();
        c.and("id").is("60acc0b7df06d613edf45f");
        c.and("job").regex(".*将.*");
        Query query = new Query(c);
        List<Student> students = mongoTemplate.find(query, Student.class);
        log.info(JacksonUtils.toJsonString(students));
    }

    /**
     * 更新指定字段
     */
    @Test
    public void updateField(){
        Query query = new Query(Criteria.where("id").is("60acc0b7df06d613edf45f33"));
        Update update = new Update();
        update.set("job","蜀国将军");
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Student.class);
        log.info("更新{}条记录",updateResult.getModifiedCount());
    }

}
```

## docker-compose 搭建 MongoDB 分片集群
### 规划
#### config:
c01: 172.20.0.11:27019
c02: 172.20.0.12:27019

#### 分片1：
s01：172.20.0.13:27018
s02：172.20.0.14:27018

```
# 初始化节点
rs.initiate({ _id: "shrs01",version: 1,members: [{ _id: 0, host : "172.20.0.13:27018" }]});
# 添加节点
rs.add("172.20.0.14:27018");
```

#### 分片2
s21：172.20.0.15:27018
s22：172.20.0.16:27018

```mongojs
//初始化节点
rs.initiate({ _id: "shrs02",version: 1,members: [{ _id: 0, host : "172.20.0.15:27018" }]});
//添加节点
rs.add("172.20.0.16:27018");
```

#### router
ms：172.20.0.17:27017
对外端口：27050

##### 增加分片
```mongojs
sh.addShard("shrs01/172.20.0.13:27018,172.20.0.14:27018");
sh.addShard("shrs02/172.20.0.15:27018,172.20.0.16:27018");
```

##### 创建用户
```mongojs
use admin;
db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'userAdminAnyDatabase', db: 'admin'},"readWriteAnyDatabase"]});
// 尝试使用上面创建的用户信息进行连接。
db.auth('admin', '123456');
```
```mongojs
db.createUser(
     {
       user: "shard",
       pwd: "123456",
       roles: ["readWrite"]
     }
)
```

##### 对数据库启用分片
```mongojs
sh.shardCollection("shard.t_student",{"_id":"hashed","cDate":-1});
```