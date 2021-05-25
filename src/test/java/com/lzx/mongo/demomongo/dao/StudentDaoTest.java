package com.lzx.mongo.demomongo.dao;


import com.lzx.mongo.demomongo.mogonentity.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linuxprobe.luava.json.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class StudentDaoTest {

    @Autowired
    StudentDao studentDao;

    @Test
    public void save(){
        Student student = new Student();
//        @Id
//        private String id;
        student.setId("60acc0b7df06d613edf45f33");
        student.setName("关羽");
        student.setAge("500");
        student.setCountry("汉");
        student.setJob("将军");
        Student save = studentDao.save(student);



        log.info(JacksonUtils.toJsonString(save));
    }

    @Test
    public void findAll(){
        Student all = studentDao.findById("60acc0b7df06d613edf45f33").orElse(null);
        log.info(JacksonUtils.toJsonString(all));
    }


    @Test
    public void findByNameContains() {
        List<Student> all = studentDao.findByNameContains("张");
        log.info(JacksonUtils.toJsonString(all));
    }
}