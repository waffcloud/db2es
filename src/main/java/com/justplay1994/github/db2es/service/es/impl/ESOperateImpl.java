package com.justplay1994.github.db2es.service.es.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justplay1994.github.db2es.client.HttpClientUtil;
import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.config.MyLogConfig;
import com.justplay1994.github.db2es.dao.TableMapper;
import com.justplay1994.github.db2es.service.db.current.DatabaseNode;
import com.justplay1994.github.db2es.service.db.current.DatabaseNodeListInfo;
import com.justplay1994.github.db2es.service.db.current.TableNode;
import com.justplay1994.github.db2es.service.es.ESOperate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import javax.swing.plaf.basic.BasicTreeUI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Package: com.justplay1994.github.db2es.service.es.impl
 * @Project: db2es
 * @Description: //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:41
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:41
 * @Update_Description: huangzezhou 补充
 **/

@Service
public class ESOperateImpl implements ESOperate {

    static StringBuilder bulkJson = new StringBuilder();   //允许跨库表、跨索引组bulk，head里面已经区别开了
    static int bulkJsonRow = 0;          //当前esBulk对应的数据行数

    private static final Logger logger = LoggerFactory.getLogger(ESOperateImpl.class);

    @Autowired
    Db2esConfig db2esConfig;

    static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TableMapper tableMapper;

    @Autowired
    MyLogConfig myLogConfig;

    List<HashMap> nameAndAddressList;

    /**
     * 给字段起别名
     * @param tbName
     * @param col
     * @param list
     * @return
     */
    private String colAlias(String tbName, String col, List<HashMap> list){
        for (HashMap map: list){
            String tbNameC = (String) map.get("TABLE_NAME");
            String nameCol = (String) map.get("NAME");
            String addressCol = (String) map.get("ADDRESS");
            String loadMapTagLv2 = (String) map.get("LOAD_MAP_TAG_LV2");
            if (tbNameC.equalsIgnoreCase(tbName)) {
                if ((!StringUtils.isEmpty(nameCol)) && nameCol.equalsIgnoreCase(col)) {
                    return "NAME";
                }
                if ((!StringUtils.isEmpty(addressCol)) && addressCol.equalsIgnoreCase(col))
                    return "ADDRESS";
                if ((!StringUtils.isEmpty(loadMapTagLv2)) && loadMapTagLv2.equalsIgnoreCase(col))
                    return "LOAD_MAP_TAG_LV2";
            }
        }
        return col;
    }

