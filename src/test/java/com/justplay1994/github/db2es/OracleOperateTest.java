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
 * @Create_Date: 2018/9/20 11:46
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/20 11:46
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OracleOperateTest {

    @Autowired
    OracleOperate oracleOperate;

    @Autowired
    ESOperate esOperate;

    @Test
    public void queryAllStructureTest(){
        try {
            oracleOperate.queryAllStructure();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("");
    }

    @Test
    public void queryAllDataTest(){
        try {
            oracleOperate.queryAllStructure();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        oracleOperate.queryAllData();
        System.out.print("");
    }


}
