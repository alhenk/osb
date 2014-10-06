package com.epam.osb.tutorial.payment;
import javax.ejb.Local;

import org.apache.xmlbeans.XmlObject;

@Local
public interface PaymentAccountingLocal {
	
	public String validatePayment(XmlObject payment);

}
