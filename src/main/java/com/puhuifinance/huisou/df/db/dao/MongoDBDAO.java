package com.puhuifinance.huisou.df.db.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.puhuifinance.huisou.df.util.FileUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by DuMing on 2016/4/14.
 */
public class MongoDBDAO implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBDAO.class);
    private transient MongoClient mongoClient;


    public MongoDBDAO() {
        String mongoConfPath = MongoDBDAO.class.getClassLoader().getResource("./conf/mongo_dao_conf.json").getPath();

        try {
            //连接到MongoDB服务
            JSONObject mongoConfJson = FileUtils.getJSONObjectFromJsonFile(mongoConfPath);
            JSONArray ipJsonArray = mongoConfJson.getJSONArray("ip");
            Map<String,String> credentialMap = new HashMap<String, String>();
            credentialMap = (Map<String, String>) mongoConfJson.get("credentials");
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            for(int i = 0 ;i<ipJsonArray.size();i++){
                ServerAddress serverAddress = new ServerAddress(ipJsonArray.getString(i),Integer.parseInt(mongoConfJson.getString("port")));
                addrs.add(serverAddress);
            }

            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码

            MongoCredential credential = MongoCredential.createScramSha1Credential(credentialMap.get("userName"), credentialMap.get("db"), credentialMap.get("password").toCharArray());
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);

            //通过连接认证获取MongoDB连接
            mongoClient = new MongoClient(addrs,credentials);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> query(String database, String table, Map<String, String> queryParams) {
        Document query = new Document();

        for (String queryParam : queryParams.keySet()) {
            // 输入参数的实际值, 例如 Input.abcId 字段的实际值 123
            if(!"df_source".equals(queryParam)){
                int paramValue = Integer.valueOf(queryParams.get(queryParam).trim());
                query.append(queryParam, paramValue);
            }else{
                String paramValue = queryParams.get(queryParam);
                query.append(queryParam, paramValue);
            }
        }

        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(table);
        Document document = collection.find(query).first();
        Map<String, String> result = new HashMap<String, String>();

        if (document != null) {
            //获取数据更新时间 updateTime
            Object updateTimeObject = null;
            if(null != document.get("updateTime")){
                updateTimeObject = document.get("updateTime");
            }else if(null != document.get("insertTime")){
                updateTimeObject = document.get("insertTime");
            }else if(null != document.get("startTime")){
                updateTimeObject = document.get("startTime");
            }

            result.put("updateTime",String.valueOf(updateTimeObject));

            try {
                //获取数据处理时间 transfer_date,将transfer_date转换成日期并减去8小时，最终转为String存入map中
                Object TransferDateObject = document.get("transfer_date");
                SimpleDateFormat formate = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                SimpleDateFormat formate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date = formate.parse(String.valueOf(TransferDateObject));
                Date transer_date = formate.parse(String.valueOf(date));
                String transferDateString = formate1.format(transer_date);
                result.put("transfer_date", transferDateString);

                //数据处理时间减去数据更新时间，得到最终延迟时间
                Date updateTime = formate1.parse(String.valueOf(updateTimeObject));
                long s = (transer_date.getTime() - updateTime.getTime())/1000;
                result.put("mongo入库延迟时间",String.valueOf(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    public void close() throws Exception {
        mongoClient.close();
    }

}
