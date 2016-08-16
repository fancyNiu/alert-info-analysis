package com.puhuifinance.huisou.df.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by puhui on 2016/8/11.
 */
public class MessageUtilTest {

    @Test
    public void parseMessageTest(){
        List<Map<String,String>> messageInfoList = new ArrayList<Map<String,String>>();
        List list = new ArrayList();
        Map<String,String> map = new HashMap<String, String>();
        list.add("com.puhui.df.model.mysql.puhui.RiskCompany-->com.puhui.df.query.mongo.QueryRiskInfo执行时间:2016-08-09 12:03:14 data : id = 549831, updateTime = 2016-08-09 12:01:13 mongo 没有数据");
//        list.add("com.puhui.df.query.mongo.QueryTmCusApplyLoan执行时间:2016-08-09 12:04:21 data : id6950868, updateTime2016-08-09 12:02:49 mongo 没有数据");
//        list.add("com.puhui.df.model.mysql.puhui.Address-->com.puhui.df.query.mongo.QueryAddressInfo执行时间:2016-08-09 12:04:22address data : id1570963, updateTime2016-08-09 12:03:11 AddressInfo mongo 没有数据");
//        list.add("com.puhui.df.model.mysql.puhui.Address-->com.puhui.df.query.mongo.QueryAddressInfo执行时间:2016-08-09 12:03:17address data : id1570949, updateTime2016-08-09 12:01:30 AddressInfo mongo 没有数据");
//        list.add("com.puhui.df.query.mongo.QueryLendRepayRecord执行时间:2016-08-09 12:03:07 data : id3124420, updateTime2016-08-09 12:01:00 mongo 没有数据");
        messageInfoList = MessageUtil.parseMessage(list);

        for(int i=0;i<messageInfoList.size();i++){
            map = null;
            map = (Map<String,String>)messageInfoList.get(i);
            for (String key : map.keySet()){
                System.out.println(key+"--->"+map.get(key));
            }
        }
    }


}
