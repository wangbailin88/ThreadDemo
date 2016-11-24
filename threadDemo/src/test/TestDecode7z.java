package test;

import java.io.File;

import com.ushine.utils.Decode7zUtil;

/**
 *
 * @date 2016-6-17
 * @author liy
 */
public class TestDecode7z {

	/**
	 * @date 2016-6-17
	 * @author liy
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = "F:/Bruce-Work/物流公司文档相关/韵达/yundajzyunda0002.20160521.expresses.xml.7z";
		String filePath1 = "F:/Bruce-Work/物流公司文档相关/韵达/yundajzyunda0002.20160521.expresses.temp";
		String filePath2 = "F:/Bruce-Work/物流公司文档相关/韵达/yundajzyunda0002.20160521.expresses.xml";
		String password="jvhDW8ZyNnJutbRs";
		try {
			File file = new File(filePath);
			//System.out.println(file.getName().substring(0, file.getName().lastIndexOf(".")));
			Decode7zUtil.extractile(filePath,filePath1,filePath2,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
