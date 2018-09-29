package com.justplay1994.github.db2es;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/28 16:24
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/28 16:24
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThreadTest {


    @Test
    public void startAndWaitThread() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; ++i)
                    System.out.println(i);
            }
        });
        thread.start();
        System.out.println(thread.isAlive());
        Thread.sleep(3000);
        System.out.println(thread.isAlive());
    }

}
