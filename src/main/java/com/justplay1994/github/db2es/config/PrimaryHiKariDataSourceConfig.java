/**
 *
 * Created by HZZ on 2017/11/14.
 *
 */
package com.justplay1994.github.db2es.config;


import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 工程包名:    com.cetc.cloud.command.provider.config
 * 项目名称:    db-statement-plugin
 * 创建描述:    zhangliang 补充
 * Creator:     zhangliang
 * Create_Date: 2017/10/10
 * Updater:     zhangliang
 * Update_Date：2017/10/10
 * 更新描述:    zhangliang 补充
 **/
@Configuration
//@MapperScan(basePackages = "com.cetccity.operationcenter.webframework.web.dao", sqlSessionTemplateRef  = "sqlSessionTemplatePrimary")
public class PrimaryHiKariDataSourceConfig {

    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.primary-datasource")
    public DataSource primaryDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        System.out.println(dataSource);
        return dataSource;
    }

}