    @Override
    public void createMapping() {
        //查询映射表：每张表那个字段是name，那个字段是address
        HashMap map = new HashMap();
        map.put("tbName", "CONTENT_RELATION_MAP");
        List<String> cols = new ArrayList<String>();
        cols.add("TABLE_NAME");
        cols.add("NAME");
        cols.add("ADDRESS");
        cols.add("LOAD_MAP_TAG_LV2");
        map.put("cols", cols);
        nameAndAddressList = tableMapper.queryTable(map);

        /**
         * 创建mapping的线程实例
         */
        class MappingThread implements Runnable {

            String ESUrl = db2esConfig.getEsUrl();

            String indexName;
            String mapping;

            public MappingThread(String indexName, String mapping) {
                this.indexName = indexName;
                this.mapping = mapping;
            }

            public void run() {
                logger.info("creating mapping...");
                /*创建索引映射*/
                String result="";
                try {
//                    result = new MyURLConnection().request(ESUrl + indexName, "PUT", mapping);
                    result = HttpClientUtil.request(ESUrl + indexName, "PUT", mapping);
                    logger.info("mapping finished! indexName: " + indexName);
                } catch (MalformedURLException e) {
                    logger.error("【MappingError1】", e);
                    logger.error("url: " + ESUrl + indexName + "\n " + mapping, e);
                    logger.error(result);
                } catch (ProtocolException e) {
                    logger.error("【MappingError2】", e);
                    logger.error("url: " + ESUrl + indexName + "\n " + mapping, e);
                    logger.error(result);
                } catch (IOException e) {
                    logger.error("【MappingError3】", e);
                    logger.error("url: " + ESUrl + indexName + "\n " + mapping, e);
                    logger.error(result);
                }
            }
        }

        logger.info("begin create es mapping...");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                db2esConfig.getMaxThreadCount(),
                db2esConfig.getMaxThreadCount(),
                100,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(db2esConfig.getMaxThreadCount())     //等待队列
        );
        /*遍历数据结构，构造Mapping*/
        Iterator<DatabaseNode> databaseNodeIt = DatabaseNodeListInfo.databaseNodeList.iterator();
        while (databaseNodeIt.hasNext()) {
            DatabaseNode databaseNode = databaseNodeIt.next();
            Iterator<TableNode> tableNodeIterator = databaseNode.getTableNodeList().iterator();
            while (tableNodeIterator.hasNext()) {

                TableNode tableNode = tableNodeIterator.next();
                //每张表先创建mapping关系，创建location为地理信息点类型
                    /*遍历字段名，给每一个字段增加分词器
                    * 每个属性增加：
                    * {"type":"text",
                	    "analyzer": "ik_max_word",
                	    "search_analyzer": "ik_max_word"}
                    * */
                HashMap properties = new HashMap();
                /*地理信息字段*/
                HashMap geo = new HashMap();
                geo.put("type", "geo_point");
                properties.put("location", geo);
                /*分词字段*/
                HashMap textAnalyzer = new HashMap();
                textAnalyzer.put("type", "text");
                textAnalyzer.put("analyzer", "ik_max_word");
                textAnalyzer.put("search_analyzer", "ik_max_word");
                /*时间字段*/
                HashMap dateTime = new HashMap();
                dateTime.put("type", "date");
                dateTime.put("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
                /*普通字符串字段，避免被自动识别为其他类型*/
                HashMap textType = new HashMap();
                textType.put("type", "text");

                for (int i = 0; i < tableNode.getColumns().size(); ++i) {
                    String col = tableNode.getColumns().get(i);
                    String colA = colAlias(tableNode.getTableName(), col, nameAndAddressList);
                    if (tableNode.getDataType().get(i).equalsIgnoreCase("NVARCHAR2")
                            || tableNode.getDataType().get(i).equalsIgnoreCase("VARCHAR2")) {
                        properties.put(colA, textAnalyzer);
                    } else if (tableNode.getDataType().get(i).equalsIgnoreCase("DATE")) {
                        properties.put(colA, dateTime);
                    } else {
                        properties.put(colA, textType);
                    }
                }
                try {
                    String mapping =
                            " {\n" +
                                    "    \"mappings\": {\n" +
                                    "        \"" + db2esConfig.getIndexType() + "\": {\n" +
                                    "            \"properties\": \n" +
                                    objectMapper.writeValueAsString(properties) +
                                    "            \n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "}";

                    String indexName = indexName(databaseNode.getDbName(), tableNode.getTableName());
                    executor.execute(new Thread(new MappingThread(indexName, mapping)));
                        /*如果当前线程数达到最大值，则阻塞等待*/
                    while (executor.getQueue().size() >= executor.getMaximumPoolSize()) {
                        logger.debug("Thread waite ...Already maxThread. Now Thread nubmer:" + executor.getActiveCount());
//                            logger.debug("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+executor.getQueue().size()+"，已执行完别的任务数目："+executor.getCompletedTaskCount());
                        long time = 100;
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            logger.error("sleep error!", e);
                        }
                    }
                } catch (JsonProcessingException e) {
                    logger.error("json error!\n", e);
                }

            }
        }
        while (executor.getActiveCount() != 0 || executor.getQueue().size() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("sleep error!\n", e);
            }
        }
            /*关闭线程池*/
        executor.shutdown();
        logger.info("Finished to create es mapping!");
    }

    @Override
    public void bulk() {

        /**
         * 1. 循环遍历所有的表
         * 2. 进入每个表的时候，先判断该表所有相关操作是否完成，再判断队列是否完成。
         * 3. 如果没有完成，则进入该表，一直取队列，直到取完（返回null）后，再退出循环，进入下一张表
         */

        class BulkThread implements Runnable {

            String json;
            private String url = db2esConfig.getEsUrl() + "_bulk";
            private String type = "POST";/*必须大写*/
            private String result = "";

            BulkThread(String json) {
                this.json = json;
            }

            @Override
            public void run() {

                try {

//                    result = new MyURLConnection().request(url, type, json);
                    result = HttpClientUtil.request(url, type, json);

                    logger.debug(getRequestFullData());

                    /*201是成功插入，209是失败，*/
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(result.getBytes(), Map.class);
                    if ("true".equals(map.get("errors").toString())) {
                        logger.error("insert error:");
                        printEsError(result);
//                    logger.error(getRequestFullData());//打印整个请求包
                        addNowFailedRowNumber(bulkJsonRow);
                    }
                } catch (MalformedURLException e) {
                    addNowFailedRowNumber(bulkJsonRow);
                    logger.error("【BulkDataError1】", e);
//                    logger.error(getRequestFullData());//打印整个请求包
                } catch (ProtocolException e) {
                    addNowFailedRowNumber(bulkJsonRow);
                    logger.error("【BulkDataError2】", e);
//                    logger.error(getRequestFullData());//打印整个请求包
                } catch (IOException e) {
                    addNowFailedRowNumber(bulkJsonRow);
                    logger.error("【BulkDataError3】", e);
                    logger.error(getRequestFullData());//打印整个请求包
                } finally {
                    printNowRowNumber();/*打印进度条*/
                }
            }

            /*打印进度条*/
            synchronized public void printNowRowNumber() {

                DecimalFormat df = new DecimalFormat("0.00");
                logger.info("query finished: " + df.format(((float) DatabaseNodeListInfo.queryRowNumber / DatabaseNodeListInfo.totalRowNumber) * 100) + "% " + DatabaseNodeListInfo.queryRowNumber + "/" + DatabaseNodeListInfo.totalRowNumber);
                logger.info("has finished: " + df.format(((float) DatabaseNodeListInfo.isFinishedCount / DatabaseNodeListInfo.totalRowNumber) * 100) + "% " + DatabaseNodeListInfo.isFinishedCount + "/" + DatabaseNodeListInfo.totalRowNumber);
                logger.info("has error: " + df.format(((float) DatabaseNodeListInfo.failCount / DatabaseNodeListInfo.totalRowNumber) * 100) + "% " + DatabaseNodeListInfo.failCount + "/" + DatabaseNodeListInfo.totalRowNumber);
            }

            /*获取完整请求信息，包括数据体*/
            public String getRequestFullData() {
                return "[request] url:" + url + ",type:" + type + ",body:" + json + "" +
                        "\n[result]: " + result;
            }

            /*打印ES关键错误信息*/
            public void printEsError(String result){
                try {
                    ArrayList<LinkedHashMap> items = (ArrayList) objectMapper.readValue(result, HashMap.class).get("items");
                    for (LinkedHashMap item : items){
                        try {
                            LinkedHashMap index = (LinkedHashMap) item.get("index");
                            String _index = (String) index.get("_index");
                            String _id = (String) index.get("_id");
                            LinkedHashMap error = (LinkedHashMap) index.get("error");
                            LinkedHashMap cause_by = (LinkedHashMap) error.get("caused_by");
                            String reason = (String) cause_by.get("reason");
                            logger.error("【index: " + _index + ",error: " + reason + ",id="+_id+"】");
                        } catch (Exception e){
                            //没有解析到error则跳过
                        }
                    }
                } catch (IOException e) {
                    logger.error("Json format error!\n",e);
                }
            }


            synchronized void addNowFailedRowNumber(long addNumber) {
                DatabaseNodeListInfo.failCount += addNumber;
            }

        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(db2esConfig.getMaxThreadCount(), db2esConfig.getMaxThreadCount(), 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(db2esConfig.getMaxThreadCount()));


        while (true) {//循环遍历，直到所有的都结束
            boolean allDbFinished = true;
            for (DatabaseNode db : DatabaseNodeListInfo.databaseNodeList) {//遍历所有库，跳过已完成的库
                if (db.getTableFinishedCount() != db.getTableNodeList().size())
                    allDbFinished = false;
            }
            if (allDbFinished)
                break;
            for (DatabaseNode db : DatabaseNodeListInfo.databaseNodeList) {//遍历所有库
                if (db.getTableFinishedCount() == db.getTableNodeList().size()) {//该库下的所有数据都已使用完毕，不需要再进入。该库所有bulk已经收集完毕，基本请求完毕，可能会有最有一个不足bulk size的还没有请求。
                    continue;
                }
                for (TableNode tb : db.getTableNodeList()) {//遍历所有表
                    if (tb.isDoEsBulk()) //该表收集bulk已经完成，不需要进入该表
                        continue;
                    if (tb.getEsBulks().size() > 0) {
                        while (true) {
                            String temp = tb.getEsBulks().poll();
                            if (tb.isGeneratorEsBulkFinished() && temp == null) {//先判断bulk是否完成，bulk完成说明不会再出现入队操作，再判断队列如果为空，说明该表已经完成了。
                                tb.setDoEsBulk(true);//该表完成bulk的所有搜集工作，该表不需要再次进入。
                                db.setTableFinishedCount(db.getTableFinishedCount() + 1);//已完成的表的数量+1
                                break;
                            } else if (temp == null) { //如果bulk没有完成，只是当前队列为空，只是说明当前表没有完成，只是生产不及了。临时退出该表，后续还会进来。
                                break;
                            }
                            DatabaseNodeListInfo.isFinishedCount++;//已完成的行数增加
                            bulkJsonRow++; //bulk对应的行数增加
                            bulkJson.append(temp);
                            /*等待请求body满一个数据块大小，便开始执行请求*/
                            if (bulkJson.length() >= db2esConfig.getEsBulkSize() * 1024 * 1024) {
                                executor.execute(new BulkThread(bulkJson.toString()));
                                bulkJson.delete(0, bulkJson.length());/*清空请求数据体*/
                                bulkJsonRow = 0;//同时情况bulk请求对应的行数
                                /*如果当前线程数达到最大值，则阻塞等待*/
                                while (executor.getQueue().size() >= executor.getMaximumPoolSize()) {
                                    logger.debug("Already maxThread. Now Thread nubmer:" + executor.getActiveCount());
                                    long time = 200;
                                    try {
                                        Thread.sleep(time);
                                    } catch (InterruptedException e) {
                                        logger.error("sleep error!", e);
                                    }
                                }
                            }
                        }
                    }else {
                        //解决BUG：遇到一种情况，队列为空了，bulkGenerator标志位也为true，但是该表bulk导入的标志位还是没有完成。
                        //可能的问题：bulkGenerator刚好产生完数据，还没有更改标志位的时候，bulk就消费了，导致队列为空，但是bulk还没有完成。然后空队列又不会进来，所有死循环。
                        if (tb.isGeneratorEsBulkFinished() && tb.getEsBulks().size() == 0){//先判断bulk是否完成，bulk完成说明不会再出现入队操作，再判断队列如果为空，说明该表已经完成了。
                            tb.setDoEsBulk(true);//该表完成bulk的所有搜集工作，该表不需要再次进入。
                            db.setTableFinishedCount(db.getTableFinishedCount() + 1);//已完成的表的数量+1
                            continue;
                        }
                    }
                }
            }
        }
        //BUG:这里会被调用2次
        /*将剩余不足一个数据块的数据单独发起请求*/
        if (bulkJson.length() > 0)
            executor.execute(new BulkThread(bulkJson.toString()));
        /*阻塞等待线程结束*/
        while (executor.getActiveCount() != 0 || executor.getQueue().size() != 0) {
//            logger.info("wait thread number : " + executor.getActiveCount());
            long time = 1000;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                logger.error("sleep error!", e);
            }
        }
    }

    public Thread createBulkThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                bulk();
            }
        });
    }

    @Override
    public void deleteAllConflict() {
        try {

            logger.info("delete already exist and conflict index ...");

            String url = "";
            if (DatabaseNodeListInfo.databaseNodeList == null || DatabaseNodeListInfo.databaseNodeList.size() <= 0) {
                return;
            }
            Iterator<DatabaseNode> databaseNodeIt = DatabaseNodeListInfo.databaseNodeList.iterator();
            while (databaseNodeIt.hasNext()) {
                DatabaseNode databaseNode = databaseNodeIt.next();
                Iterator<TableNode> tableNodeIterator = databaseNode.getTableNodeList().iterator();
                while (tableNodeIterator.hasNext()) {
                    TableNode tableNode = tableNodeIterator.next();

                    /*逐个删除*/
                    url = indexName(databaseNode.getDbName(), tableNode.getTableName());
                    try {
//                        new MyURLConnection().request(db2esConfig.getEsUrl() + url, "DELETE", "");
                        HttpClientUtil.request(db2esConfig.getEsUrl() + url, "DELETE", "");
                        logger.info("delete success: " + url);
                    } catch (MalformedURLException e) {
                        logger.error("delete index error: " + url, e);
                    } catch (FileNotFoundException e) {
                        // 删除不存在的索引，会报此错误，不打印，不抛出该异常
                        logger.error("delete error: " + url);
                    } catch (IOException e) {
                        logger.error("delete index error", e);
                    }
                }
            }
            logger.info("delete finished!");
        } catch (Exception e) {
            //删除索引失败，不报错，因为第一次导入大概率不存在，会有非常多报错日志，掩盖掉重要日志。
        }
    }


    /**
     * 索引名与库表名的关系映射，ES索引名必须是小写
     *
     * @param dbName
     * @param tbName
     * @return 索引名称
     */
    public String indexName(String dbName, String tbName) {
        if (!db2esConfig.getIndexDB().equals(""))
            return (tbName + "@" + db2esConfig.getIndexDB()).toLowerCase();
        return (tbName + "@" + dbName).toLowerCase();
    }



    /**
     * 每张表启动一个esBulk生成器,多线程并发处理。
     */
    public void esBulkGenerator() {
        class Table2esBulk implements Runnable {

            TableNode tableNode;
            String dbName;
            String json; //一条esBulk语句

            public Table2esBulk() {

            }

            public Table2esBulk(String dbName, TableNode tableNode) {
                this.dbName = dbName;
                this.tableNode = tableNode;
            }

            @Override
            public void run() {

                /**
                 * 组成部分为：请求head和请求body
                 * 1.构建head，包括：判断主键字段、索引名
                 * 2.根据字段类型构建body需要：判断经纬度字段、判断时间字段
                 *
                 */
                while (true) {
                    if (tableNode.isQueryDataFinished() && tableNode.getRows().size() == 0)
                        break;
                    List<String> row = null;
                    try {
                        row = tableNode.getRows().poll(db2esConfig.getQueueWaitTime(), TimeUnit.MILLISECONDS);
                        if (row == null) continue;

                        try {
                            //将一行数据，转化为bulk请求，如果成功则入队
                            if (bulkGeneratorByRow(row))
                                tableNode.getEsBulks().offer(json.toString(), db2esConfig.getQueueWaitTime(), TimeUnit.MILLISECONDS);//入队一个bulk
                            else {
                                //如果该行数据错误，则已完成数据行数+1，失败行数+1
                                DatabaseNodeListInfo.isFinishedCount++;
                                DatabaseNodeListInfo.failCount++;
                                if (myLogConfig.isLocation())
                                    logger.error("Row data error, lat or lon is null! [ dbName=" + dbName + ", tbName=" + tableNode.getTableName() + ",ROWID="+row.get(0)+"]\n");
                            }
                        } catch (InterruptedException e) {
                            logger.error("Offer bulk queue error! [ dbName=" + dbName + ", tbName=" + tableNode.getTableName() + "\n", e);
                        }
                    } catch (InterruptedException e) {
                        logger.error("Poll row queue error! [ dbName=" + dbName + ", tbName=" + tableNode.getTableName() + "\n", e);
                    } catch (Exception e){
                        logger.error("Bulk generator error! [ dbName=" + dbName + ", tbName=" + tableNode.getTableName() + "\n",e);
                    }

                }
                tableNode.setGeneratorEsBulkFinished(true);
            }

            /**
             * 根据字段类型构建head，包括：判断主键字段、判断经纬度字段、判断时间字段
             */
            private boolean bulkGeneratorByRow(List<String> row) {
                Map map = new HashMap();/*数据*/
                HashMap location = new HashMap();
                String lat="";
                String lon="";
                for (int i = 0; i < tableNode.getColumns().size(); ++i) {
                    if (row.get(i) != null && !row.get(i).equals("")) { //去除空数据，节省es搜索空间
                        String col = tableNode.getColumns().get(i);
                        String colA = colAlias(tableNode.getTableName(), col, nameAndAddressList);
                        if (col.equals(db2esConfig.getLatColumn())) {
                            location.put("lat", row.get(i));
                            lat= row.get(i);
                        } else if (col.equals(db2esConfig.getLonColumn())) {
                            location.put("lon", row.get(i));
                            lon= row.get(i);
                        } else if (tableNode.getDataType().get(i).equals("DATE")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date date = null;
                            try {
                                date = df.parse(row.get(i));
                            } catch (ParseException e) {
                                logger.error("Date format error!\n", e);
                            }
                            map.put(col, date);
                        } else {
                            map.put(colA, row.get(i));
                        }
                    }
                }
                map.put("location", location);
                try {
                    //经纬度同时存在时，才导入
                    if (isValidGeo(lat, lon)) {
                        json = "";
                        /*构建head头*/
                        json += "{ \"index\":{ \"_index\": \"" + indexName(dbName, tableNode.getTableName()) + "\", \"_type\": \"" + db2esConfig.getIndexType() + "\", \"_id\": \"" + row.get(0) + "\"}}\n";
                        /*构建body*/
                        json += objectMapper.writeValueAsString(map) + "\n";
                        return true;
                    }
                } catch (JsonProcessingException e) {
                    logger.error("To json error when generator bulk body!\n", e);
                    return false;
                }
                return false;
            }

        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(db2esConfig.getMaxThreadCount(), db2esConfig.getMaxThreadCount(), 100, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(db2esConfig.getMaxThreadCount()));
        for (DatabaseNode db : DatabaseNodeListInfo.databaseNodeList) {
            for (TableNode tb : db.getTableNodeList()) {
                executor.execute(new Table2esBulk(db.getDbName(), tb));
                /*如果当前线程数达到最大值，则阻塞等待*/
                while (executor.getQueue().size() >= executor.getMaximumPoolSize()) {
                    logger.debug("Already maxThread. Now Thread number:" + executor.getActiveCount());
                    long time = 200;
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        logger.error("sleep error!", e);
                    }
                }
            }
        }
        /*阻塞等待线程结束*/
        while (executor.getActiveCount() != 0 || executor.getQueue().size() != 0) {
//            logger.info("wait thread number : " + executor.getActiveCount());
            long time = 1000;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                logger.error("sleep error!", e);
            }
        }
        /*关闭线程池*/
        executor.shutdown();
    }

    public Thread createEsBulkGeneratorTread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                esBulkGenerator();
            }
        });
    }


    /**
     * 判断经纬度字段是否有效
     * @param lat_str
     * @param lon_str
     * @return
     */
    public boolean isValidGeo(String lat_str, String lon_str){
        if (lat_str == null || lon_str == null || "".equals(lat_str) || "".equals(lon_str))return false;
        try {
            Double lat = Double.parseDouble(lat_str);
            Double lon = Double.parseDouble(lon_str);
            if (-90 <= lat && lat <= 90 && -180<=lon && lon <= 180){
                return true;
            }
        }catch (Exception e){
            logger.error("IsValidGeo error! lat="+lat_str+", lon="+lon_str+"\n",e);
            return false;
        }
        return false;
    }
}


