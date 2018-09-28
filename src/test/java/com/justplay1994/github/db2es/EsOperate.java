package com.justplay1994.github.db2es;

import com.justplay1994.github.db2es.service.db.operate.OracleOperate;
import com.justplay1994.github.db2es.service.es.ESOperate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/26 15:00
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/26 15:00
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EsOperate {

    @Autowired
    ESOperate esOperate;

    @Autowired
    OracleOperate oracleOperate;

    @Test
    public void createMapping(){
        oracleOperate.queryAllStructure();// 请求所有的数据结构
        esOperate.deleteAllConflict();//删除重名的冲突索引
        esOperate.createMapping();//生成es索引映射（index mapping)
        System.out.print("");
    }

    @Test
    public void db2esTest(){
        oracleOperate.queryAllStructure();// 请求所有的数据结构
        esOperate.deleteAllConflict();//删除重名的冲突索引
        esOperate.createMapping();//生成es索引映射（index mapping)
        oracleOperate.queryAllDataByPage();//单线程查询数据
        esOperate.esBulkGenerator();//单线程产生esBulk请求
        esOperate.bulk();//阻塞导入数据至es
    }

    @Test
    public void oracle2esThread() throws InterruptedException {
        oracleOperate.queryAllStructure();// 请求所有的数据结构
        esOperate.deleteAllConflict();//删除重名的冲突索引
        esOperate.createMapping();//生成es索引映射（index mapping)
//        oracleOperate.queryAllDataByPage();//单线程查询数据
        Thread oracle = oracleOperate.createQueryAllDataByPage();
        oracle.start();
//        esOperate.esBulkGenerator();//单线程产生esBulk请求
        Thread generator = esOperate.createEsBulkGeneratorTread();
        generator.start();
//        esOperate.bulk();//阻塞导入数据至es
        Thread bulk = esOperate.createBulkThread();
        bulk.start();

        while (true){
            if (oracle.isAlive() || generator.isAlive() || bulk.isAlive()){
                Thread.sleep(2000);
            }else {
                break;
            }
        }
    }
}
