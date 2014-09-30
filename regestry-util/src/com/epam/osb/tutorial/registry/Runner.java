package com.epam.osb.tutorial.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class Runner {
	private static InputStream getResource(String filename) {
		return Runner.class.getClassLoader().getResourceAsStream(filename);
	}

	public static void main(String... args) throws IOException {
		String sourceFile = "test-original.txt";
		String zippedFile = "test-base64.txt";
		String revertedFile = "test-reverted.txt";

		InputStream fis = getResource(sourceFile);
		String srcTxt = IOUtils.toString(fis, "UTF-8");
		IOUtils.closeQuietly(fis);
		System.out.println("Source text = " + srcTxt);

		String str = ZipUtil.compressString(srcTxt);
		System.out.println("str = " + str);
		FileWriter fw = null;
		URL url = Runner.class.getClassLoader().getResource(zippedFile);
		System.out.println("URL = " + url);

		try {
			fw = new FileWriter(new File(url.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		IOUtils.write(str, fw);
		IOUtils.closeQuietly(fw);

		fis = getResource(zippedFile);
		String zippedBase64Str = IOUtils.toString(fis, "UTF-8");
		IOUtils.closeQuietly(fis);

		String originalStr = ZipUtil.uncompressString(zippedBase64Str);
		url = Runner.class.getClassLoader().getResource(revertedFile);
		try {
			fw = new FileWriter(new File(url.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IOUtils.write(originalStr, fw);
		IOUtils.closeQuietly(fw);
	}
}
