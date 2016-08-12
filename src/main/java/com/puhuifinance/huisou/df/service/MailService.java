package com.puhuifinance.huisou.df.service;


import com.alibaba.fastjson.JSONObject;
import com.puhuifinance.huisou.df.util.FileUtils;

import javax.mail.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by puhui on 2016/8/3.
 */
public class MailService {

    public static Store store;

    //创建邮箱连接
    public MailService(String conf) {
        try {
            JSONObject mailConf = FileUtils.getJSONObjectFromJsonFile(conf);
            Properties props = new Properties();
            props.put("mail.transport.protocol", "pop3");
            props.put("mail.pop.port", "993");
            Session session = Session.getInstance(props);
            store = session.getStore("pop3");

            //获取域名或ip,邮箱地址及密码
            String ip = mailConf.getString("ip");
            String email = mailConf.getString("email");
            String password = mailConf.getString("password");

            //连接邮箱
            store.connect(ip, email, password);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //接收邮件
    public List<String> recieve(String fromDate){
        //定义一个list存放数据
        List list = new ArrayList();

        try {
            //获取收件箱
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formate.parse(fromDate);
            Folder folder = store.getFolder("INBOX");
            if (folder == null) {
                throw(new RuntimeException("服务器不可用"));
            }
            //打开收件箱，并将消息内容放入list中
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            int count = messages.length;
            for(int i = count-1;i>1000;i--){
                if(messages[i].getSentDate().getTime()>=date.getTime() && "知识图谱数据报警".equals(messages[i].getSubject())){
                    String messageContexts = messages[i].getContent().toString();
                    String[] messageContext = messageContexts.split("\n");
                    for(int j=0;j<messageContext.length;j++){
                        list.add(messageContext[i]);
                    }
                }
            }
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
