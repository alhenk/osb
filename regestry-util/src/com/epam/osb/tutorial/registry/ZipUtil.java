package com.epam.osb.tutorial.registry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class ZipUtil {
	/** 
	   * At server side, use ZipOutputStream to zip text to byte array, then convert 
	   * byte array to base64 string, so it can be trasnfered via http request. 
	   */ 
	  public static String compressString(String srcTxt) 
	      throws IOException { 
	    ByteArrayOutputStream rstBao = new ByteArrayOutputStream(); 
	    GZIPOutputStream zos = new GZIPOutputStream(rstBao); 
	    zos.write(srcTxt.getBytes()); 
	    IOUtils.closeQuietly(zos); 
	      
	    byte[] bytes = rstBao.toByteArray(); 
	    return Base64.encodeBase64String(bytes); 
	  }
	  
	  /** 
	   * When client receives the zipped base64 string, it first decode base64 
	   * String to byte array, then use ZipInputStream to revert the byte array to a 
	   * string. 
	   */
	  public static String uncompressString(String zippedBase64Str) 
	      throws IOException { 
	    String result = null; 
	    byte[] bytes = Base64.decodeBase64(zippedBase64Str); 
	    GZIPInputStream zi = null; 
	    try { 
	      zi = new GZIPInputStream(new ByteArrayInputStream(bytes)); 
	      result = IOUtils.toString(zi);
	      System.out.println(result);
	    } finally { 
	      IOUtils.closeQuietly(zi); 
	    } 
	    return result; 
	  }
}
