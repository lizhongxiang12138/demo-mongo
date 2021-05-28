package com.lzx.mongo.demomongo.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class RandomUtilsTest {

    @Test
    public void getRandomRange() {
    }

    @Test
    public void generatePlate(){
        String[] palteStr = {"Q","W","E","R","T","Y","U","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M","1","2","3","4","5","6","7","8","9","0"};
        for(int j=0;j<1000;j++){
            StringBuffer sb = new StringBuffer("云A");
            for(int i =0;i < 5;i++){
                sb.append(palteStr[RandomUtils.getRandomRange(0,palteStr.length-1)]);
            }
            log.info("车牌：{}",sb.toString());
        }
    }
}