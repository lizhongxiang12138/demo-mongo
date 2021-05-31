package com.lzx.mongo.demomongo.dao;


import com.lzx.mongo.demomongo.mogonentity.Student;
import com.lzx.mongo.demomongo.utils.RandomUtils;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linuxprobe.luava.json.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

        CountDownLatch count = new CountDownLatch(10);
        String[] nameStr = {"刘备","张飞","曹操","关羽","王麻子","李四","王武","孙权"};
        String[] palteStr = {"Q","W","E","R","T","Y","U","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M","1","2","3","4","5","6","7","8","9","0"};
        String[] countryStr = {"魏国","蜀国","吴国","美国","中国","英国"};
        String[] jobStr = {"皇帝","将军","工人"};
        String[] platePrefixStr = {"魏A","蜀A","吴A","美A","中A","英A"};

        for(int i=0 ;i<10;i++){
            new Thread(()->{
                try{
                    exec(nameStr,palteStr,"10",countryStr,jobStr,platePrefixStr);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }finally {
                    count.countDown();
                }
            },String.format("插入线程-->%s",i)).start();
        }

        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void exec(@NotNull String[] name,
                      @NotNull String[] palte,
                      @NotNull String age,
                      @NotNull String[] country,
                      @NotNull String[] job,
                      @NotNull String[] platePrefix) {
        Student student = null;
        ArrayList<Student> saveBatch = new ArrayList<>();
        int saveCount = 0;
        for(int i =0 ;i<200000;i++){
            student =  new Student();
            student.setName(name[RandomUtils.getRandomRange(0,name.length -1)]);
            student.setAge(age+i);
            student.setCountry(country[RandomUtils.getRandomRange(0,country.length-1)]);
            student.setJob(job[RandomUtils.getRandomRange(0,job.length-1)]);
            student.setCDate(new Date());
            StringBuffer sb = new StringBuffer(platePrefix[RandomUtils.getRandomRange(0,platePrefix.length-1)]);
            for(int j =0;j < 5;j++){
                sb.append(palte[RandomUtils.getRandomRange(0,palte.length-1)]);
            }
            log.info("车牌：{}",sb.toString());
            student.setLicensePlate(sb.toString());
            saveBatch.add(student);
            saveCount++;
            log.info("{},添加第{}万条记录",Thread.currentThread().getName(),i/10000.0);
            if(saveCount == 500){
                long start = System.currentTimeMillis();
                BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Student.class);
                bulkOperations.insert(saveBatch);
                BulkWriteResult execute = bulkOperations.execute();
                long end = System.currentTimeMillis();
                log.info("{},插入{}条,更新{}条,耗时{}s",Thread.currentThread().getName(),execute.getInsertedCount(),execute.getUpserts(),(end - start)/1000.0);
                log.info(JacksonUtils.toJsonString(saveBatch));
                saveBatch.clear();
                saveCount=0;
            }

        }
        if(saveBatch.size() > 0){
            studentDao.saveAll(saveBatch);
        }
    }

    /**
     * 查询测试
     */
    @Test
    public void findAll() throws ParseException {
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        c.and("cDate").gte(df.parse("2021-05-31 17:00:00")).lt(df.parse("2021-06-01 00:00:00"));
        c.and("licensePlate").regex(".*AP.*");
//        c.and("name").regex("马云.*");
        Query query = new Query(c);
        Pageable page = PageRequest.of(0, 10);
//        long conditionCount = mongoTemplate.count(query,Student.class);
        long conditionCount = 0;
        long satrt = System.currentTimeMillis();
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

    /**
     * 删除测试
     */
    @Test
    public void delete(){
        Criteria criteria = new Criteria();
        Query query = new Query(criteria);
        query.with(PageRequest.of(0,1000,Sort.by(Sort.Direction.DESC,"cDate")));
        DeleteResult remove = mongoTemplate.remove(query, Student.class);
        long deletedCount = remove.getDeletedCount();
        log.info("删除的记录数：{}",deletedCount);
    }

}