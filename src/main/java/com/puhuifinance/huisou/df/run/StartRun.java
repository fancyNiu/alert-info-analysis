package com.puhuifinance.huisou.df.run;

import com.puhuifinance.huisou.df.service.MailService;
import com.puhuifinance.huisou.df.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by puhui on 2016/8/15.
 */
public class StartRun {

    private  static final Logger LOG = LoggerFactory.getLogger(StartRun.class);

    public static void main(String[] args) {

        //加载邮箱配置文件
        String mailConfPath = MailService.class.getClassLoader().getResource("./conf/mail.json").getPath();

        //连接邮箱，获取报警邮件
        MailService mailService = new MailService(mailConfPath);
        List<String> list = mailService.recieve("2016-08-09 12:03:00");

        //对报警邮件进行解析
        List<Map<String,String>> messageInfoList = new ArrayList<Map<String,String>>();
        messageInfoList = MessageUtil.parseMessage(list);

        //存入excel

    }

}
