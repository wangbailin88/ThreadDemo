package com.ushine.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ushine.exception.AppException;
import com.ushine.exception.ErrorCode;

public class AnalysisXmlUtils {
	private static String[] headers;
	static{
		String path = PathUtils.getCurrentThreadClassPath()+"/config/config.properties";
		PropertiesUtils util = new PropertiesUtils();
		util.load(path);
		String tempStr = util.getValue("header");
		headers = tempStr.split(",");
	}
	/**
	 * 将XML文件转换为标准的csv文件
	 * @param sourceFile xml源文件
	 * @param tempFile csv目标临时文件
	 */
	public void analysis(File sourceFile,File tempFile,File errorFile){
		if(sourceFile == null || tempFile == null){
			throw new AppException(ErrorCode.ERROR_FILE_NOT_FOUND, "指定文件不存在");
		}
		BufferedWriter writer = null;
		FileInputStream in = null;
		try {
			 in = new FileInputStream(sourceFile);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile, true),"UTF-8"));
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			/**
			 * 获取根元素下的所有子节点元素
			 */
			Iterator datas = root.elementIterator();
			/**
			 * 迭代所有子节点元素
			 */
			while(datas.hasNext()){
				Element dataLine = (Element) datas.next();
				/**
				 * 获取每一个子元素的所有下级子元素
				 */
				List fields = dataLine.elements();
				/**
				 * 迭代所有下级子元素，此处已经可以定位到每一个字段
				 * 根据数据文件配置的表头信息，从XML文件的每一行解析出每一个字段的值，根据表头信息拼成字符串，如果没有表头字段对应的字段值，则拼"0"，拼串过程中，对字段值包含特殊字符逗号，
				 */
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < headers.length; i++) {
					String value = null;
					for(int j=0 ;j<fields.size(); j++){
						Element field = (Element) fields.get(j);
						String fieldName = field.getName();
						String fieldValue = field.getTextTrim();
						/**
						 * 对包含英文逗号和英文引号的字段值，将其替换为对应的中文符号
						 */
						if(field.getTextTrim().contains("\"") || field.getTextTrim().contains("“") || field.getTextTrim().contains("”") || field.getTextTrim().contains(",")){
							fieldValue = field.getTextTrim().replace("\"", "").replace("“", "").replace("”", "").replace(",", "，");
						}
						/**
						 * 如果字段值为空，则替换为0
						 */
						if(field.getTextTrim().equals("")){
							fieldValue = "0";
						}
						if(fieldName.equals(headers[i])){
							value = fieldValue;
							break;
						}
					}
					if(value != null){
						buffer.append(value).append(",");
					}else{
						buffer.append("0").append(",");
					}
				}
				String dataRow = buffer.substring(0, buffer.length()-1).toString();
				writer.write(dataRow);
				writer.write("\r\n");
				//System.out.println(dataRow);
			}
		} catch (FileNotFoundException e) {
			System.out.println("空文件名："+sourceFile.getName());
			e.printStackTrace();
		} catch (DocumentException e) {
			FileUtil.copy(sourceFile, errorFile);
			System.out.println("异常文件名："+sourceFile.getName());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			FileUtil.copy(sourceFile, errorFile);
			System.out.println("异常文件名："+sourceFile.getName());
			e.printStackTrace();
		} catch (IOException e) {
			FileUtil.copy(sourceFile, errorFile);
			System.out.println("异常文件名："+sourceFile.getName());
			e.printStackTrace();
		}finally {
			if(writer != null){
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		File sourceFile = new File("F:/YtoWork/backup/ZTO_20150706093250.xml");
		File destFile = new File("F:/YtoWork/backup/ZTO_20150706093250.temp");
		//analysis(sourceFile, destFile);
	}
}
