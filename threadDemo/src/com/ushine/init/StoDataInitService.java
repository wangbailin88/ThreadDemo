/**
 * 
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

/**
 * @author LiYan
 * @Date 2016年3月8日
 * @Describe 
 */
public class StoDataInitService extends Thread implements ISystemService {
	/**
	 * 源文件路径
	 */
	private static String stoSourcePath = "";
	/**
	 * 文件备份路径
	 */
	private static String stoBackupPath  = "";
	/**
	 * 目标文件路径
	 */
	private static String stoDestPath  = "";
	private static String stoErrorPath  = "";
	private static int stoInterval = 1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 读取路径配置文件
	 */
	static{
		String path = PathUtils.getCurrentThreadClassPath()+"config/system.properties";
		PropertiesUtils util = new PropertiesUtils();
		util.load(path);
		stoSourcePath = util.getValue("sto_source_path");
		stoBackupPath = util.getValue("sto_backup_path");
		stoDestPath = util.getValue("sto_dest_path");
		stoErrorPath = util.getValue("sto_error_path");
		stoInterval = Integer.parseInt(util.getValue("sto_interval"));
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
		return "中通数据转换服务";
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
			thread.interrupt();
			stop = false;
			thread = null;
		}
	}
	
	@Override
	public void run() {
		AnalysisXmlUtils util = new AnalysisXmlUtils();
		while (!Thread.interrupted() && stop) {
			try {
				List<File> files = new ArrayList<>();
				FileUtil.fileScanning(stoSourcePath, files); 
				if(files.size()>0){
					System.out.println("本次扫描到申通XML文件共：" + files.size() + "个，准备进行文件转换...");
					String now = sdf.format(new Date(System.currentTimeMillis()));
					for (int i = 0; i < files.size(); i++) {
						try {
							/**
							 * 文件备份
							 */
							File bakFolder = new File(stoBackupPath+"/"+now);
							File errorFolder = new File(stoErrorPath + "/" + now);
							//判断文件夹是否存在如果不存在创建此文件夹
							fileIsExist(bakFolder,new File(stoDestPath),errorFolder);
							File bakFile = new File(bakFolder+"/"+files.get(i).getName());
							File tempFile = new File(stoDestPath+"/"+System.currentTimeMillis()+".temp");
							File destFile = new File(stoDestPath+"/"+files.get(i).getName().replace(".xml", ".csv"));
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
							if(files.get(i).exists()){
								boolean flag = files.get(i).delete();
								if(!flag){
									System.out.println("删除中通源文件失败："+files.get(i).getName()+"     "+new Date());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
					System.out.println("本次转换中通文件完成...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(stoInterval*1000);
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
