package com.justplay1994.github.db2es.config;
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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Package: com.justplay1994.github.db2es.config
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/26 14:13
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/26 14:13
 * @Update_Description: huangzezhou 补充
 **/
@Component
@ConfigurationProperties(prefix = "db2es")
public class Db2esConfig {

    private String esUrl;

    private int maxThreadCount;

    private String indexType;

    private String indexDB;

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIndexDB() {
        return indexDB;
    }

    public void setIndexDB(String indexDB) {
        this.indexDB = indexDB;
    }

    public String getEsUrl() {
        return esUrl;
    }

    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public void setEsUrl(String esUrl) {
        this.esUrl = esUrl;
    }
}
