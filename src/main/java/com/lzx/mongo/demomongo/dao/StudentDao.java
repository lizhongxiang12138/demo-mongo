package com.lzx.mongo.demomongo.dao;

import com.lzx.mongo.demomongo.mogonentity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StudentDao extends MongoRepository<Student,String> {


    List<Student> findByNameContains(String name);

}
