package com.ushine.utils;

import com.ushine.exception.AppException;

/**
 * 
 * @author lee
 *
 */
public class ObjectFactory {
	
	/**
	 * 反射生成实例
	 * @param className
	 * @return
	 */
	public static Object getInstance(String className){
		try {
			Class c = Class.forName(className);
			return c.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new AppException(-1, className+" 不存在");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new AppException(-1, className+" 创建实例异常");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new AppException(-1, className+" 创建实例异常");
		}
	}
}
