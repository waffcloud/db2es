package com.justplay1994.github.db2es.util;

import org.junit.Test;

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

    public Properties read(String propertiesName) {
        Properties properties = null;
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(propertiesName);
            if (stream != null) {
                InputStreamReader in = new InputStreamReader(stream, "utf-8");
                properties = new Properties();
                properties.load(in);
            } else {
                File file = new File("loadmap.properties");
                InputStream inputStreamReader = new FileInputStream(file);
                InputStreamReader in = new InputStreamReader(inputStreamReader, "utf-8");
                properties = new Properties();
                properties.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }


}
