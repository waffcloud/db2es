package com.justplay1994.github.db2es.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Package: com.justplay1994.github.db2es.controller
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 16:52
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 16:52
 * @Update_Description: huangzezhou 补充
 **/
@Api(description = "首页加载swagger ui")
@RestController
public class HomeController {

    @ApiOperation(value = "home", notes = "首页跳转至swagger ui")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
