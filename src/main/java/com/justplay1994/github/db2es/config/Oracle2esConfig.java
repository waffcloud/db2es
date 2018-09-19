package com.justplay1994.github.db2es.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Package: com.justplay1994.github.db2es.config
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 19:34
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 19:34
 * @Update_Description: huangzezhou 补充
 **/
//@Component
//@ConfigurationProperties(prefix = "oracle2es")
//@PropertySource("classpath:config/oracle2es.properties")// 用来指定配置文件的位置
public class Oracle2esConfig {

    //由于oracle受限情况下，不能使用all_tables查询tablespace，所以查询不到tablespace，只能讲tablespace用owner替代了
    private String owner;

    //命名空间
    private String tableSpace;

    //只读的数据库列表，为空则读全库.eg: dbName
    private String justReadDB;

    //只读的表的列表，为空则读所有表。eg: dbName.tbName
    private String justReadTB;

    //需要跳过的库的列表，为空则不跳过。eg: dbName
    private String skipReadDB;

    //需要跳过的表的列表，为空则不跳过。eg: dbName.tbName
    private String skipReadTB;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTableSpace() {
        return tableSpace;
    }

    public void setTableSpace(String tableSpace) {
        this.tableSpace = tableSpace;
    }

    public String getJustReadDB() {
        return justReadDB;
    }

    public void setJustReadDB(String justReadDB) {
        this.justReadDB = justReadDB;
    }

    public String getJustReadTB() {
        return justReadTB;
    }

    public void setJustReadTB(String justReadTB) {
        this.justReadTB = justReadTB;
    }

    public String getSkipReadDB() {
        return skipReadDB;
    }

    public void setSkipReadDB(String skipReadDB) {
        this.skipReadDB = skipReadDB;
    }

    public String getSkipReadTB() {
        return skipReadTB;
    }

    public void setSkipReadTB(String skipReadTB) {
        this.skipReadTB = skipReadTB;
    }

    public String toString(){
        return "Read oracle2es.properties success!";
    }
}
