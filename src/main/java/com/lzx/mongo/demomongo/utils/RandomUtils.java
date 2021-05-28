package com.lzx.mongo.demomongo.utils;

import java.util.Random;

public class RandomUtils {


    public static int getRandomRange(int min,int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }

}
