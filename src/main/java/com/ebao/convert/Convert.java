package com.ebao.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ebao.pojo.KeyResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Convert {
	
	//root node numbers
	private static int itemNum = 0;
	
	/**
	 * read string from file and parse to JsonObject(Gson)
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static JsonObject getJson(String fileName) throws Exception{
		String path = System.getProperty("user.dir");
		
		BufferedReader br = new BufferedReader(new FileReader(path + File.separator + fileName));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		System.out.println("============get json from :" + fileName);
		System.out.println(sb.toString());
		
		JsonObject jsonobj = new JsonParser().parse(sb.toString()).getAsJsonObject();
		return jsonobj;
	}
	
	/**
	 * get map structure data from jsonObject(Gson)
	 * @param jsonObj
	 * @return
	 */
	private static Map<String, List<String>> getMapData(JsonObject jsonObj){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		analysisJson(jsonObj, map, "Policy");
		Gson gson = new Gson();
		System.out.println("json1:==================");
		System.out.println(gson.toJson(map));
		
		Map<String, List<String>> mapDc = new HashMap<String, List<String>>();
		return map;
	}
	
	/**
	 * two params are two file name in the same foler of jar file
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("execute start");
		
		System.out.println("current path: " + System.getProperty("user.dir"));
		
		Map<String, List<String>> mapCore = getMapData(getJson(args[0]));
		Map<String, List<String>> mapDc = getMapData(getJson(args[1]));

		Set<String> keyCore = mapCore.keySet();
		Set<String> keyDc = mapDc.keySet();
		
		List<String> keyLst = new ArrayList<String>();
		keyLst.addAll(keyCore);
		keyLst.addAll(keyDc);
		
		//<"Policy", <"POI", {"POI", Y, Y}>>
		Map<String, Map<String, KeyResult>> result = new HashMap<String, Map<String, KeyResult>>();
		
		for(Entry<String, List<String>> entry : mapCore.entrySet()){
			Map<String, KeyResult> rsMap =  new HashMap<String, KeyResult>();
			
			String parentName = entry.getKey();
			List<String> childNames = entry.getValue();
			for(String name : childNames){
				KeyResult ks = new KeyResult(name,true, false);
				rsMap.put(name, ks);
			}
			result.put(parentName, rsMap);
		}
		
		for(Entry<String, List<String>> entry : mapDc.entrySet()){
			
			String parentName = entry.getKey();
			List<String> childNames = entry.getValue();
			
			if(result.containsKey(parentName)){
				
				Map<String, KeyResult> rsMapOld = result.get(parentName);
				for(String name : childNames){
					if(rsMapOld.containsKey(name)){
						rsMapOld.get(name).setDc(true);
					}
				}
				
			}else{
				Map<String, KeyResult> rsMapNew =  new HashMap<String, KeyResult>();
				for(String name : childNames){
					KeyResult ks = new KeyResult(name,false, true);
					rsMapNew.put(name, ks);
				}
				result.put(parentName, rsMapNew);
			}
		}
		
//		System.out.println("==========result==========");
//		printResult(result);
//		System.out.println("==========end==========");
		
		export(result, args[0], args[1]);
		
		System.out.println("execute end");
	}
	
	/**
	 * export to excel with result Map data
	 * @param result
	 * @param name1
	 * @param name2
	 */
	private static void export(Map<String, Map<String, KeyResult>> result, String name1, String name2){
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("json compare sheet");
		
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("Node Name");
		row.createCell(1).setCellValue("Key Name");
		row.createCell(2).setCellValue(name1);
		row.createCell(3).setCellValue(name2);
		
		int rowIdx = 1;
		//export data
		for(Entry<String, Map<String, KeyResult>> entry : result.entrySet()){
			String nodeName = entry.getKey();
			System.out.println("root name : " + nodeName);
			
			Map<String, KeyResult> rsMap = entry.getValue();
			for(KeyResult kr: rsMap.values()){
				Row rowForKey = sheet.createRow(rowIdx);
				rowIdx++;
				rowForKey.createCell(0).setCellValue(nodeName);;
				rowForKey.createCell(1).setCellValue(kr.getKey());;
				rowForKey.createCell(2).setCellValue(kr.isCore());
				rowForKey.createCell(3).setCellValue(kr.isDc());
				System.out.println( kr.getKey() +",\t\t " + kr.isCore() + ",\t " + kr.isDc());
			}
		}
		
		
		String path = System.getProperty("user.dir");
		File convertFile = new File(path + File.separator + name1 +"_vs_"+ name2 +"_" +System.currentTimeMillis() + ".xlsx");
		
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
	
	
	/**
	 * print the resut data
	 * @param result
	 */
	private static void printResult(Map<String, Map<String, KeyResult>> result){
		for(Entry<String, Map<String, KeyResult>> entry : result.entrySet()){
			System.out.println("root name : " + entry.getKey());
			Map<String, KeyResult> rsMap = entry.getValue();
			for(KeyResult kr: rsMap.values()){
				System.out.println( kr.getKey() +",\t\t " + kr.isCore() + ",\t " + kr.isDc());
			}
		}
	}
	
	/**
	 * recursive function to analysis JsonObject to update Map data
	 * @param jsonobj
	 * @param mapCore
	 * @param parentKey
	 */
	private static void analysisJson(JsonObject jsonobj, Map<String, List<String>> map, String parentKey){
		Map<String, JsonObject> objMap =  new HashMap<String, JsonObject>();
		for(Map.Entry<String,JsonElement> entry: jsonobj.entrySet()){
			
			String key = entry.getKey();
			JsonElement value = entry.getValue();					
			
			if(value.isJsonObject()){
				//add to map for next cycle
				objMap.put(parentKey+"."+key, (JsonObject)value);
			}else if(value.isJsonArray()){
				//get the first one, add to map for next cycle
				objMap.put(parentKey+"."+key, value.getAsJsonArray().get(0).getAsJsonObject());
			}else{
				//directly handle
				System.out.println(parentKey +","+itemNum +","+key);
				handleMap(map, parentKey, key);
			}
		}
		
		//handle other list
		for(Entry<String, JsonObject> entry : objMap.entrySet()){
			itemNum++;
			System.out.println("hanling the tag :" + entry.getKey());
			analysisJson(entry.getValue(), map, entry.getKey());
		}
	}
	
	/**
	 * add value to list, to map
	 * @param mapCore
	 * @param key
	 * @param lstValue
	 */
	private static void handleMap(Map<String, List<String>> map, String key, String lstValue){
		if(map.containsKey(key)){
			List lst = map.get(key);
			lst.add(lstValue);
		}else{
			List lst = new ArrayList<String>();
			lst.add(lstValue);
			map.put(key, lst);
		}
	}
}
