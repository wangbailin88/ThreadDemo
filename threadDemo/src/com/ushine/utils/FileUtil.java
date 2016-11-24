package com.ushine.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.ushine.exception.AppException;
import com.ushine.exception.ErrorCode;

/**
 * 
 * @author Lee
 *
 */
public class FileUtil {

	public static void move(File source, File dest){
		if(!dest.exists()){
			try {
				dest.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new AppException(ErrorCode.ERROR_IO, "无法创建文件:"+dest.getAbsolutePath());
			}
		}
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			byte[] data = new byte[1024];
			int len = 0;
			while((len = fis.read(data))!=-1){
				fos.write(data, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.ERROR_IO, "移动文件失败:"+dest.getAbsolutePath()+e.getMessage());
		} finally {
			try {
				if(fis != null){
					fis.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fos != null){
					fos.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		source.delete();
	}
	
	public static void copy(File source, File dest){
		if(!dest.exists()){
			try {
				dest.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new AppException(ErrorCode.ERROR_IO, "无法创建文件:"+dest.getAbsolutePath());
			}
		}
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			byte[] data = new byte[1024];
			int len = 0;
			while((len = fis.read(data))!=-1){
				fos.write(data, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.ERROR_IO, "移动文件失败:"+dest.getAbsolutePath()+e.getMessage());
		} finally {
			try {
				if(fis != null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fos != null){
					fos.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteFile(String fileName){
		if(fileName==null||fileName.trim().equals("")){
			throw new AppException(ErrorCode.ERROR_RUNTIME_NULL, "文件路径不为能空！");
		}
		File file = new File(fileName);
		file.delete();
	}
	
	
	public static String extName(String fileName){
		int start = fileName.lastIndexOf(".");
		return fileName.substring(start+1);
	}
	
	public static String fileName(String fullName){
		int start = fullName.lastIndexOf(".");
		return fullName.substring(0, start);
	}
	

	/**
	 * 文件的递归扫描
	 * @param tempFilePath 待扫描的文件路径
	 */
	public static void fileScanning(String tempFilePath,List<File> files){
		try {
			File companyTempFile = new File(tempFilePath);
			if(!companyTempFile.exists()){
				companyTempFile.mkdirs();
				System.out.println("创建路径:"+tempFilePath);
			}
			File[] childFiles = companyTempFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					long time = pathname.lastModified();
					if(pathname.isDirectory()){
						return true;
					}
					if(pathname.getName().endsWith(".xml") && (System.currentTimeMillis() - time)>1000*60*10){
						return true;
					}
					return false;
				}
			});
			for(int i=0; i<childFiles.length; i++){
				if(!childFiles[i].isDirectory()){
					if(childFiles[i].getName() == null || "".equals(childFiles[i].getName())){
						continue;
					}
					files.add(childFiles[i]);
				}else{
					fileScanning(childFiles[i].getPath(),files);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 文件的递归扫描
	 * @param tempFilePath 待扫描的文件路径
	 */
	public static void fileScanningTxt(String tempFilePath,List<File> files){
		try {
			File companyTempFile = new File(tempFilePath);
			if(!companyTempFile.exists()){
				companyTempFile.mkdirs();
				System.out.println("创建路径:"+tempFilePath);
			}
			File[] childFiles = companyTempFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()){
						return true;
					}
					if(pathname.getName().endsWith(".txt")){
						return true;
					}
					return false;
				}
			});
			for(int i=0; i<childFiles.length; i++){
				if(!childFiles[i].isDirectory()){
					if(childFiles[i].getName() == null || "".equals(childFiles[i].getName())){
						continue;
					}
					files.add(childFiles[i]);
				}else{
					fileScanning(childFiles[i].getPath(),files);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
