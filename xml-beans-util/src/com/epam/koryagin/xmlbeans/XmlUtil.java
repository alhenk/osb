package com.epam.koryagin.xmlbeans;

import java.util.UUID;
import javax.xml.namespace.QName;

import kz.test.osbservice.InfoDocument;
import kz.test.osbservice.InfoType;
import kz.test.osbservice.MessageType;


import noNamespace.ResponseDocument;
import noNamespace.ResponseType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;


import com.osb.cits.kz.test.registry.DataDocument;
import com.osb.cits.kz.test.registry.DataType;
import com.osb.cits.kz.test.registry.RequestDocument;
import com.osb.cits.kz.test.registry.RequestType;

public class XmlUtil {
	public static final Logger LOGGER = Logger.getLogger(XmlUtil.class);
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";
	public static final long MIN_JMSPRIORITY = 1L;
	public static final long MAX_JMSPRIORITY = 9L;
	public static final String TEST_NAMESPACE = "http://kz.cits.osb.com/test/registry";
	public static final String DATA_ELEMENT = "data";

	public static final String INFO = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<Info xmlns=\"http://osbservice.test.kz/\">"
			+ "<Name>InfoName</Name>"
			+ "<Class>InfoClass</Class>"
			+ "<UUID>545e417e3d5b11e48782164230d1df67</UUID>" + "</Info>";

	public static final String REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<test:request xmlns:test=\"http://kz.cits.osb.com/test/registry\">"
			+ "<test:data clazz=\"05\">"
			+ "<test:title>Serendipity</test:title>"
			+ "<test:director>Peter Chelsom</test:director>"
			+ "<test:release>MMI</test:release>"
			+ "<test:rating>2</test:rating>"
			+ "<test:grade>3</test:grade>"
			+ "</test:data>" + "</test:request>";

	public static final String DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<test:data clazz=\"05\" xmlns:test=\"http://kz.cits.osb.com/test/registry\">"
			+ "<test:title>Serendipity</test:title>"
			+ "<test:director>PeterChelsom</test:director>"
			+ "<test:release>MMI</test:release>"
			+ "<test:rating>12</test:rating>"
			+ "<test:grade>3</test:grade>"
			+ "</test:data>";

	public static final String BAD_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<test:request xmlns:test=\"http://kz.cits.osb.com/test/registry\">"
			+ "</test:request>";
	public static final String RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<response>" + "</response>";

	public static XmlObject retrieveRequestData(XmlObject xml) {
		RequestDocument requestDocument;
		DataType dataType;
		XmlObject data;
		XmlOptions opt;
		try {
			requestDocument = RequestDocument.Factory.parse(xml.xmlText());
			// requestDocument = (RequestDocument) xml.copy();
			RequestType request = requestDocument.getRequest();
			dataType = request.getData();
			if (dataType == null) {
				return null;
			}
			opt = new XmlOptions();
			opt.setSaveSyntheticDocumentElement(new QName(TEST_NAMESPACE,
					DATA_ELEMENT));
			data = XmlObject.Factory.parse(dataType.xmlText(opt));

		} catch (XmlException e) {
			LOGGER.error("Failed to parse the request envelope"
					+ e.getMessage());
			System.err.println("Failed to parse the request envelope"
					+ e.getMessage());
			return null;
		}
		return data;
	}
	
	public static XmlObject retrieveMessage(XmlObject xml) {
		RequestDocument requestDocument;
		DataType dataType;
		XmlObject data;
		XmlOptions opt;
		try {
			requestDocument = RequestDocument.Factory.parse(xml.xmlText());
			// requestDocument = (RequestDocument) xml.copy();
			RequestType request = requestDocument.getRequest();
			dataType = request.getData();
			if (dataType == null) {
				return null;
			}
			opt = new XmlOptions();
			opt.setSaveSyntheticDocumentElement(new QName(TEST_NAMESPACE,
					DATA_ELEMENT));
			data = XmlObject.Factory.parse(dataType.xmlText(opt));

		} catch (XmlException e) {
			LOGGER.error("Failed to parse the request envelope"
					+ e.getMessage());
			System.err.println("Failed to parse the request envelope"
					+ e.getMessage());
			return null;
		}
		return data;
	}

	public static long getJMSPriority(XmlObject xml) {
		long rating = 0L;
		long grade = 0L;
		long priority = MIN_JMSPRIORITY;
		// DataDocument dataDocument = (DataDocument) xml;
		DataDocument dataDocument;
		try {
			dataDocument = DataDocument.Factory.parse(xml.xmlText());
		} catch (XmlException e) {
			e.printStackTrace();
			return MIN_JMSPRIORITY;
		}
		DataType data = dataDocument.getData();
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

	public static XmlObject createResponse(String status) {
		ResponseDocument responseDocument = null;
		try {
			responseDocument = ResponseDocument.Factory.parse(RESPONSE);
			ResponseType response = responseDocument.getResponse();
			response.setStatus(status);
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return responseDocument;
	}

	public static XmlObject createInfo(XmlObject xml) {
		InfoDocument infoDocument;
		InfoType infoType;
		XmlCursor cursor = null;
		try {
			MessageType messageType = MessageType.Factory.parse(xml.xmlText());
			infoDocument = InfoDocument.Factory.parse(XmlUtil.INFO);
			infoType = infoDocument.getInfo();
			cursor = infoType.newCursor();
			while (!cursor.toNextToken().isNone()) {
				if (cursor.currentTokenType().intValue() == XmlCursor.TokenType.INT_START
						&& cursor.getName().getLocalPart().equals("UUID")) {
					cursor.setTextValue(UUID.randomUUID().toString()
							.toUpperCase().replace("-", ""));
				}
			}
			infoType.setMessage(messageType);
		} catch (XmlException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.dispose();
			}
		}
		return infoDocument;
	}

	public static XmlObject setDataClazz(XmlObject xml, String clazzValue) {

		XmlCursor cursor = xml.newCursor();
		while (!cursor.toNextToken().isNone()) {
			if (cursor.isAttr()
					&& cursor.getName().getLocalPart().equals("clazz")) {
				cursor.setTextValue(clazzValue);
			}
		}
		return xml;
	}

	public static XmlObject retrieveInfoMessage(XmlObject xml) {
		// InfoDocument infoDocument = (InfoDocument) xml;
		InfoDocument infoDocument;
		try {
			infoDocument = InfoDocument.Factory.parse(xml.xmlText());
		} catch (XmlException e) {
			e.printStackTrace();
			return null;
		}
//		InfoType info = infoDocument.getInfo();
//		MessageType message = info.getMessage();
		return infoDocument;
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
	
	public static String greet(String name){
		return "Hello "+ name + "!";
	}
}
