package com.justplay1994.github.db2es.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @Package: com.justplay1994.github.oracle2es.core.dao
 * @Project: oracle-elasticsearch
 * @Description: //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/11/10 13:52
 * @Updater: huangzezhou
 * @Update_Date: 2018/11/10 13:52
 * @Update_Description: huangzezhou 补充
 **/
public interface TableMapper {

    /**
     * map={
     *     tbName: String       //表名
     *     cols: List<String>   //要返回的列名
     * }
     * @param map
     * @return
     */
    List<HashMap> queryTable(@Param("map") HashMap map);

}
