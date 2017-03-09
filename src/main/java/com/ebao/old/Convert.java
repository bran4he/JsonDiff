package com.ebao.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Convert {
	private static int itemNum = 0;
	
	/**
	 * 
	 * @param jsonobj
	 * @param currentIdx 
	 * @param sheet
	 * @param parentKey
	 */
	private static void loop(JsonObject jsonobj, Sheet sheet, String parentKey, int currentIdx){
		
		Map<String, JsonObject> objMap =  new HashMap<String, JsonObject>();
		
		for(Map.Entry<String,JsonElement> entry: jsonobj.entrySet()){
			
			String key = entry.getKey();
			JsonElement value = entry.getValue();					
			
			if(value.isJsonObject()){
				//直接进行递归, 增加一列
				//itemNum++;
				objMap.put(parentKey+"."+key, (JsonObject)value);
			}else if(value.isJsonArray()){
				//默认选择第一个进行递归， 增加一列
				//itemNum++;
				objMap.put(parentKey+"."+key, value.getAsJsonArray().get(0).getAsJsonObject());
			}else{
				//直接写入excel当前列，不改变列
				if(currentIdx == 0){
					Row row = getOrCreateRow(sheet, currentIdx);
					getOrCreateCell(row, itemNum).setCellValue(parentKey);
					currentIdx++;
				}
				Row row = getOrCreateRow(sheet, currentIdx);
				getOrCreateCell(row, itemNum).setCellValue(key);
				
				System.out.println(parentKey +","+ itemNum +","+ currentIdx+","+ key);
				
				currentIdx++;
			}
		}
		
		//handle other list
		for(Entry<String, JsonObject> entry : objMap.entrySet()){
			itemNum++;
			System.out.println("hanling the tag :" + entry.getKey());
			loop(entry.getValue(), sheet, entry.getKey(), 0 );
		}
	}
	
	private static Row getOrCreateRow(Sheet sheet, int idx){
		return sheet.getRow(idx)!= null?sheet.getRow(idx):sheet.createRow(idx);
	}
	
	private static Cell getOrCreateCell(Row row, int idx){
		return row.getCell(idx)!= null?row.getCell(idx):row.createCell(idx);
	}
	
	public static void generateRootEleType() throws Exception{
		String path = System.getProperty("user.dir");

		BufferedReader br = new BufferedReader(new FileReader(path + File.separator + "source.json"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		System.out.println(sb.toString());
		
		JsonObject jsonobj = new JsonParser().parse(sb.toString()).getAsJsonObject();
		System.out.println(jsonobj.get("GrossPremiumDifference"));
		
		
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("covert sheet");
		
		//root ele
		Row row = sheet.createRow((short)0);
		Cell c00 = row.createCell(0);
		c00.setCellValue("Policy");
		
		//row 1 and 2 for source and target tag
		row = sheet.createRow((short)1);
		Cell c10 = row.createCell(0);
		c10.setCellValue("key");
		
		Cell c11 = row.createCell(1);
		c11.setCellValue("isJsonArray");
		
		c11 = row.createCell(2);
		c11.setCellValue("isJsonObject");
		
		c11 = row.createCell(3);
		c11.setCellValue("isJsonNull");
		
		c11 = row.createCell(4);
		c11.setCellValue("isJsonPrimitive");
		
		//key start from index 2
		int sourceIndex = 2;
		
		System.out.println("key \t isJsonArray \t isJsonObject \t isJsonNull \t isJsonPrimitive");
		
		Row sourceRow = null;
		for(Map.Entry<String,JsonElement> entry: jsonobj.entrySet()){
			sourceRow = sheet.createRow((short)sourceIndex);
			
			int j =0;
			Cell sourceCell = sourceRow.createCell((short)j);
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			sourceCell.setCellValue(key);
			
			j++;
			sourceCell = sourceRow.createCell((short)j);
			sourceCell.setCellValue(value.isJsonArray());
			j++;
			sourceCell = sourceRow.createCell((short)j);
			sourceCell.setCellValue(value.isJsonObject());
			j++;
			sourceCell = sourceRow.createCell((short)j);
			sourceCell.setCellValue(value.isJsonNull());
			j++;
			sourceCell = sourceRow.createCell((short)j);
			sourceCell.setCellValue(value.isJsonPrimitive());
			
			System.out.println(key +"\t"+ value.isJsonArray()+"\t"+ value.isJsonObject()+"\t"+ value.isJsonNull()+"\t"+ value.isJsonPrimitive());
			sourceIndex ++;
		}
		
		File convertFile = new File(path + File.separator + System.currentTimeMillis() + ".xlsx");
		
    	FileOutputStream fileOut = null;
    	try {
    		fileOut = new FileOutputStream(convertFile);
    		wb.write(fileOut);
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally{
    		try {
    			fileOut.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	
	}
}
