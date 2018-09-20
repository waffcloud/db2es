package com.justplay1994.github.db2es;

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
 * @Create_Date: 2018/9/20 11:03
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/20 11:03
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Oracle2esConfigTest {

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Test
    public void testArrayStringConfig(){
        String[] strings = oracle2esConfig.arrayJustReadTB();
        String[] strings1 = oracle2esConfig.arrayJustReadDB();
        String[] strings2 = oracle2esConfig.arraySkipReadDB();
        String[] strings3 = oracle2esConfig.arraySkipReadTB();
        System.out.println();
    }

}
