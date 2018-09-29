package com.justplay1994.github.db2es;

import com.justplay1994.github.db2es.service.db.current.DatabaseNode;
import com.justplay1994.github.db2es.service.db.current.DatabaseNodeListInfo;
import com.justplay1994.github.db2es.service.db.current.TableNode;
import com.justplay1994.github.db2es.service.db.operate.OracleOperate;
import com.justplay1994.github.db2es.service.db.operate.impl.OracleOperateImpl;
import com.justplay1994.github.db2es.service.es.ESOperate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Package: com.justplay1994.github.db2es
 * @Project: db2es
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/9/20 11:46
 * @Updater: huangzezhou
 * @Update_Date: 2018/9/20 11:46
 * @Update_Description: huangzezhou 补充
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OracleOperateTest {

    @Autowired
    OracleOperate oracleOperate;

    @Autowired
    ESOperate esOperate;

    @Test
    public void queryAllStructureTest(){
        oracleOperate.queryAllStructure();
        System.out.print("");
    }

    @Test
    public void queryAllDataByPageTest(){
        oracleOperate.queryAllStructure();//请求所有数据结构
        oracleOperate.queryAllDataByPage();//分页请求数据，插入数据队列，数据队列生产者
        List<DatabaseNode> databaseNodeList = DatabaseNodeListInfo.databaseNodeList;

        System.out.println("\n===================================queryAllDataByPageTest start");
        for (DatabaseNode db : databaseNodeList){
            for (TableNode tb : db.getTableNodeList()){
                System.out.println("[dbName="+db.getDbName()+", tbName="+tb.getTableName()+", rowNumber="+tb.getRowNumber()+", rowSize="+tb.getRows().size()+"]");
            }
        }
        System.out.println("===================================queryAllDataByPageTest end\n");

    }

    @Test
    public void deleteTableWithoutGeoTest(){
        oracleOperate.queryAllStructure();
        OracleOperateImpl o = new OracleOperateImpl();
        o.deleteTableWithoutGeo();
        System.out.println();
    }

    @Test
    public void collectionRemoveTest(){
        List<List<Integer>> lists = new ArrayList<List<Integer>>();
        List<Integer> list = new ArrayList<Integer>();
        list.add(10);list.add(20);list.add(3);list.add(4);
        lists.add(list);
        lists.add(list);
        lists.add(list);
        Iterator<List<Integer>> its = lists.iterator();
        while (its.hasNext()){
            Iterator it = its.next().iterator();
            while (it.hasNext()){
                if(it.next().equals(20)){
                    it.remove();
                    its.remove();
                }
            }
        }
        System.out.println();
    }



}
