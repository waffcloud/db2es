package com.justplay1994.github.db2es.service.db.operate;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Package: com.justplay1994.github.db2es.service.db.operate
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:14
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:14
 * @Update_Description: huangzezhou 补充
 **/
public interface DatabaseOperate {

    public void queryAllStructure(); //获取所有表结构
    public void deleteTableWithoutGeo();//删除没有JD84和WD84的表
    public void queryAllDataByPage(); //分页查询
    public Thread createQueryAllDataByPage();   //创建分页查询线程
}
