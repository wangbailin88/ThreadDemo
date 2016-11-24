package com.ushine.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常代码
 * @author Lee
 *
 */
public class ErrorCode {
	private ErrorCode(){}
	
	//自定义异常代码
	public final static int ERROR_SQL = 1000;
	public final static int ERROR_DATA_TYPE = 2000;
	public final static int ERROR_FILE_NOT_FOUND = 3000;
	public final static int ERROR_IO = 4000;
	public final static int ERROR_CLASS_NOT_FOUND = 5000;
	
	public final static int ERROR_RUNTIME_NULL = 6001;
	
	public final static int ERROR_OTHER = 9999;
	
//	public final static int ERROR_
	
	private static Map<Integer, String> map = new HashMap<Integer, String>();
	
	static{
		//注入异常代码对应的文字信息
//		map.put(paramK, paramV)
	}
	
	/**
	 * 获取异常文字信息
	 * @param code
	 * @return
	 */
	public static String getErrorInfo(int code){
		return map.get(new Integer(code));
	}
}
