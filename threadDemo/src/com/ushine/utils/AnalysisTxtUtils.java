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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ushine.exception.AppException;
import com.ushine.exception.ErrorCode;
/**
 * 转换宅急送txt文件
 * @author 王百林
 *2015-9-15下午2:06:17
 *
 */
public class AnalysisTxtUtils {
	public static void analysis(File srcFile,File destFile,File errorFile){
		FileInputStream sou = null;
		BufferedWriter writer = null;
		BufferedReader buf = null;
		try {
			sou = new FileInputStream(srcFile);
			buf = new BufferedReader(new InputStreamReader(sou,"GBK"));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile, true),"UTF-8"));
			String str = null;
			while((str = buf.readLine()) != null){
				writer.write(str+"\n");
			}
		} catch (Exception e) {
			FileUtil.copy(srcFile, errorFile);
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if(sou!=null){
				try {
					sou.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if(buf!=null){
				try {
					buf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}


