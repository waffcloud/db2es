/**
 *
 * Created by HZZ on 2018/4/19.
 *
 */
package com.justplay1994.github.db2es.service.db.current;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by HZZ on 2018/4/19.
 * 数据库列表，存放所有的数据库
 */
public class DatabaseNodeListInfo {
    public static List<DatabaseNode> databaseNodeList;/*所有数据*/
    public static int dbNumber=0;/*数据库总数量*/
    public static int tbNumber = 0;/*表总数量*/
    public static long rowNumber=0;/*总数据量*/
}
