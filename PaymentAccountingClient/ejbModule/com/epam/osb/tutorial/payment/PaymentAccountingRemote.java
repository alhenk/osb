package com.epam.osb.tutorial.payment;
import javax.ejb.Remote;

import org.apache.xmlbeans.XmlObject;

@Remote
public interface PaymentAccountingRemote {
	
	public String validatePayment(XmlObject payment);

}
