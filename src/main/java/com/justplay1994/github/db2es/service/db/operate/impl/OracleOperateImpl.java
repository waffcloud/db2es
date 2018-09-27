package com.justplay1994.github.db2es.service.db.operate.impl;

import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.config.Oracle2esConfig;
import com.justplay1994.github.db2es.service.db.current.DatabaseNode;
import com.justplay1994.github.db2es.service.db.current.DatabaseNodeListInfo;
import com.justplay1994.github.db2es.service.db.current.TableNode;
import com.justplay1994.github.db2es.service.db.operate.DatabaseOperate;
import com.justplay1994.github.db2es.service.db.operate.OracleOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @Package: com.justplay1994.github.db2es.service.db.operate.impl
 * @Project: db2es
 * @Description: //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:33
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:33
 * @Update_Description: huangzezhou 补充
 **/
@Service
public class OracleOperateImpl implements OracleOperate {

    private static final Logger logger = LoggerFactory.getLogger(OracleOperateImpl.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Autowired
    Db2esConfig db2esConfig;

    @Override
    public void queryAllStructure() {
        Connection con=null;
        Statement st=null;
        ResultSet rs=null;
        try {
            /*创建缓存数据结构*/
            DatabaseNodeListInfo.databaseNodeList = new ArrayList<DatabaseNode>();
            /*查询所有库、表、字段*/
            con = dataSource.getConnection();
            logger.info("Connect oracle success.");
            st = con.createStatement();
            /*获取库名、表名*/
            String sql = "SELECT DISTINCT TABLE_NAME, OWNER FROM all_tab_columns WHERE OWNER='" + oracle2esConfig.getOwner().toUpperCase() + "'";
            logger.debug("[sql: " + sql + " ]");
            rs = st.executeQuery(sql);

            /*获取所有库、表、列名开始*/
            List<DatabaseNode> dbList = DatabaseNodeListInfo.databaseNodeList;

            while (rs.next()) {
                String tbStr = rs.getString("TABLE_NAME");
                String dbStr = rs.getString("OWNER");


                boolean skip = false;
                /*判断该库是否是必须读取*/
                if (!oracle2esConfig.isNull(oracle2esConfig.arrayJustReadDB())) {
                    skip = true;
                    for (int i = 0; i < oracle2esConfig.arrayJustReadDB().length; ++i) {
                        if (dbStr.equals(oracle2esConfig.arrayJustReadDB()[i])) {
                            skip = false;
                            break;
                        }
                    }
                }
            /*判断该表是否是必须读取*/
                if (!oracle2esConfig.isNull(oracle2esConfig.arrayJustReadTB())) {
                    skip = true;
                    for (int i = 0; i < oracle2esConfig.arrayJustReadTB().length; ++i) {
                    /*dbName.tbName*/
                        if (dbStr.equals(oracle2esConfig.arrayJustReadTB()[i].split("\\.")[0]) && tbStr.equals(oracle2esConfig.arrayJustReadTB()[i].split("\\.")[1])) {
                            skip = false;
                            break;
                        }
                    }
                }
            /*判断该库是否需要跳过*/
                if (!oracle2esConfig.isNull(oracle2esConfig.arraySkipReadDB())) {
                    for (int i = 0; i < oracle2esConfig.arraySkipReadDB().length; ++i) {
                        if (dbStr.equals(oracle2esConfig.arraySkipReadDB()[i])) {
                            skip = true;
                            break;
                        }
                    }
                }
            /*判断该表是否需要跳过*/
                if (!oracle2esConfig.isNull(oracle2esConfig.arraySkipReadTB())) {
                    for (int i = 0; i < oracle2esConfig.arraySkipReadTB().length; ++i) {
                     /*dbName.tbName*/
                        if (dbStr.equals(oracle2esConfig.arraySkipReadTB()[i].split("\\.")[0]) && tbStr.equals(oracle2esConfig.arraySkipReadTB()[i].split("\\.")[1])) {
                            skip = true;
                            break;
                        }
                    }
                }

                if (skip) continue;
                logger.debug("[dbName= " + dbStr + ", tbName= " + tbStr+"]");
                /*判断该库是否存在*/
                if (dbList.size() != 0 && dbStr.equalsIgnoreCase(dbList.get(dbList.size() - 1).getDbName())) {
                    List<TableNode> tbList = new ArrayList<TableNode>();
                    TableNode tableNode = new TableNode(tbStr);
                    dbList.get(dbList.size() - 1).getTableNodeList().add(tableNode);
                } else {/*不存在则新建一个库节点，并且新建表节点*/
                    List<TableNode> tbList = new ArrayList<TableNode>();
                    TableNode tableNode = new TableNode(tbStr);
                    tbList.add(tableNode);
                    DatabaseNode dbNode = new DatabaseNode(dbStr, tbList);
                    dbList.add(dbNode);
                }
            }

            /*获取表名、字段名\字段类型*/
            /*TODO 只能假设，同一个owner（用户）下没有重名的表了，这里有风险*/
            String sql1 = "SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE from all_tab_columns WHERE OWNER='" + oracle2esConfig.getOwner().toUpperCase() + "'";
            logger.debug("[sql: " + sql1 + " ]");
            rs = st.executeQuery(sql1);


            while (rs.next()) {
                String colStr = rs.getString("COLUMN_NAME");
                String tbStr = rs.getString("TABLE_NAME");
                String dataType = rs.getString("DATA_TYPE");
                for (int i = 0; i < dbList.size(); ++i) {
                    List<TableNode> tbList = dbList.get(i).getTableNodeList();
                    for (int j = 0; j < tbList.size(); ++j) {
                        if (tbList.get(j).getTableName().equalsIgnoreCase(tbStr)) {
                            logger.debug("[tbName=" + tbStr + ",colName=" + colStr + ",dataType=" + dataType + "]");
                            TableNode tb = tbList.get(j);
                            tb.getColumns().add(colStr);
                            tb.getDataType().add(dataType);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Query oracle error!\n", e);
        } finally {
            /*获取所有库、表、列名结束*/
            close(con, st, rs);
        }
    }

    /**
     * 1.调度器，遍历所有表名
     * 2.调度器，控制分页参数
     * 3.多线程，分页查询每张表的数据
     */
    @Override
    public void queryAllDataByPage() {
        Connection con=null;
        Statement st=null;
        ResultSet rs=null;
        String queryByPage="";
        try {//一旦请求数据库异常，则停止与该数据库交互
            con = dataSource.getConnection();
            st = con.createStatement();
            List<DatabaseNode> databaseNodeList = DatabaseNodeListInfo.databaseNodeList;
            for (DatabaseNode databaseNode : databaseNodeList) {
                DatabaseNodeListInfo.dbNumber++;
                for (TableNode tableNode : databaseNode.getTableNodeList()) {
                    DatabaseNodeListInfo.tbNumber++;
                    //获取该表的行数
                    String queryRowNumber = "SELECT COUNT(*) FROM " + tableNode.getTableName();
                    rs = st.executeQuery(queryRowNumber);
                    if (rs.next()) {
                        tableNode.setRowNumber(rs.getInt(1));
                    }
                    /*总共数据量统计：所有数据量+该表数据量*/
                    DatabaseNodeListInfo.rowNumber += tableNode.getRowNumber();
                    /*该库数据量统计：该库所有数据量+该表数据量*/
                    databaseNode.setRowNumber(databaseNode.getRowNumber() + tableNode.getRowNumber());
                    if (tableNode.getRowNumber() > 0) {
                        int startPosition = 1;
                        int endPosition;
                        while (true){
                            if (startPosition > tableNode.getRowNumber())break;
                            endPosition = startPosition + db2esConfig.getPageSize();
                            //oracle 分页查询语句
                            queryByPage = "SELECT * FROM(SELECT ROWID ROW_ID, ROWNUM ROW_NUM, T.*  FROM " + tableNode.getTableName() + " T) TM WHERE ROW_NUM >= " + startPosition + " AND ROW_NUM < " + endPosition;
                            rs = st.executeQuery(queryByPage);
                            while (rs.next()) {
                                ArrayList<String> row = new ArrayList<String>();
                                List cols = tableNode.getColumns();
                                for (int i = 0; i < cols.size(); ++i) {
                                    row.add(rs.getString(cols.get(i).toString()));
                                }
                                boolean insertRowSuccess = tableNode.getRows().offer(row, db2esConfig.getQueueWaitTime(), TimeUnit.MILLISECONDS);
                                if (!insertRowSuccess){
                                    logger.error("Offer row queue error!\n");
                                }
                            }
                            startPosition = endPosition;
                        }
                    }
                    /*完成一张表的查询，更改该表的数据查询完成标识符*/
                    tableNode.setQueryDataFinished(true);
                }
            }
        } catch (SQLException e) {
            logger.error("sql error: "+queryByPage+"\n",e);
        } catch (InterruptedException e) {
            logger.error("Offer block queue error!\n", e);
        } finally {
            close(con, st, rs);
        }
    }

    public void close(Connection con, Statement st, ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }catch (SQLException e){
            logger.error("Oracle Connection/Statement/ResultSet close error!\n",e);
        }
    }
}
