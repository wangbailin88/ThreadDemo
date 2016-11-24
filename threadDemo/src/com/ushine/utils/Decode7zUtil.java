package com.ushine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
/**7zbinding*/
public class Decode7zUtil {

/**
 * 解压加密的.7z文件工具方法
 * @date 2016-6-17
 * @author liy
 * @param sourceFile
 * @param tempFile
 * @param destFile
 * @param password
 * @throws Exception
 */
 public static void extractile(String sourceFile, String tempFile, String destFile,String password) throws Exception{
     RandomAccessFile randomAccessFile = null;
      IInArchive inArchive = null;
      try {
        randomAccessFile = new RandomAccessFile(sourceFile, "r");
        inArchive = SevenZip.openInArchive(null, // autodetect archive type
            new RandomAccessFileInStream(randomAccessFile));

        // Getting simple interface of the archive inArchive
        ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

      //  System.out.println("  Hash  |  Size  | Filename");
       // System.out.println("----------+------------+---------");
        File file = new File(tempFile);
        final FileOutputStream fos = new FileOutputStream(file);
        for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
          final int[] hash = new int[] { 0 };
          if (!item.isFolder()) {
            ExtractOperationResult result;

            final long[] sizeArray = new long[1];
            result = item.extractSlow(new ISequentialOutStream() {
              public int write(byte[] data) throws SevenZipException {

                //Write to file
               
                try {
                  
                  //error occours below
//                 file.getParentFile().mkdirs();
                 
                  fos.write(data);

                } catch (FileNotFoundException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }

                hash[0] ^= Arrays.hashCode(data); // Consume data
                sizeArray[0] += data.length;
                return data.length; // Return amount of consumed data
              }
            },password);
            if (result != ExtractOperationResult.OK) {
              System.err.println("Error extracting item: " + result);
              throw new IOException();
            }
          }
        }
        fos.close();
        if(!file.renameTo(new File(destFile))){
        	throw new IOException();
        }
      } catch (Exception e) {
        System.err.println("Error occurs: " + e);
        e.printStackTrace();
        System.exit(1);
      } finally {
    	 
        if (inArchive != null) {
          try {
            inArchive.close();
          } catch (SevenZipException e) {
            System.err.println("Error closing archive: " + e);
          }
        }
        if (randomAccessFile != null) {
          try {
            randomAccessFile.close();
          } catch (IOException e) {
            System.err.println("Error closing file: " + e);
          }
        }
      }
  }
}
