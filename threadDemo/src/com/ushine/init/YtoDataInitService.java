/**
 * 下午3:45:14
 */
package com.ushine.init;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ushine.utils.AnalysisXmlUtils;
import com.ushine.utils.FileUtil;
import com.ushine.utils.PathUtils;
import com.ushine.utils.PropertiesUtils;

public class YtoDataInitService extends Thread implements ISystemService{
	/**
	 * 源文件路径
	 */
	private static String ytoSourcePath = "";
	/**
	 * 文件备份路径
	 */
	private static String ytoBackupPath  = "";
	/**
	 * 目标文件路径
	 */
	private static String ytoDestPath  = "";
	private static String ytoErrorPath  = "";
	private static int ytoInterval = 1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 读取路径配置文件
	 */
	static{
		String path = PathUtils.getCurrentThreadClassPath()+"config/system.properties";
		PropertiesUtils util = new PropertiesUtils();
		util.load(path);
		ytoSourcePath = util.getValue("yto_source_path");
		ytoBackupPath = util.getValue("yto_backup_path");
		ytoDestPath = util.getValue("yto_dest_path");
		ytoErrorPath = util.getValue("yto_error_path");
		ytoInterval = Integer.parseInt(util.getValue("yto_interval"));
	}
	/**
	 * 当前线程
	 */
	private Thread thread;
	/**
	 * 是否停止当前线程
	 */
	private volatile boolean stop = true;
	/**
	 * 线程睡眠时间，单位：秒，从配置文件读取
	 */
	@Override
	public String serviceName() {
		return "圆通数据转换服务";
	}

	@Override
	public ISystemService init() {
		return this;
	}

	@Override
	public void execute() {
		if (thread == null) {
			thread = new Thread(this);
			/**
			 * 强制将当前进程设为守护线程
			 * @date 2015-06-09 14:53:00 LiYan
			 */
			thread.setDaemon(true);
			thread.start();
		}
	}

	@Override
	public void destory() {
		if (thread != null) {
			System.out.println("正在停止圆通数据转换线程...");
			thread.interrupt();
			stop = false;
			thread = null;
			System.out.println("圆通数据转换线程停止完成...");
		}
	}
	
	@Override
	public void run() {
		AnalysisXmlUtils util = new AnalysisXmlUtils();
		while (!Thread.interrupted() && stop) {
			try {
				List<File> files = new ArrayList<>();
				FileUtil.fileScanning(ytoSourcePath, files); 
				if(files.size()>0){
					System.out.println("本次扫描到圆通XML文件共：" + files.size() + "个，准备进行文件转换...");
					String now = sdf.format(new Date(System.currentTimeMillis()));
					for (int i = 0; i < files.size(); i++) {
						try {
							/**
							 * 文件备份
							 */
							File bakFolder = new File(ytoBackupPath+"/"+now);
							File errorFolder = new File(ytoErrorPath+"/"+now);
							//判断文件夹是否存在如果不存在创建此文件夹
							fileIsExist(bakFolder,new File(ytoDestPath),errorFolder);
							File bakFile = new File(bakFolder+"/"+files.get(i).getName());
							File tempFile = new File(ytoDestPath+"/"+System.currentTimeMillis()+".temp");
							File destFile = new File(ytoDestPath+"/"+files.get(i).getName().replace(".xml", ".csv"));
							File errorFile = new File(errorFolder+"/" + files.get(i).getName());
							//如果找不到当前文件就contrinue进行转换下一个文件
							FileUtil.copy(files.get(i), bakFile);
							util.analysis(files.get(i), tempFile, errorFile);
							/**
							 * 如果当前临时文件为Ok的情况，则不重命名为CSV文件，直接删除此临时文件
							 */
							if(tempFile.length() == 0){
								tempFile.delete();
							}else{
								tempFile.renameTo(destFile);
							}
							/**
							 * 如果源文件存在，则删除源文件
							 */
							if(files.get(i).exists()){
								boolean flag = files.get(i).delete();
								if(!flag){
									System.out.println("删除圆通源文件失败："+files.get(i).getName()+"     "+new Date());
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
					System.out.println("本次转换圆通文件完成...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(ytoInterval*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断文件夹是否存在如果不存在创建此文件夹
	 *2015-8-25
	 */
	private void fileIsExist(File bakFolder,File dest,File error) {
		if(!bakFolder.exists()){
			bakFolder.mkdirs();
		}
		if(!dest.exists()){
			dest.mkdirs();
		}
		if(!error.exists()){
			error.mkdirs();
		}
		
	}
	
}

