package com.ushine.init;
/**
 * 系统基础服务类接口
 * @author LiYan
 *
 */
public interface ISystemService {
	/**
	 * 获取服务名称
	 * @return 服务名称
	 */
	public String serviceName();
	/**
	 * 初始化服务，加载一些静态变量之类
	 * @return
	 */
	public ISystemService init();
	/**
	 * 执行服务
	 */
	public void execute();
	/**
	 * 销毁服务
	 */
	public void destory();
}
