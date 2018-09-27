package com.justplay1994.github.db2es.service.db.current;

import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.util.SpringContextUtils;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 表节点，存放该表所有数据、字段名、字段类型、字段描述
 */

public class TableNode {

    String tableName;       /*表名*/
    private int rowNumber;       /*总行数*/
    ArrayList<String> columns;  /*字段名*/
    LinkedBlockingQueue<ArrayList<String>> rows;  /*数据列表，采用阻塞队列，线程安全*/
    private ArrayList<String> dataType;/*字段类型*/
    LinkedBlockingQueue<String> esBulks;
    private boolean queryDataFinished = false;
    private boolean generatorEsBulkFinished = false;
    /*----------- constructor  -----------------*/
    public TableNode(){

    }

    public TableNode(String tableName){
        Db2esConfig db2esConfig = SpringContextUtils.getBean(Db2esConfig.class);
        this.tableName = tableName;
        this.columns = new ArrayList<String>();
        this.rows = new LinkedBlockingQueue<ArrayList<String>>(db2esConfig.getRowQueueSize()!=0 ?  db2esConfig.getRowQueueSize() : 10000);
        this.dataType = new ArrayList<String>();
        this.esBulks = new LinkedBlockingQueue<String>(db2esConfig.getEsBulkQueueSize()!=0 ?  db2esConfig.getEsBulkQueueSize() : 100);
        columns.add("ROW_ID");//oracle,唯一主键
        dataType.add("NVARCHAR2");
    }

    /*--------- setter getter ----------*/

    public boolean isQueryDataFinished() {
        return queryDataFinished;
    }

    public void setQueryDataFinished(boolean queryDataFinished) {
        this.queryDataFinished = queryDataFinished;
    }

    public boolean isGeneratorEsBulkFinished() {
        return generatorEsBulkFinished;
    }

    public void setGeneratorEsBulkFinished(boolean generatorEsBulkFinished) {
        this.generatorEsBulkFinished = generatorEsBulkFinished;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public LinkedBlockingQueue<String> getEsBulks() {
        return esBulks;
    }

    public void setEsBulks(LinkedBlockingQueue<String> esBulks) {
        this.esBulks = esBulks;
    }


    public ArrayList<String> getDataType() {
        return dataType;
    }

    public void setDataType(ArrayList<String> dataType) {
        this.dataType = dataType;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    public LinkedBlockingQueue<ArrayList<String>> getRows() {
        return rows;
    }

    public void setRows(LinkedBlockingQueue<ArrayList<String>> rows) {
        this.rows = rows;
    }
}
