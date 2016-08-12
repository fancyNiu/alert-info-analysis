package com.puhuifinance.huisou.df.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by puhui on 2016/8/8.
 */
public class ExcelUtilsTest {

    @Test
    public void readExcelTest(){
        String filePath = "E:\\工作\\反欺诈系统\\2.0\\自动化测试数据\\AntifraudQuestion.xls";
        String sheetName = "hasInstored";
        List list = ExcelUtils.readExcel(filePath,sheetName);

        for(int i=0;i<list.size();i++){
            System.out.println(i);
            String[]  strs = (String[])list.get(i);
            for(int j=0;j<strs.length;j++){
                System.out.print(strs[j] + "\t");
            }
        }
    }

    @Test
    public void writeExcelTest(){
        String filePath = "alert_message/20160810.xls";
        List list = new ArrayList();
        Map<String,String> map = new HashMap<String,String>();
        map.put("报警时间","1");
        map.put("报警信息","你猜");
        list.add(map);
        ExcelUtils.writeExcel(filePath,list);
    }

}
