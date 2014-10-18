package com.epam.koryagin.xmlbeans;

import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.osb.cits.kz.test.registry.MessageDocument;
import com.osb.cits.kz.test.registry.MessageType;

public class MessageUtil {
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";
	public static final long MIN_JMSPRIORITY = 1L;
	public static final long MAX_JMSPRIORITY = 9L;
	public static final String MESSAGE_NAMESPACE = "http://osbservice.test.kz/";
	public static final String DATA_ELEMENT = "data";

	public static final String INFO = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<Info xmlns=\"http://osbservice.test.kz/\">"
			+ "<Name>InfoName</Name>"
			+ "<Class>InfoClass</Class>"
			+ "<UUID>CC5761ADA33D4DDA98C8FFC3308E6B61</UUID>" + "</Info>";
	
	public static final String MESSAGE = 
			"<test:Message class=\"05\" xmlns:test=\"http://kz.cits.osb.com/test/registry\">"
			+ "<test:title>Serendipity</test:title>"
			+ "<test:director>PeterChelsom</test:director>"
			+ "<test:release>MMI</test:release>"
			+ "<test:rating>2</test:rating>"
			+ "<test:grade>3</test:grade>"
			+ "</test:Message>";

	public static long getJMSPriority(XmlObject xml) {
		long rating = 0L;
		long grade = 0L;
		long priority = MIN_JMSPRIORITY;
		MessageDocument messageDocument;
		try {
			messageDocument = MessageDocument.Factory.parse(xml.xmlText());
		} catch (XmlException e) {
			e.printStackTrace();
			return MIN_JMSPRIORITY;
		}
		MessageType data = messageDocument.getMessage();
		if (data != null) {
			rating = (long) data.getRating();
			grade = (long) data.getGrade();
		}
		priority = rating + grade;
		if (priority < 1)
			priority = MIN_JMSPRIORITY;
		if (priority > 9)
			priority = MAX_JMSPRIORITY;
		return priority;
	}

	public static String getRandomUUID() {
		return UUID.randomUUID().toString().toUpperCase().replace("-", "");
	}

	public static XmlObject removeNamespaces(XmlObject xml) {
		XmlObject root = xml.copy();
		String localPart;
		XmlCursor cursor = root.newCursor();
		cursor.toNextToken();
		while (cursor.hasNextToken()) {
			if (cursor.isNamespace()) {
				cursor.removeXml();
			} else {
				if (cursor.isStart() || cursor.isAttr()) {
					localPart = cursor.getName().getLocalPart();
					cursor.setName(new QName(localPart));
				}
				cursor.toNextToken();
			}
		}
		cursor.dispose();
		return root;
	}
}
