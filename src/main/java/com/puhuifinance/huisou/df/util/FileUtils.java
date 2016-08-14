package com.puhuifinance.huisou.df.util;





import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.Properties;

/**
 * Created by jingyang on 16-6-20.
 */
public class FileUtils {

    /**
     *
     * @param filePath
     * @return
     * @throws java.io.IOException
     */

    public static JSONObject getJSONObjectFromJsonFile(String filePath) throws IOException {
        BufferedReader reader  = new BufferedReader(new FileReader(new File(filePath)));
        StringBuffer jsonData = new StringBuffer();
        String line = null;
        while((line=reader.readLine())!=null){
            jsonData = jsonData.append(line);
        }

        JSONObject jsonFileData = JSONObject.parseObject(String.valueOf(jsonData));
        return jsonFileData;
    }
//    public static Properties getPropertiesFile(String filePath) throws IOException {
//        Properties properties = new Properties();
//        properties.load(new FileReader(filePath));
//        return properties;
//    }


}
