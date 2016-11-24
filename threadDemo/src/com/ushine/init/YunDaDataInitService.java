package com.ushine.init;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ushine.utils.AnalysisXmlUtils;
import com.ushine.utils.Decode7zUtil;
import com.ushine.utils.FileUtil;
import com.ushine.utils.PathUtils;
import com.ushine.utils.PropertiesUtils;

/**
 *
 * @date 2016-5-23
 * @author liy
 */
public class YunDaDataInitService extends Thread implements ISystemService {

	/**
	 * 源文件路径
	 */
	private static String yundaSourcePath = "";
	/**
	 * 文件备份路径
	 */
	private static String yundaBackupPath  = "";
	/**
	 * 目标文件路径
	 */
	private static String yundaDestPath  = "";
	private static String yundaErrorPath  = "";
	private static int yundaInterval = 1;
	/**
	 * 解压出错文件保存目录
	 */
	private static String yunda7zErrorPath = "";
	/**
	 * 加密文件的解压密钥
	 */
	private static String decodeKey = "";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 读取路径配置文件
	 */
	static{
		String path = PathUtils.getCurrentThreadClassPath()+"config/system.properties";
		PropertiesUtils util = new PropertiesUtils();
		util.load(path);
		yundaSourcePath = util.getValue("yunda_source_path");
		yundaBackupPath = util.getValue("yunda_backup_path");
		yundaDestPath = util.getValue("yunda_dest_path");
		yundaErrorPath = util.getValue("yunda_error_path");
		yundaInterval = Integer.parseInt(util.getValue("yunda_interval"));
		yunda7zErrorPath = util.getValue("yunda_7z_error_path");
		decodeKey = util.getValue("decode_key");
	}
	/**
	 * 当前线程
	 */
	private Thread thread;
	/**
	 * 是否停止当前线程
	 */
	private volatile boolean yundap = true;
	/**
	 * 线程睡眠时间，单位：秒，从配置文件读取
	 */
	@Override
	public String serviceName() {
		return "韵达数据转换服务";
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
			yundap = false;
			thread = null;
		}
	}
	
	@Override
	public void run() {
		AnalysisXmlUtils util = new AnalysisXmlUtils();
		while (!Thread.interrupted() && yundap) {
			decodeFiles();	
			long transStatTime = System.currentTimeMillis();
			try {
				//
				List<File> files = new ArrayList<>();
				FileUtil.fileScanning(yundaSourcePath, files); 
				if(files.size()>0){
					System.out.println("本次扫描到韵达XML文件共：" + files.size() + "个，准备进行文件转换...");
					String now = sdf.format(new Date(System.currentTimeMillis()));
					for (int i = 0; i < files.size(); i++) {
						try {
							/**
							 * 文件备份
							 */
							File bakFolder = new File(yundaBackupPath+"/"+now);
							File errorFolder = new File(yundaErrorPath+"/"+now);
							//判断文件夹是否存在如果不存在创建此文件夹
							fileIsExist(bakFolder,new File(yundaDestPath),errorFolder);
							File bakFile = new File(bakFolder+"/"+files.get(i).getName());
							File tempFile = new File(yundaDestPath+"/"+System.currentTimeMillis()+".temp");
							File destFile = new File(yundaDestPath+"/"+files.get(i).getName().replace(".xml", ".csv"));
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
					long transEndTime = System.currentTimeMillis();
					System.out.println("本次转换中通文件完成，耗时"+(transEndTime-transStatTime)/1000/60+"分钟");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(yundaInterval*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将suorce目录下的所有。7z加密文件进行解密，解密出错的文件保存到指定的目录，解密成功后删除加密的.7z文件
	 * @date 2016-6-17
	 * @author liy
	 */
	public void decodeFiles(){
		File sourceFolder = new File(yundaSourcePath);
		/**
		 * 递归扫描源文件目录下的素有加密文件
		 */
		File[] files = sourceFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				long time = pathname.lastModified();
				if(pathname.isDirectory()){
					return true;
				}
				if(pathname.getName().endsWith(".7z") && (System.currentTimeMillis() - time)>1000*60*10){
					return true;
				}
				return false;
			}
		});
		/**
		 * 依次解压加密文件
		 */
		if(files.length>0){
			long decodeStatTime = System.currentTimeMillis();
			for (File file : files) {
				String now = sdf.format(new Date());
				//System.out.println(file.getAbsolutePath());
				String sourceFile = file.getAbsolutePath();
				String fileName = file.getName();
				String tempFile = yundaSourcePath+"/"+System.currentTimeMillis()+".temp";
				String destFile = yundaSourcePath+"/"+fileName.substring(0, fileName.lastIndexOf("."));
				
				try {
					Decode7zUtil.extractile(sourceFile, tempFile, destFile, decodeKey);
					/**
					 * 解压成功后删除加密文件
					 */
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
					/**
					 * 解压失败备份解压错误的文件
					 */
					File errorFile = new File(yunda7zErrorPath+"/"+now+"/"+fileName);
					/**
					 * 如果备份目录不存在，则创建目录
					 */
					if(!errorFile.getParentFile().exists()){
						errorFile.getParentFile().mkdirs();
					}
					FileUtil.move(file, errorFile);
				}
			}
			long decodeEndTime = System.currentTimeMillis();
			System.out.println("本次文件解压完成,耗时"+(decodeEndTime-decodeStatTime)/1000/60+"分钟");
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
