package com.justplay1994.github.db2es.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @Package: com.justplay1994.github.db2es.util
 * @Project: db2es
 * @Creator: huangzezhou
 * @Create_Date: 2018/11/22 15:33
 * @Updater: huangzezhou
 * @Update_Date: 2018/11/22 15:33
 * @Update_Description: huangzezhou 补充
 * @Description: //TODO
 **/
public class PropertiesLoadUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoadUtils.class);

    /**
     * properties文件读取优先级：
     * 1.相对路径 config/xx.properties
     * 2.相对路径 xx.properties
     * 3.类路径 classpath:xx.properties
     * @param propertiesName
     * @return
     */
    public Properties read(String propertiesName) {
        Properties properties = null;
        try {
            File file = new File("config/"+propertiesName);
            if (file.exists()) {
                InputStream inputStreamReader = new FileInputStream(file);
                InputStreamReader in = new InputStreamReader(inputStreamReader, "utf-8");
                properties = new Properties();
                properties.load(in);
                in.close();
                logger.info("Read properties finished: config/" + propertiesName);
            }else{
                file = new File(propertiesName);
                if (file.exists()){
                    InputStream inputStreamReader = new FileInputStream(file);
                    InputStreamReader in = new InputStreamReader(inputStreamReader, "utf-8");
                    properties = new Properties();
                    properties.load(in);
                    in.close();
                    logger.info("Read properties finished: " + propertiesName);
                }else {
                    InputStream stream = getClass().getClassLoader().getResourceAsStream(propertiesName);
                    InputStreamReader in = new InputStreamReader(stream, "utf-8");
                    properties = new Properties();
                    properties.load(in);
                    in.close();
                    logger.info("Read properties finished: classpath: " + propertiesName);
                }
            }

        } catch (Exception e) {
            logger.error("read properties error: "+propertiesName, e);
        }
        return properties;
    }


}
