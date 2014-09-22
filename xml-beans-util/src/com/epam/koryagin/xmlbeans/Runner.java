package com.epam.koryagin.xmlbeans;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import com.osb.cits.kz.test.registry.RequestDocument;

public class Runner {
	public static void main(String[] args) throws XmlException {
		RequestDocument requestDocument;
		XmlObject requestData;
		XmlOptions opts;

		/*1 Receive XML REQUEST*/
		requestDocument = RequestDocument.Factory.parse(XmlUtil.REQUEST);
		/*2 Retrieve Data (message) from XML REQUEST*/
		requestData = XmlUtil.retrieveRequestData(requestDocument);
		opts = new XmlOptions();
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		System.out
				.println("REQUEST DATA:\n"
						+ (null == requestData ? "NO DATA" : requestData
								.xmlText(opts)));

		/*3 Put Data (message) into Queue and add header attribute and JMSPriority*/
		long priority = XmlUtil.getJMSPriority(requestData);
		System.out.println("JMS PRIORITY = " + priority);
				
		/*4 Send Response with Status OK/FAIL */
		XmlObject response = XmlUtil.createResponse("OK");
		System.out.println("RESPONSE:\n"
				+ (null == response ? "NO RESPONSE" : response.xmlText(opts)));
		
		/*5 Consume the message from Queue with the attribute selector (messageType = 'messageMy')*/
		/*6 Replace name space, remove prefixes 'test' and change attribute 'class' value on '06'*/
		XmlObject data = requestData.copy();
		data = XmlUtil.setDataClazz(data, "29");
		XmlObject message = data.copy();
		message = XmlUtil.removeNamespaces(message);
		System.out.println("MESSAGE:\n"
				+ (null == message ? "NO MESSAGE" : message.xmlText(opts)));
		
		/*7 Add the message into the Info, get UUID*/
		/*8 Print out the Info in log */
		XmlObject info = XmlUtil.createInfo(message);
		System.out.println("INFO:\n"
				+ (null == info ? "NO INFO" : info.xmlText(opts)));
		
		/*9 Send the Info into Proxy3 and retrieve the message */
		XmlObject messageProxy = XmlUtil.retrieveInfoMessage(info);
		System.out.println("MESSAGE PROXY3:\n"
				+ (null == message ? "NO MESSAGE PROXY3" : messageProxy.xmlText(opts)));
	}
}
