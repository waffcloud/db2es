package com.justplay1994.github.db2es.service.es.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justplay1994.github.db2es.client.urlConnection.MyURLConnection;
import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.config.Oracle2esConfig;
import com.justplay1994.github.db2es.service.db.current.DatabaseNode;
import com.justplay1994.github.db2es.service.db.current.DatabaseNodeListInfo;
import com.justplay1994.github.db2es.service.db.current.TableNode;
import com.justplay1994.github.db2es.service.es.ESOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Package: com.justplay1994.github.db2es.service.es.impl
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:41
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:41
 * @Update_Description: huangzezhou 补充
 **/

@Service
public class ESOperateImpl implements ESOperate {

    private static final Logger logger = LoggerFactory.getLogger(ESOperateImpl.class);

    @Autowired
    Db2esConfig db2esConfig;

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void createMapping() {
        logger.info("begin create es mapping...");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0,
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
                geo.put("type","geo_point");
                properties.put("location",geo);
                    /*分词字段*/
                HashMap textAnalyzer = new HashMap();
                textAnalyzer.put("type","text");
                textAnalyzer.put("analyzer","ik_max_word");
                textAnalyzer.put("search_analyzer","ik_max_word");
                    /*时间字段*/
                HashMap dateTime = new HashMap();
                dateTime.put("type","date");
                dateTime.put("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
                    /*普通字符串字段，避免被自动识别为其他类型*/
                HashMap textType = new HashMap();
                textType.put("type","text");

                for(int i = 0; i < tableNode.getColumns().size(); ++i){
                    if (tableNode.getDataType().get(i).equalsIgnoreCase("NVARCHAR2")) {
                        properties.put(tableNode.getColumns().get(i).toLowerCase(), textAnalyzer);
                    }else {
                        properties.put(tableNode.getColumns().get(i).toLowerCase(), textType);
                    }
                }
                try {
                    String mapping =
                            " {\n" +
                                    "    \"mappings\": {\n" +
                                    "        \""+db2esConfig.getIndexType()+"\": {\n" +
                                    "            \"properties\": \n" +
                                    objectMapper.writeValueAsString(properties) +
                                    "            \n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "}";

                    String indexName = indexName(databaseNode.getDbName(), tableNode.getTableName());
                    executor.execute(new Thread(new MappingThread(indexName,mapping)));
                        /*如果当前线程数达到最大值，则阻塞等待*/
                    while(executor.getQueue().size()>=executor.getMaximumPoolSize()){
                        logger.debug("Thread waite ...Already maxThread. Now Thread nubmer:"+executor.getActiveCount());
//                            logger.debug("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+executor.getQueue().size()+"，已执行完别的任务数目："+executor.getCompletedTaskCount());
                        long time = 100;
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            logger.error("sleep error!",e);
                        }
                    }
                } catch (JsonProcessingException e) {
                    logger.error("json error!\n",e);
                }

            }
        }
        while(executor.getActiveCount()!=0 || executor.getQueue().size()!=0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("sleep error!\n",e);
            }
        }
            /*关闭线程池*/
        executor.shutdown();
        logger.info("Finished to create es mapping!");
    }

    @Override
    public void bulk() {

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
                    new MyURLConnection().request(db2esConfig.getEsUrl() + url, "DELETE", "");
                    logger.info("delete success: " + url);
                } catch (MalformedURLException e) {
                    logger.error("delete index error: " + url, e);
                } catch (IOException e) {
                    logger.error("delete index error", e);
                }
            }
        }
        logger.info("delete finished!");
        }catch (Exception e){
            //删除索引失败，不报错，因为第一次导入大概率不存在，会有非常多报错日志，掩盖掉重要日志。
        }
    }

    @Override
    public void config() {

    }

    /**
     * 索引名与库表名的关系映射
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
     * 创建mapping的线程实例
     */
    class MappingThread implements Runnable{

        String ESUrl = db2esConfig.getEsUrl();

        String indexName;
        String mapping;

        public MappingThread(String indexName, String mapping){
            this.indexName = indexName;
            this.mapping = mapping;
        }

        public void createMapping(){
            logger.info("creating mapping...");

        /*创建索引映射*/

            try {
                new MyURLConnection().request(ESUrl + indexName,"PUT",mapping);
                logger.info("mapping finished! indexName: "+ indexName);
            } catch (MalformedURLException e) {
                logger.error("【MappingError】", e);
                logger.error("url: "+ESUrl+indexName+"\n "+ mapping);
            } catch (ProtocolException e) {
                logger.error("【MappingError】", e);
                logger.error("url: "+ESUrl+indexName+"\n "+ mapping);
            } catch (IOException e) {
                logger.error("【MappingError】", e);
                logger.error("url: "+ESUrl+indexName+"\n "+ mapping);
            }
        }

        public void run() {
            createMapping();
        }
    }
}


