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
        queryParams.put("id","6950868");
//        queryParams.put("updateTime","2016-08-09 12:02:49 ");
        resultMap = dao.query("df", "TmCusApplyLoan", queryParams);

        for(String key : resultMap.keySet()){
            System.out.println(key+"--->"+resultMap.get(key));
        }
    }

}
