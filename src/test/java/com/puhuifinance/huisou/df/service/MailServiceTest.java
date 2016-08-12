package com.puhuifinance.huisou.df.service;

import org.junit.Test;

import java.io.IOException;

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
        mailService.recieve("2016-08-09 12:03:00");

    }

}
