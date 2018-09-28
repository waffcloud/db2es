package com.justplay1994.github.db2es;
/**********************************************************************
 * Copyright (c) 2018 CETC Company
 * 中电科新型智慧城市研究院有限公司版权所有
 * <p>
 * PROPRIETARY RIGHTS of CETC Company are involved in the
 * subject matter of this material. All manufacturing, reproduction, use,
 * and sales rights pertaining to this subject matter are governed by the
 * license agreement. The recipient of this software implicitly accepts
 * the terms of the license.
 * 本软件文档资料是中电科新型智慧城市研究院有限公司的资产，任何人士阅读和
 * 使用本资料必须获得相应的书面授权，承担保密责任和接受相应的法律约束。
 *************************************************************************/

import com.justplay1994.github.db2es.client.urlConnection.MyURLConnection;
import com.justplay1994.github.db2es.config.Db2esConfig;
import com.justplay1994.github.db2es.service.db2es.Oracle2es;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/28 15:16
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/28 15:16
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyUrlConnectionBulkTest {

    @Autowired
    Db2esConfig db2esConfig;

    @Test
    public void bulkTest() throws IOException {
        String json = "{ \"index\":{ \"_index\": \"tb_road_disease@31project\", \"_type\": \"_doc\", \"_id\": \"AAAUrkAAEAABzdjAAH\"}}\n" +
                "{\"NUMBER\":\"M-S1-001\",\"PHOTO_URL\":\"photo_road_disease/shenzhen_futian_hongliroad/1\",\"REGION_CODE\":\"440304\",\"ROAD\":\"深圳市福田区红荔路\",\"DISEASE_TYPE\":\"脱空\",\"ADDRESS_DESC\":\"距护栏2.3米；过皇岗路口第一个指示牌正下方\",\"COMMUNITY_CODE\":\"440304008006\",\"STREET\":\"华富\",\"UPDATE_TIME\":1526537331000,\"RISK_GRADE\":\"Ⅲ\",\"ROW_ID\":\"AAAUrkAAEAABzdjAAH\",\"STREET_CODE\":\"440304008\",\"PLANE_RANGE\":\"1.8m×1.6m\",\"X\":\"2496143.047\",\"Y\":\"301381.379\",\"location\":{\"lon\":\"114.06932288\",\"lat\":\"22.55102335\"},\"ID\":\"38\",\"CREATE_TIME\":1526609928000,\"UUID\":\"ZGJfMzFwcm9qZWN0OTk3Mjk5NDU1NjA4ODg1MjQ4\",\"DEPTH\":\"1.2m\"}\n";
        System.out.println(json);
        System.out.println(System.getProperty("file.encoding"));
        MyURLConnection c = new MyURLConnection();
        c.request(db2esConfig.getEsUrl()+"_bulk","POST",json);

    }
}
