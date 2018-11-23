package com.justplay1994.github.db2es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Package: com.justplay1994.github.db2es.config
 * @Project: db2es
 * @Creator: huangzezhou
 * @Create_Date: 2018/11/23 10:38
 * @Updater: huangzezhou
 * @Update_Date: 2018/11/23 10:38
 * @Update_Description: huangzezhou 补充
 * @Description: //TODO
 **/
@Component
@ConfigurationProperties(prefix = "mylog")
@Data
public class MyLogConfig {

    boolean location;
}
