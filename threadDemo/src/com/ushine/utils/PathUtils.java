package com.ushine.utils;





/**
 * 文件操作类：
 * @author Franklin
 * @version 1.0 2011-11-4 -> 2011-11-4
 *
 */
public class PathUtils {
	
	/**
	 * 获取WEB-INF/Classes路径
	 * @return String
	 */
	public static String getClassesPath() {
		String path = PathUtils.class.getClassLoader().getResource("/").toString().replaceAll("%20", " ");
		return path.substring(6);
	}
	
	public static String getCurrentThreadClassPath(){
		return Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceAll("%20", " ");
	}
}
