package com.epam.koryagin.xmlbeans;

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
	
	public static long getJMSPriority(XmlObject xml) {
		long rating = 0L;
		long grade = 0L;
		long priority = MIN_JMSPRIORITY;
		// DataDocument dataDocument = (DataDocument) xml;
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

}
