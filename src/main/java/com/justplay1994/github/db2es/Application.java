package com.justplay1994.github.db2es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @Package: com.justplay1994.github
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 16:48
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 16:48
 * @Update_Description: huangzezhou 补充
 **/
@SpringBootApplication
@ComponentScan
@Component
@EnableScheduling
@EnableCaching
@MapperScan({
        "com.justplay1994.github.db2es.dao"
})
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(Application.class);
    }

    public static void main(String[] args)
    {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {}
}
