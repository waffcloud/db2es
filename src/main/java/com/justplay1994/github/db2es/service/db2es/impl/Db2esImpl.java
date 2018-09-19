package com.justplay1994.github.db2es.service.db2es.impl;


import com.justplay1994.github.db2es.service.db2es.Oracle2es;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


/**
 * @Package: com.justplay1994.github.db2es.service.db2es.impl
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/19 17:50
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/19 17:50
 * @Update_Description: huangzezhou 补充
 **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Service
public class Db2esImpl extends Oracle2es {

    @Autowired
    Oracle2es oracle2es;

//    @Test
    @Override
    public void transfer() {
        System.out.println();
    }
}
