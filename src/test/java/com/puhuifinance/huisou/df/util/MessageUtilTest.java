package com.puhuifinance.huisou.df.util;

import org.junit.Test;

import javax.mail.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by puhui on 2016/8/11.
 */
public class MessageUtilTest {

    @Test
    public void parseMessageTest(){
        List list = new ArrayList();
        list.add("com.puhui.df.query.mongo.QueryLendRepayRecord执行时间:2016-08-09 12:03:07 data : id3124420, updateTime2016-08-09 12:01:00 mongo 没有数据");
        MessageUtil.parseMessage(list);

    }


}
