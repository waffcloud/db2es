package com.justplay1994.github.db2es.service.db.operate;

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
    public void queryAllData(); //一次获取所有数据
    public void queryAllDataByPage(); //分页查询
    public void config(); //数据库配置
}
