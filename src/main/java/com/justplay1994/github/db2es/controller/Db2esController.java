package com.justplay1994.github.db2es.controller;

import com.justplay1994.github.db2es.service.db2es.Oracle2es;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Package: com.justplay1994.github.db2es.controller
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/29 16:41
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/29 16:41
 * @Update_Description: huangzezhou 补充
 **/
@Api(description = "将数据库数据同步至es中")
@RestController
public class Db2esController {

    @Autowired
    Oracle2es oracle2es;

    @ApiOperation(value = "oracle2es", notes = "oracle数据同步至es中，必须包含经纬度字段，es用于geo查询")
    @RequestMapping(value = "/oracle2es", method = RequestMethod.GET)
    public String oracle2es() throws IOException {
        return oracle2es.transfer();
    }
}
