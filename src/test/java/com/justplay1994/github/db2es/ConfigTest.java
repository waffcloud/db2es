package com.justplay1994.github.db2es;

import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.config.Oracle2esConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/27 11:05
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/27 11:05
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ConfigTest {

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Autowired
    Db2esConfig db2esConfig;

    @Test
    public void configExtendsTest(){
        System.out.println(db2esConfig.getLatColumn());
        System.out.println(db2esConfig.getLonColumn());
    }
}
