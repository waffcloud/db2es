package com.justplay1994.github.db2es.service.db2es.impl;

import com.justplay1994.github.db2es.config.Oracle2esConfig;
import com.justplay1994.github.db2es.service.db.operate.OracleOperate;
import com.justplay1994.github.db2es.service.db2es.Oracle2es;

import com.justplay1994.github.db2es.service.es.ESOperate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.sql.SQLException;


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
public class Db2esImpl extends Oracle2es {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Autowired
    OracleOperate oracleOperate;

    @Autowired
    ESOperate esOperate;

    @Override
    public void transfer() {
        try {
            oracleOperate.queryAllStructure();// 查询所有数据结构，阻塞
        } catch (SQLException e) {
            logger.error("oracle operate error!\n",e);
        }
        esOperate.createMapping();// 创建索引映射，阻塞

        oracleOperate.queryAllDataByPage();// 数据查询，生产者
        esOperate.bulk(); //数据导入，消费者
    }
}
