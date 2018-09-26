package com.justplay1994.github.db2es.service.db.operate.impl;

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


/**
 * @Package: com.justplay1994.github.db2es.service.db.operate.impl
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:33
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:33
 * @Update_Description: huangzezhou 补充
 **/
@Service
public class OracleOperateImpl implements OracleOperate{

    private static final Logger logger = LoggerFactory.getLogger(OracleOperateImpl.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    Oracle2esConfig oracle2esConfig;

    @Override
    public void queryAllStructure() throws SQLException {

        /*查询所有库、表、字段*/
        /*mysql*/
        Connection con = dataSource.getConnection();
        logger.info("Connect oracle Successfull.");
        Statement st = con.createStatement();
        /*获取库名、表名*/
//        String sql = "SELECT TABLE_NAME, TABLESPACE_NAME FROM all_tables WHERE OWNER='"+user.toUpperCase()+"'";/*在受限的oracle数据库中，all_tables表不能用*/
        String sql = "SELECT DISTINCT TABLE_NAME, OWNER FROM all_tab_columns WHERE OWNER='" + oracle2esConfig.getOwner().toUpperCase() + "'";
        logger.debug("[sql: " + sql + " ]");
        ResultSet rs = st.executeQuery(sql);

        /*获取所有库、表、列名开始*/
        DatabaseNodeListInfo.databaseNodeList = new ArrayList<DatabaseNode>();
        List<DatabaseNode> dbList = DatabaseNodeListInfo.databaseNodeList;

        DatabaseNode lastDB = null;
        TableNode lastTable = null;


        while (rs.next()) {
            String tbStr = rs.getString("TABLE_NAME");
            String dbStr = rs.getString("OWNER");

            logger.debug("[dbName= " + dbStr + ", tbName= " + tbStr);
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

        /*获取表名、字段名*/
        /*只能假设，同一个owner（用户）下没有重名的表了，这里有风险*/
        String sql1 = "SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE from all_tab_columns WHERE OWNER='" + oracle2esConfig.getOwner().toUpperCase() + "'";
        logger.debug("[sql: " + sql1 + " ]");
        rs = st.executeQuery(sql1);


        while (rs.next()) {
            String colStr = rs.getString("COLUMN_NAME");
            String tbStr = rs.getString("TABLE_NAME");
            String dataType = rs.getString("DATA_TYPE");
            logger.debug("[tbName=" + tbStr + ",colName=" + colStr + ",dataType=" + dataType + "]");
            for (int i = 0; i < dbList.size(); ++i) {
                List<TableNode> tbList = dbList.get(i).getTableNodeList();
                for (int j = 0; j < tbList.size(); ++j) {
                    if (tbList.get(j).getTableName().equalsIgnoreCase(tbStr)) {
                        TableNode tb = tbList.get(j);
                        tb.getColumns().add(colStr);
                        tb.getDataType().add(dataType);
                        tb.getCloumnComment().add("无");/*预先插入空值*/
                    }
                }
            }
        }

        /*获取comment备注*/
        String sql3 = "SELECT TABLE_NAME,COLUMN_NAME,COMMENTS from all_col_comments WHERE OWNER='" + oracle2esConfig.getOwner().toUpperCase() + "'";
        logger.debug("[sql: " + sql3 + "]");
        rs = st.executeQuery(sql3);
        while (rs.next()) {
            String colStr = rs.getString("COLUMN_NAME");
            String tbStr = rs.getString("TABLE_NAME");
            String colComment = rs.getString("COMMENTS");
            logger.debug("[tbName=" + tbStr + ",colStr=" + colStr + ",colComment=" + colComment + "]");
            for (int i = 0; i < dbList.size(); ++i) {
                String dbStr = null;
                /*数据字典输出的db字符串*/
                if (oracle2esConfig.getIndexDB() != "")
                    dbStr = dbList.get(i).getDbName();
                else
                    dbStr = oracle2esConfig.getIndexDB();
                List<TableNode> tbList = dbList.get(i).getTableNodeList();
                for (int j = 0; j < tbList.size(); ++j) {
                    if (tbList.get(j).getTableName().equalsIgnoreCase(tbStr)) {/*匹配上表名*/
                        List<String> cList = tbList.get(j).getColumns();
                        for (int k = 0; k < cList.size(); ++k) {
                            if (cList.get(k).equalsIgnoreCase(colStr)) {/*该表中匹配上字段就行了*/
                                tbList.get(j).getCloumnComment().add(k, colComment);
                            }
                        }
                    }
                }
            }
        }

        /*获取所有库、表、列名结束*/
        rs.close();
        st.close();
        con.close();
    }

    @Override
    public void queryAllData() {
        try {

        /*连接Mysql相关变量*/
            Connection con = null;
            ResultSet rs = null;
            Statement st = null;

            String sql = "select * from ";
            if (DatabaseNodeListInfo.databaseNodeList == null || DatabaseNodeListInfo.databaseNodeList.size() <= 0) {
                logger.error("database structure is null!");
                return;
            }

            Iterator<DatabaseNode> databaseNodeIt = DatabaseNodeListInfo.databaseNodeList.iterator();
            while (databaseNodeIt.hasNext()) {
                DatabaseNodeListInfo.dbNumber++;
                DatabaseNode databaseNode = databaseNodeIt.next();
                /*获取数据库连接*/
                con = dataSource.getConnection();
                Iterator<TableNode> tableNodeIterator = databaseNode.getTableNodeList().iterator();
                while (tableNodeIterator.hasNext()) {
                    DatabaseNodeListInfo.tbNumber++;
                    TableNode tableNode = tableNodeIterator.next();
                    /*sql查询该表所有数据*/
                    st = con.createStatement();
                    String sql1 = "select ";
                    for (int i = 0; i < tableNode.getColumns().size(); ++i) {
                        sql1 += " \"" + tableNode.getColumns().get(i) + "\",";
                    }
                    sql1 = sql1.substring(0, sql1.length() - 1);/*去掉最后一个逗号*/
                    sql1 += " from \"" + oracle2esConfig.getOwner() + "\".\"" + tableNode.getTableName() + "\"";
                    logger.debug("[sql: " + sql1 + "]");
                    rs = st.executeQuery(sql1);
                    while (rs.next()) {
                    /*所有数据+1*/
                        DatabaseNodeListInfo.rowNumber++;
                    /*该库数据+1*/
                        databaseNode.setRowNumber(databaseNode.getRowNumber() + 1);
                        ArrayList<String> row = new ArrayList<String>();
                        List cols = tableNode.getColumns();
                        for (int i = 0; i < cols.size(); ++i) {
                            row.add(rs.getString(cols.get(i).toString()));
                        }
                        tableNode.getRows().add(row);
                    }
                }
            }
        /* 这里有多次连接，需要每次创建新的之前，都close之前的，还是只需在最后close即可*/
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            logger.error("Query oracle error!\n", e);
        }
    }

    @Override
    public void queryAllDataByPage() {
        //从数据库获取数据，插入队列中，需要控制队列大小
        try {
            Connection connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("query oracle ",e);
        }
    }

    @Override
    public void config() {

    }
}
