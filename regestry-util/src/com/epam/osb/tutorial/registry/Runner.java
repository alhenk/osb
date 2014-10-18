package com.epam.osb.tutorial.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlException;

public class Runner {
	private static InputStream getResource(String filename) {
		return Runner.class.getClassLoader().getResourceAsStream(filename);
	}
	
	private static final String TEST_BASE64_ZIP="UEsDBBQAAAAIAHBPUEUAS9mI1gAAAAkEAAARAAAAdGVzdC1vcmlnaW5hbC50eHSl090KgjAUB/Dr"
+"gt5Bdp/LzLQwu+sJ6gHMho10E3eI7Ok7CtIHHQsmCPufs53fzRZvb2XhXEVtpFYb5rkz5giV6ZNU"
+"+YYd9rtpxLbJZBzXIl/jLw3UjYNnlMGIW84A1Zrzy93NJBhXm6Ob6ZKDMMD7/QwHOPh1Q6q0KYUC"
+"LI1GXUGCKBMv5v36ozP/1gENaZH42OpD28P4DryC/4ztkk+DgQXok+CCBkMLcEGCAQ2uLMCABJc0"
+"6HkW4pIUwwHR5tqEpBgNiDb3JiLFFS3Of4ltfj5oLDwAUEsBAj8AFAAAAAgAcE9QRQBL2YjWAAAA"
+"CQQAABEAJAAAAAAAAAAgAAAAAAAAAHRlc3Qtb3JpZ2luYWwudHh0CgAgAAAAAAABABgAthyNlvXo"
+"zwG2HI2W9ejPATbfNC4H3s8BUEsFBgAAAAABAAEAYwAAAAUBAAAAAA==";

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
		
		
		try {
			System.out.println(ZipUtil.unzipTextFile(TEST_BASE64_ZIP));
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
