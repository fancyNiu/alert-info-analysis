package com.puhuifinance.huisou.df.service;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by puhui on 2016/8/3.
 */
public class MailServiceTest {

    @Test
    public void mailServiceTest() {
        String confPath = null;
        confPath = MailServiceTest.class.getClassLoader().getResource("./conf/mail.json").getPath();
        System.out.println(confPath);
        MailService mailService = new MailService(confPath);
        List<String> list = mailService.recieve("2016-08-09 12:03:00");
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i).toString());
        }

    }

}
