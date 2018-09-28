package com.justplay1994.github.db2es.service.db.current;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustPlay1994 on 2018/4/3.
 * https://github.com/JustPlay1994/daily-log-manager
 */

/**
 * 数据库节点，存放一个数据库下的所有数据
 */
public class DatabaseNode {
    String dbName;      /*数据库名*/
    List<TableNode> tableNodeList = new ArrayList<TableNode>();  /*数据表列表*/
    long rowNumber;     /*数据总行数*/
    int tableFinishedCount; //已完成bulk收集的表数量，这时候还没有正在执行bulk，等待组成bulk Size大的包后，便执行请求

    public DatabaseNode(){

    }

    public DatabaseNode(String dbName, List<TableNode> tableNodeList){
        this.dbName = dbName;
        this.tableNodeList = tableNodeList;
    }

    public int getTableFinishedCount() {
        return tableFinishedCount;
    }

    public void setTableFinishedCount(int tableFinishedCount) {
        this.tableFinishedCount = tableFinishedCount;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<TableNode> getTableNodeList() {
        return tableNodeList;
    }

    public void setTableNodeList(List<TableNode> tableNodeList) {
        this.tableNodeList = tableNodeList;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(long rowNumber) {
        this.rowNumber = rowNumber;
    }
}
