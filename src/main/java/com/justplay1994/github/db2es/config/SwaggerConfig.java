package com.justplay1994.github.db2es.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 工程包名:    com.cetc.cloud.db_statement.provider.config
 * 项目名称:    db-statement
 * 创建描述:    zhangliang 补充
 * Creator:     zhangliang
 * Create_Date: 2017/9/25
 * Updater:     zhangliang
 * Update_Date：2017/10/09
 * 更新描述:    zhangliang 补充
 **/

@EnableWebMvc
@EnableSwagger2
@Configuration
public class SwaggerConfig extends WebMvcConfigurerAdapter implements SwaggerConfigInterfaces{


    /**
     * Swagger框架的自定义配置
     * */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

//        registry.addResourceHandler("login.html")
//                .addResourceLocations("classpath:/static/");


        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Bean
    public Docket createRestApi()
    {
        Docket curSwaggerConfigDocket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(SwaggerConfigInterfaces.SERVICE_GROUP_NAME_STR)                                             /** set SwaggerUI current project(Service) name */
                .useDefaultResponseMessages(false)                                             /** refuse the default response mode */
                .forCodeGeneration(true)                                                       /** set good type for API Document code */
                .select()                                                                      /** set the path where build the document */
                .apis(RequestHandlerSelectors.basePackage(SwaggerConfigInterfaces.SWAGGER_SCAN_BASE_PACKAGE_PATH))     /** set the path for the scanning of API,default as apis(RequestHandlerSelectors.any()) */
                .paths(PathSelectors.any())                                                    /** monitoring the api state */
                .build()
                .apiInfo(testApiInfo());
        return curSwaggerConfigDocket;
    }

    private ApiInfo testApiInfo() {
        return new ApiInfoBuilder()
                .title(SwaggerConfigInterfaces.SERVICE_GROUP_NAME_STR)
                .description(SwaggerConfigInterfaces.SERVICE_DESCRIPTION_STR)
                .version(SwaggerConfigInterfaces.SERVICE_FRAMEWORK_VERSION_STR)
                .termsOfServiceUrl(SwaggerConfigInterfaces.SERVICE_CONTACT_USER_URL_STR)
                .contact(new Contact(SwaggerConfigInterfaces.SERVICE_CONTACT_USERNAME_STR, SwaggerConfigInterfaces.SERVICE_CONTACT_USER_URL_STR, SwaggerConfigInterfaces.SERVICE_CONTACT_USER_MAIL_STR))
                .license(SwaggerConfigInterfaces.SERVICE_LICENSE_STR)
                .licenseUrl(SwaggerConfigInterfaces.SERVICE_LICENSE_URL)
                .build();
    }
}
