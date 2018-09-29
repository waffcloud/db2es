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
    public static long totalRowNumber=0;/*已导入数据行数*/

    public static long queryRowNumber=0;/*已导入数据行数*/

    public static int isFinishedCount = 0; //已完成的行数
    public static int failCount = 0;      //失败的行数
}
