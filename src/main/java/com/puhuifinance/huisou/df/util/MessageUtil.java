package com.puhuifinance.huisou.df.util;

import com.alibaba.fastjson.JSONObject;
import com.puhuifinance.huisou.df.db.dao.MongoDBDAO;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对数据进行解析，并调取mongo数据，返回最终消息解析后的结果
 */
public class MessageUtil {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

    public static List<Map<String,String>> parseMessage(List<String> list) {
        //创建最终返回的list
        List<Map<String,String>> messageInfoList = new ArrayList<Map<String, String>>();
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
                Map<String, String> map = new HashMap<String, String>();
                message = list.get(i);
                LOG.debug("the message is :"+message);
                map.put("报警信息", message);
                map.put("报警字段1","applyNo");
                //如果包含neo4j，证明是neo4j入库慢，则调用neo4j的配置文件进行分析，否则是mongo入库慢
                if (message.contains("neo4j")) {
                    LOG.debug("neo4j引起的报警");
                    map.put("引起报警服务", "neo4j");
                    regexMap = (Map<String, String>) regexJson.get("neo4j");
                    regex = "com.puhui.df.model.mysql.(.*?)执行时间";
                } else {
                    LOG.debug("mongo引起的报警");
                    map.put("引起报警服务", "mongo");
                    map.put("报警字段1","id");
                    map.put("报警字段2","updateTime");
                    regexMap = (Map<String, String>) regexJson.get("mongo");
                    if(message.startsWith("com.puhui.df.model.mysql")){
                        regex = "com.puhui.df.model.mysql.(.*?)--";
                    }else if(message.startsWith("com.puhui.df.query.mongo")){
                        regex = "com.puhui.df.query.mongo.Query(.*?)执行时间";
                    }else {
                        regex = "\\d{2}:\\d{2}:\\d{2}(.*?) data";
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
                        if ("报警字段1的值".equals(regexMap.get(key)) && matcher.group(1).trim().startsWith("=")) {
                            String value = matcher.group(1).substring(2);
                            map.put(regexMap.get(key), value.trim());
                        } else {
                            map.put(regexMap.get(key), matcher.group(1).trim());

                        }
                    }
                }
                LOG.debug("调取mongo数据进行整合");
                //根据解析结果调取mongo数据并进行整合
                Map<String,String> mongoResult = getMongoData(map);

                if(null != mongoResult ){
                    for(String key : mongoResult.keySet()){
                        map.put(key,mongoResult.get(key));
                    }
                    if(map.containsKey("引起报警服务") && "mongo".equals(map.get("引起报警服务"))){
                        map.put("最终延迟可能的原因","mongo");
                    }else if(map.containsKey("引起报警服务") && "neo4j".equals(map.get("引起报警服务")) && Long.parseLong(mongoResult.get("mongo入库延迟时间"))>80){
                        map.put("最终延迟可能的原因","mongo");
                    }else if(map.containsKey("引起报警服务") && "neo4j".equals(map.get("引起报警服务"))){
                        map.put("最终延迟可能的原因","neo4j");
                    }
                }
                messageInfoList.add(map);
                LOG.debug("mongo数据整合完毕");
            }
            LOG.info("the message handling is finished");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return messageInfoList;
    }

    public static Map<String,String> getMongoData(Map<String,String> map){

        //定义传入MongoDao中的map
        Map<String,String> queryParams = new HashMap<String, String>();
        Map<String,String> result = new HashMap<String, String>();

        //获取id转换的配置文件
        String queryParamsConfPath = MessageUtil.class.getClassLoader().getResource("./conf/query_params.json").getPath();
        JSONObject queryParamsConfJson = null;
        try {
            queryParamsConfJson = FileUtils.getJSONObjectFromJsonFile(queryParamsConfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //通过解析出来的信息调取mongo中的数据进行计算
        //1.确定查询要使用的map
        if("neo4j".equals(map.get("引起报警服务")) && map.containsKey("报警字段1的值")){
            queryParams.put("applyNo",map.get("报警字段1的值"));
        }else if("mongo".equals(map.get("引起报警服务"))){
            if(map.get("mysql中的表名").startsWith("puhui")){
                queryParams.put("df_source","puhui");
            }else if(map.get("mysql中的表名").startsWith("jiea")){
                queryParams.put("df_source","jiea");
            }

            if(queryParamsConfJson.containsKey(map.get("mysql中的表名")) ){
                queryParams.put(queryParamsConfJson.getString(map.get("mysql中的表名")),map.get("报警字段1的值"));
            }else {
                queryParams.put("id",map.get("报警字段1的值"));
            }
        }

        //2.确定要查询的库和表
        String database = "df";
        if(map.containsKey("Mongo中的表名")){
            MongoDBDAO dao = new MongoDBDAO();
            result = dao.query(database,map.get("Mongo中的表名"),queryParams);
        }

        return result;
    }
}
