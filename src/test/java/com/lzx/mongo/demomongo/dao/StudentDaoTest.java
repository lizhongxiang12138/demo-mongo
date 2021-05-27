package com.lzx.mongo.demomongo.dao;


import com.lzx.mongo.demomongo.mogonentity.Student;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linuxprobe.luava.json.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
        CountDownLatch count = new CountDownLatch(4);

        new Thread(()->{
            Student student = null;
            for(int i =0 ;i<200000;i++){
                student =  new Student();
//        @Id
//        private String id;
                //没有设置id时是新增
                //student.setId("60acc0b7df06d613edf45f33");
//            try {
//                Thread.sleep(1*1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                student.setName("马云"+i);
                student.setAge("5003"+i);
                student.setCountry("汉");
                student.setJob("将军");
                student.setCDate(new Date());
                Student save = studentDao.save(student);
                log.info(JacksonUtils.toJsonString(save));
            }
            count.countDown();
        }).start();

        new Thread(()->{
            Student student = null;
            for(int i =0 ;i<200000;i++){
                student =  new Student();
//        @Id
//        private String id;
                //没有设置id时是新增
                //student.setId("60acc0b7df06d613edf45f33");
//            try {
//                Thread.sleep(1*1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                student.setName("刘强东"+i);
                student.setAge("5003"+i);
                student.setCountry("汉");
                student.setJob("皇帝");
                student.setCDate(new Date());
                Student save = studentDao.save(student);
                log.info(JacksonUtils.toJsonString(save));
            }
            count.countDown();
        }).start();

        new Thread(()->{
            Student student = null;
            for(int i =0 ;i<200000;i++){
                student =  new Student();
//        @Id
//        private String id;
                //没有设置id时是新增
                //student.setId("60acc0b7df06d613edf45f33");
//            try {
//                Thread.sleep(1*1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                student.setName("公孙策"+i);
                student.setAge("5003"+i);
                student.setCountry("汉");
                student.setJob("军师");
                student.setCDate(new Date());
                Student save = studentDao.save(student);
                log.info(JacksonUtils.toJsonString(save));
            }
            count.countDown();
        }).start();


        new Thread(()->{
            Student student = null;
            for(int i =0 ;i<200000;i++){
                student =  new Student();
//        @Id
//        private String id;
                //没有设置id时是新增
                //student.setId("60acc0b7df06d613edf45f33");
//            try {
//                Thread.sleep(1*1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                student.setName("赵子龙"+i);
                student.setAge("5003"+i);
                student.setCountry("汉");
                student.setJob("将军");
                student.setCDate(new Date());
                Student save = studentDao.save(student);
                log.info(JacksonUtils.toJsonString(save));
            }
            count.countDown();
        }).start();


        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询测试
     */
    @Test
    public void findAll(){
//        //根据id查询
//        Student all = studentDao.findById("60acc0b7df06d613edf45f33").orElse(null);
//        log.info(JacksonUtils.toJsonString(all));
//
//        //dao 自定义方法查询
//        List<Student> findByNameContains = studentDao.findByNameContains("张");
//        log.info(JacksonUtils.toJsonString(findByNameContains));

        long count = mongoTemplate.estimatedCount(Student.class);
        double coutD = 0.0d;
        //动态定义条件查询
        Criteria c = new Criteria();
        //c.and("id").is("60acc0b7df06d613edf45f");
        c.and("name").regex(".*马云.*");
        Query query = new Query(c);
        Pageable page = PageRequest.of(0, 10);
        long satrt = System.currentTimeMillis();
        long conditionCount = mongoTemplate.count(query,Student.class);
        List<Student> students = mongoTemplate.find(query.with(page).with(Sort.by(Sort.Direction.DESC,"cDate")), Student.class);
        long end = System.currentTimeMillis();
        log.info(JacksonUtils.toJsonString(students));
        coutD = count/10000.0d;
        log.info("记录数：{} w",coutD);
        log.info("查询用时：{} ms,符合条件记录：{}",(end - satrt),conditionCount);
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