package com.justplay1994.github.db2es.config;

import com.justplay1994.github.db2es.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @Package: com.justplay1994.github.db2es.config
 * @Project: db2es
 * @Creator: huangzezhou
 * @Create_Date: 2018/11/22 15:50
 * @Updater: huangzezhou
 * @Update_Date: 2018/11/22 15:50
 * @Update_Description: huangzezhou 补充
 * @Description: //TODO
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PropertiesConfigTest {

    @Autowired
    LoadMap loadMap;

    @Test
    public void testSetLoadMap() throws Exception {
        System.out.println("testSetLoadMap finished!");
    }
}