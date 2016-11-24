package com.ushine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/**
 * CSV文件类
 * 用户对CSV文件的操作
 * 主要是完成数据单行顺序读写
 * @author Lee
 *
 */
public class CSV {
	
	private File file;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	public CSV(String path, boolean isWrite) {
		this.file = new File(path);
		this.init(isWrite);
	}
	
	public CSV(File file, boolean isWrite) {
 		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 		this.file = file;
		this.init(isWrite);
	}
	
	public CSV(String path, boolean createFile, boolean isWrite) {
		this.file = new File(path);
		if(!file.exists()){
			if(createFile != false){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.init(isWrite);
	}
	
	public CSV(File file, boolean createFile, boolean isWrite) {
		if(!file.exists()){
			if(createFile != false){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.file = file;
		this.init(isWrite);
	}
	
	/**
	 * 从当前位置读取下一行
	 * @return
	 */
	public String[] readLine(){
		String[] rowData = null;
		try {
			String row = reader.readLine();
			if(row == null){
				return null;
			}
			String[] list = row.split(",");	
			if(list == null || list.length ==0 ){
				return null;
			}else{
				rowData = new String[list.length];
				for(int i=0; i<list.length; i++){
					rowData[i] = list[i];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rowData;
	}
	
	/**
	 * 在文本末新增写入一行
	 * @param data
	 */
	public void writeLine(String[] data){
		if(data != null && data.length != 0){
			StringBuffer str = new StringBuffer("");
			for(int i=0; i<data.length; i++){
				String temp = data[i];
				if(temp.indexOf(",")!=-1 || temp.contains("\"")){
					temp="\""+temp.replace("\"", "\"\"")+"\"";
				}
				if(i != (data.length-1)){
					str.append(temp).append(",");
				}else{
					str.append(temp).append("\r\n");
				}
			}
			try {
				writer.write(str.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 初始化配置
	 */
	public void init(boolean isWrite){
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			if(isWrite){
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭操作文件的系统资源
	 */
	public void close(){
		if(reader!=null){
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(writer!=null){
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	
}
