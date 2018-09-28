package com.justplay1994.github.db2es.service.db2es.impl;

import com.justplay1994.github.db2es.config.Oracle2esConfig;
import com.justplay1994.github.db2es.service.db.current.DatabaseNode;
import com.justplay1994.github.db2es.service.db.current.DatabaseNodeListInfo;
import com.justplay1994.github.db2es.service.db.operate.OracleOperate;
import com.justplay1994.github.db2es.service.db2es.Oracle2es;

import com.justplay1994.github.db2es.service.es.ESOperate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


/**
 * @Package: com.justplay1994.github.db2es.service.db2es.impl
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:50
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:50
 * @Update_Description: huangzezhou 补充
 **/

@Service
public class Oracle2esImpl implements Oracle2es {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Autowired
    OracleOperate oracleOperate;

    @Autowired
    ESOperate esOperate;

    @Override
    public void transfer() {
        long startTime = System.currentTimeMillis();

        oracleOperate.queryAllStructure();// 查询所有数据结构，阻塞

        esOperate.deleteAllConflict();//删除冲突索引
        esOperate.createMapping();// 创建索引映射，阻塞

//        oracleOperate.queryAllDataByPage();// 数据分页查询，将数据插入数据队列，数据队列生产者
        Thread oracleQueryThread = oracleOperate.createQueryAllDataByPage();//创建分页查询线程
        oracleQueryThread.start();

//        esOperate.esBulkGenerator();//将数据队列的数据出队，数据队列消费者。esBulk队列生产者。
        Thread esBulkGeneratorTread = esOperate.createEsBulkGeneratorTread();
        esBulkGeneratorTread.start();

//        esOperate.bulk(); //数据导入，消费者
        Thread bulkThread = esOperate.createBulkThread();
        bulkThread.start();

        while (true){
            if (oracleQueryThread.isAlive() || esBulkGeneratorTread.isAlive() || bulkThread.isAlive()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error("Main sleep error!\n", e);
                }
            }else {
                break;
            }
        }

        long time = System.currentTimeMillis()-startTime;
        long m = (time/1000)/60;
        long s = time%60;

        logger.info("\n\n============ Oracle2es finished! ==================start");
        logger.info("*  total dbNumber:   " + DatabaseNodeListInfo.dbNumber);
        logger.info("*  total tbNumber:   " + DatabaseNodeListInfo.tbNumber);
        logger.info("*  total rowNumber:  " + DatabaseNodeListInfo.rowNumber);
        logger.info("*  total failNumber: " + DatabaseNodeListInfo.failCount);
        logger.info("*  total Time: " + m + "m" + s + "s");
        logger.info("\n============ Oracle2es finished! ==================end\n\n");
    }
}
