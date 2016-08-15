package com.puhuifinance.huisou.df.db.dao;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by puhui on 2016/8/15.
 */
public class MongoDBDAOTest {

    @Test
    public void query(){
        MongoDBDAO dao = new MongoDBDAO();
        Map<String,String> queryParams = new HashMap<String,String>();
        List<String> resultList = new ArrayList<String>();
        Map<String,String> resultMap = new HashMap<String, String>();
        queryParams.put("id","34");
        queryParams.put("updateTime","2015-04-15 22:03:00");
        resultList.add("updateTime");
        resultList.add("transfer_date");
        resultMap = dao.query("df", "ApplyBasicInfo", queryParams, resultList);

        for(String key : resultMap.keySet()){
            System.out.println(key+"--->"+resultMap.get(key));
        }
    }

}
