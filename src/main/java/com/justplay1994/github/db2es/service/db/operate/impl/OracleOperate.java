package com.justplay1994.github.db2es.service.db.operate.impl;

import com.justplay1994.github.db2es.service.db.operate.DatabaseOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


/**
 * @Package: com.justplay1994.github.db2es.service.db.operate.impl
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:33
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:33
 * @Update_Description: huangzezhou 补充
 **/
@Service
public class OracleOperate implements DatabaseOperate{

    @Autowired
    DataSource dataSource;

    @Override
    public void queryAllStructure() {

    }

    @Override
    public void queryAllData() {

    }

    @Override
    public void queryAllDataByPage() {

    }

    @Override
    public void config() {

    }
}
