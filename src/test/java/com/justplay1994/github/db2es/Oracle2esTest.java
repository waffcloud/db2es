package com.justplay1994.github.db2es;

import com.justplay1994.github.db2es.service.db2es.Oracle2es;
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
 * @Create_Date: 2018/9/27 16:17
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/27 16:17
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Oracle2esTest {

    @Autowired
    Oracle2es oracle2es;

    @Test
    public void transfer(){
        oracle2es.transfer();
        System.out.println();
    }
}
