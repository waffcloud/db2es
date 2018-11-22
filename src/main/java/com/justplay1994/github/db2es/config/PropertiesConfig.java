package com.justplay1994.github.db2es.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justplay1994.github.db2es.util.PropertiesLoadUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * @Package: com.justplay1994.github.db2es.config
 * @Project: db2es
 * @Creator: huangzezhou
 * @Create_Date: 2018/11/22 15:48
 * @Updater: huangzezhou
 * @Update_Date: 2018/11/22 15:48
 * @Update_Description: huangzezhou 补充
 * @Description: //TODO
 **/
@Configuration
public class PropertiesConfig {

    @Bean
    public LoadMap setLoadMap() throws IOException {
        Properties properties = new PropertiesLoadUtils().read("loadmap.properties");
        ObjectMapper objectMapper = new ObjectMapper();
        LoadMap loadMap = objectMapper.readValue(objectMapper.writeValueAsString(properties), LoadMap.class);
        return loadMap;
    }
}
