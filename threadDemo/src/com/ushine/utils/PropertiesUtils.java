package com.ushine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.ushine.exception.AppException;
import com.ushine.exception.ErrorCode;


/**
 * Properties文件工具：将Properties文件的内容加载到Map中，通过键获取到值
 * @author Franklin
 * @version 1.0 2011-11-4 -> 2011-11-4
 *
 */
public class PropertiesUtils {
	
	private Map<String, String> propsMap;
	
	private Properties props;
	
	private InputStream in;
	
	public PropertiesUtils() {
		propsMap = new TreeMap<String, String>();
		props = new Properties();
	}
	
	public void load(String filePath) {
		try {
			in = new FileInputStream(new File(filePath));
			props.load(in);
			Enumeration enums = props.propertyNames();
			while(enums.hasMoreElements()){
				String key = (String)enums.nextElement();
				String value = props.getProperty(key);
				propsMap.put(key, value);
			}
		} catch(FileNotFoundException e) {
			throw new AppException(ErrorCode.ERROR_FILE_NOT_FOUND, filePath);
		}
		catch(IOException e) {
			throw new AppException(ErrorCode.ERROR_IO,"读取配置文件异常："+e.getMessage());
		}
	}
	
	public Map<String, String> getMap() {
		return propsMap;
	}
	
	public String getValue(String key) {
		if(key==null || "".equals(key)) {
			throw new AppException(ErrorCode.ERROR_RUNTIME_NULL, "参数Key为空!");
		}
		return propsMap.get(key);
	}
	
	public void setMap(Map<String, String> map){
		this.propsMap = map;
	}
	
	public boolean write(String path){
		File file = this.prepareFile(path);
		props = new Properties();
		try {
			OutputStream os = new FileOutputStream(file);
			if(propsMap!=null){
				Set<String> keys = propsMap.keySet();
				for(String key : keys){
					props.setProperty(key, propsMap.get(key));
				}
			}
			props.store(os, "");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.ERROR_FILE_NOT_FOUND, "path");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.ERROR_IO, "读取配置文件异常："+e.getMessage());
		}
	}
	
	private File prepareFile(String path) {
		if(path==null||path.trim().length()==0){
			throw new AppException(ErrorCode.ERROR_FILE_NOT_FOUND, "path");
		}
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new AppException(ErrorCode.ERROR_IO, "创建文件异常："+e.getMessage());
			}
		}
		return file;
	}
}
