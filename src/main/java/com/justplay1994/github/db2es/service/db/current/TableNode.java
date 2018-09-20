package com.justplay1994.github.db2es.service.db.current;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * 表节点，存放该表所有数据、字段名、字段类型、字段描述
 */
public class TableNode {
    String tableName;       /*表名*/
    ArrayList<String> columns = new ArrayList<String>();  /*字段名*/
    LinkedBlockingDeque<ArrayList<String>> rows = new LinkedBlockingDeque<ArrayList<String>>();  /*数据列表，采用阻塞队列，线程安全*/
    private ArrayList<String> dataType = new ArrayList<String>();/*字段类型*/
    ArrayList<String> cloumnComment = new ArrayList<String>(); /*字段描述*/

    private static final String QUEUE_END_STR = "END_END_END_END#";
    private static final int QUEUE_END_COUNT = 2;

    /*----------- constructor  -----------------*/
    public TableNode(){

    }

    public TableNode(String tableName){
        this.tableName = tableName;
        this.columns = new ArrayList<String>();
        this.rows = new LinkedBlockingDeque<ArrayList<String>>();
        this.dataType = new ArrayList<String>();
    }

    public TableNode(String tableName,ArrayList<String> columns, LinkedBlockingDeque<ArrayList<String>> rows, ArrayList<String> dataType){
        this.tableName = tableName;
        this.columns = columns;
        this.rows = rows;
        this.dataType = dataType;
    }

    /*------------- util ----------------------*/

    /**
     * 放置一个队列结束标识符
     */
    public void offerQueueEnd(){
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < QUEUE_END_COUNT; ++i)
            list.add(QUEUE_END_STR);
        rows.offer(list);
    }

    /**
     * 判断当前的出队元素是否是队列结束标识符
     * @param list
     * @return
     */
    public boolean isQueueEnd(ArrayList<String> list){
        for (int i = 0; i < QUEUE_END_COUNT; ++i){
            if (!list.get(i).equals(QUEUE_END_STR))
                return false;
        }
        return true;
    }

    /*--------- setter getter ----------*/
    public ArrayList<String> getDataType() {
        return dataType;
    }

    public void setDataType(ArrayList<String> dataType) {
        this.dataType = dataType;
    }

    public ArrayList<String> getCloumnComment() {
        return cloumnComment;
    }

    public void setCloumnComment(ArrayList<String> cloumnComment) {
        this.cloumnComment = cloumnComment;
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

    public LinkedBlockingDeque<ArrayList<String>> getRows() {
        return rows;
    }

    public void setRows(LinkedBlockingDeque<ArrayList<String>> rows) {
        this.rows = rows;
    }
}
