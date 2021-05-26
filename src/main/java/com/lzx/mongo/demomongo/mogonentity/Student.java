package com.lzx.mongo.demomongo.mogonentity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Document(collection = "t_student")
public class Student {


    /**
     * _id : 2
     * name : 刘备
     * age : 200
     * country : 三国
     */
    @Id
    private String id;

    private String name;
    private String age;
    private String country;
    private String job;

    @Field
    private Date cDate;
}
