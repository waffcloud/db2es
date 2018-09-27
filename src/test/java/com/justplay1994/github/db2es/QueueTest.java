package com.justplay1994.github.db2es;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/27 9:09
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/27 9:09
 * @Update_Description: huangzezhou 补充
 **/
public class QueueTest {

    private LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(2);

    @Test
    public void testLinkedBlockingQueue() throws InterruptedException {
        System.out.println(linkedBlockingQueue.offer("1",3000, TimeUnit.MILLISECONDS));
        System.out.println(linkedBlockingQueue.offer("2",3000, TimeUnit.MILLISECONDS));
        System.out.println(linkedBlockingQueue.offer("3",3000, TimeUnit.MILLISECONDS));//false
        System.out.println(linkedBlockingQueue.poll(3000, TimeUnit.MILLISECONDS));
        System.out.println(linkedBlockingQueue.poll(3000, TimeUnit.MILLISECONDS));
        System.out.println(linkedBlockingQueue.poll(3000, TimeUnit.MILLISECONDS));//null

        LinkedBlockingQueue linkedBlockingQueue1 = new LinkedBlockingQueue(1);
    }
}
