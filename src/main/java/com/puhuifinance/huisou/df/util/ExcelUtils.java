package com.puhuifinance.huisou.df.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by puhui on 2016/8/8.
 */
public class ExcelUtils {

    //读取excel
    public static List readExcel(String inputFilePath,String sheetName){
        //创建一个list 用来存储读取的内容
        List list = new ArrayList();
        Cell cell =null;

        try {
            //创建输入流
            InputStream in = new FileInputStream(inputFilePath);

            //获取Excel文件对象
            Workbook rwb = Workbook.getWorkbook(in);

            //获取指定工作表及行数
            Sheet sheet = rwb.getSheet(sheetName);
            int rowCount = sheet.getRows();

            //去除表头，读取每一行的内容并存入list中
            for(int i=1;i<rowCount;i++){
                int columnCount = sheet.getColumns();
                String[] strs = new String[columnCount];
                for(int j=0;j<columnCount;j++){
                    cell = sheet.getCell(j,i);
                    strs[j] = cell.getContents();
                }
                list.add(strs);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    //写入excel

    public static void writeExcel(String outputFilePath,List list){

        try {
            //设置title的字体,背景色及边框
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL,14,WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
            WritableCellFormat titleCellFormat = new WritableCellFormat(titleFont);
            titleCellFormat.setBackground(Colour.GREEN);
            titleCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            //设置正文的字体，背景色及边框
            WritableFont textFont = new WritableFont(WritableFont.COURIER,14,WritableFont.NO_BOLD,false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
            WritableCellFormat textCellFormat = new WritableCellFormat(textFont);
            textCellFormat.setBackground(Colour.AUTOMATIC);
            textCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            //获取title内容
            String confPath = ExcelUtils.class.getClassLoader().getResource("./conf/excel_title.json").getPath();
            JSONObject titles = FileUtils.getJSONObjectFromJsonFile(confPath);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,String> titleMap = objectMapper.readValue(JSON.toJSONString(titles),Map.class);

            //若路径不存在，则创建文件夹
            File outputFile = new File(outputFilePath);
            String path = outputFile.getParent();
            if(!(new File(path).exists())){
                new File(path).mkdirs();
            }

            Workbook rwb = Workbook.getWorkbook(outputFile);
            File tempfile = new File(System.getProperty("user.dir") +
                    "\\"+path+"\\tempfile.xls");

            WritableWorkbook wwb = null;
            //如果文件不存在，则创建文件并写入相关数据，否则就在原有数据中追加
            if(!outputFile.exists()){
                wwb= Workbook.createWorkbook(outputFile);

            }else{
                wwb = Workbook.createWorkbook(tempfile,rwb);
            }


            //获取当前工作表量，并在最后一张表后，新建一张表存放数据
            int sheetNumber = rwb.getNumberOfSheets();
            System.out.println(sheetNumber);
            WritableSheet sheet = wwb.createSheet("第"+(sheetNumber+1)+"次报警",sheetNumber);

            //写入title
            for (String key : titleMap.keySet()) {
                Label label = new Label(Integer.parseInt(titleMap.get(key)),0,key,titleCellFormat);
                sheet.addCell(label);
            }


            for(int i=1;i<=list.size();i++){
                Map<String,String> map = (Map<String,String>)list.get(i-1);
                for (String key : map.keySet()) {
                    Label label = new Label(Integer.parseInt(titleMap.get(key)),i,map.get(key),textCellFormat);
                    sheet.addCell(label);
                }
            }
            wwb.write();
            wwb.close();
            rwb.close();
            outputFile.delete();
            tempfile.renameTo(outputFile);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }


    }



}
