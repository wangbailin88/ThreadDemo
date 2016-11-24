package com.ushine.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ushine.init.ISystemService;
import com.ushine.utils.ObjectFactory;
import com.ushine.utils.PathUtils;
import com.ushine.utils.PropertiesUtils;

public class ApplicationListener implements ServletContextListener {
	/**
	 * 配置文件中读取的类名的集合
	 */
	private static Map<String, String> inits = new HashMap<String, String>();
	/**
	 * 系统初始化服务集合
	 */
	private List<ISystemService> services = new ArrayList<>();
	/**
	 * 加载初始服务配置文件,加载需要初始化的类名
	 */
	static{
		String path = PathUtils.getCurrentThreadClassPath()+"config/init.properties";
		PropertiesUtils prep = new PropertiesUtils();
		prep.load(path);
		inits = prep.getMap();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("停止系统...");
		if(services!=null){
			for(ISystemService service : services){
				System.out.println("停止系统"+service.serviceName()+"...");
				service.destory();
				System.out.println(service.serviceName()+"启动完成...");
			}
		}
		System.out.println("系统停止完成...");
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("启动系统初始化服务...");
		/**
		 * 加载系统初始化服务
		 */
		load();
		if(services!=null){
			for(ISystemService service : services){
				System.out.println("-----------初始化"+service.serviceName()+"-----------");
				service = service.init();
				service.execute();
				System.out.println("-----------"+service.serviceName()+"启动完成-----------");
			}
		}
	}
	
	/**
	 * 加载系统初始化服务
	 */
	public void load(){
		System.out.println("加载系统初始化服务...");
		Set<String> keys = inits.keySet();
		for (String key : keys) {
			String className = inits.get(key);
			ISystemService service = (ISystemService) ObjectFactory.getInstance(className);
			services.add(service);
		}
	}

}
