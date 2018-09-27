package com.justplay1994.github.db2es.service.es;

import com.justplay1994.github.db2es.service.db.current.TableNode;

/**
 * @Package: com.justplay1994.github.db2es.service.db
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:39
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:39
 * @Update_Description: huangzezhou 补充
 **/
public interface ESOperate {
    public void createMapping(); //创建映射
    public void bulk(); //批量导入数据
    public void deleteAllConflict(); //删除已存在的同名索引，建议与navicat导入数据策略一致，先删再导
    public void config();//搜索引擎配置
    /**
     * 索引名与库表名的关系映射
     *
     * @param dbName
     * @param tbName
     * @return 索引名称
     */
    public String indexName(String dbName, String tbName);

    /**
     * rows队列消费者
     * bulkQueue队生产者
     * @return
     */
    public void esBulkGenerator();
}
