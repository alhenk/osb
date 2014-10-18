package com.epam.osb.tutorial.registry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.xml.stream.XMLStreamException;

public class ZipUtil {
	private static final String ZIP_EXCEPTION = "The zipEntry is not a text file, because it contains the directory with the name ";
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * At server side, use ZipOutputStream to zip text to byte array, then
	 * convert byte array to base64 string, so it can be trasnfered via http
	 * request.
	 */
	public static String compressString(String srcTxt) throws IOException {
		ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(rstBao);
		zos.write(srcTxt.getBytes());
		IOUtils.closeQuietly(zos);

		byte[] bytes = rstBao.toByteArray();
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * When client receives the zipped base64 string, it first decode base64
	 * String to byte array, then use ZipInputStream to revert the byte array to
	 * a string.
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

	/**
	 * Decode base64 coding and unzip
	 * 
	 * @param base64data
	 * @return XmlObject
	 * @throws IOException
	 * @throws XmlException
	 */
	public static String unzipTextFile(String base64data)
			throws XMLStreamException, IOException, XmlException {
		// Decode & unzip
		byte[] decoded = DatatypeConverter.parseBase64Binary(base64data);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				decoded);
		ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
		ZipEntry entry = zipInputStream.getNextEntry();
		if (entry.isDirectory()) {
			throw new RuntimeException(ZIP_EXCEPTION + entry.getName());
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count;
		while ((count = zipInputStream.read(buffer, 0, 1024)) > -1) {
			outputStream.write(buffer, 0, count);
		}
		String xmlAsString = new String(outputStream.toByteArray(),
				DEFAULT_ENCODING);
		return xmlAsString;
		//return XmlObject.Factory.parse(xmlAsString);
	}
}
