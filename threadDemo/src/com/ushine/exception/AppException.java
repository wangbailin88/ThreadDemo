package com.ushine.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义异常
 * @author Lee
 *
 */
public class AppException extends RuntimeException {
	
	private long timestamp;
	
	private int code;
	
	private String msg;
	
	public AppException(int code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
		this.timestamp = System.currentTimeMillis();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		Date date = new Date(timestamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		sb.append(format.format(date)).append(" ")
		.append(this.getClass().getName()).append(":")
		.append("ErrorCode=").append(this.code).append("\t")
		.append(this.msg);
		return sb.toString();
	}
}
