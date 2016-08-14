package com.puhuifinance.huisou.df.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by puhui on 2016/8/11.
 */
public class MessageUtil {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MessageUtil.class);
    public static Map<String, String> parseMessage(List<String> list) {
        //创建最终返回的map
        Map<String, String> map = new HashMap<String, String>();
        try {
            //加载配置文件
            LOG.info("load the config file.....");
            String regexConfPath = MessageUtil.class.getClassLoader().getResource("./conf/regex.json").getPath();
            JSONObject regexJson = FileUtils.getJSONObjectFromJsonFile(regexConfPath);

            String columnRelationsConfPath = MessageUtil.class.getClassLoader().getResource("./conf/column_relations.json").getPath();
            JSONObject columnRelationsJson = FileUtils.getJSONObjectFromJsonFile(columnRelationsConfPath);
            Map<String, String> mysqlColumnRelationsMap = (Map<String, String>) columnRelationsJson.get("mysql");
            Map<String, String> mongoColumnRelationsMap = (Map<String, String>) columnRelationsJson.get("mongo");

            //遍历，对每一条消息进行处理
            LOG.info("handle the message");
            Map<String, String> regexMap = null;
            String message = null;
            String regex = null;
            Pattern pattern = null;
            Matcher matcher =null;
            for (int i = 0; i < list.size(); i++) {
                //解析基本信息，并将数据分成neo4j和mongo两个部分来处理
                LOG.debug("the message is :");
                message = list.get(i);
                map.put("报警信息", message);
                map.put("报警字段1","applyNo");
                //如果包含neo4j，证明是neo4j入库慢，则调用neo4j的配置文件进行分析，否则是mongo入库慢
                if (message.contains("neo4j")) {
                    map.put("引起报警服务", "neo4j");
                    regexMap = (Map<String, String>) regexJson.get("neo4j");
                    regex = "com.puhui.df.model.mysql.(.*?)执行时间";
                } else {
                    map.put("引起报警服务", "mongo");
                    map.put("报警字段1","id");
                    map.put("报警字段2","updateTime");
                    regexMap = (Map<String, String>) regexJson.get("mongo");
                    if(message.startsWith("com.puhui.df.model.mysql")){
                        regex = "com.puhui.df.model.mysql.(.*?)--";
                    }else if(message.startsWith("com.puhui.df.query.mongo")){
                        regex = "com.puhui.df.query.mongo.Query(.*?)执行时间";
                    }else {
                        regex = "\\d(.*?) data";
                    }
                }
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(message);
                if(matcher.find()){
                    if(mysqlColumnRelationsMap.containsKey(matcher.group(1))){
                        map.put("mysql中的表名", mysqlColumnRelationsMap.get(matcher.group(1)));
                        map.put("Mongo中的表名", mongoColumnRelationsMap.get(matcher.group(1)));
                    }
                }
                for (String key : regexMap.keySet()) {
                    regex = key;
                    pattern = Pattern.compile(regex);
                    matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        map.put(regexMap.get(key), matcher.group(1));
                    }
                }
            }
            LOG.info("the message handling is finished");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return map;
    }
}
