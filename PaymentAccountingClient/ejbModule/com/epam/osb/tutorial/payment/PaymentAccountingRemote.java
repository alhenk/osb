package com.epam.osb.tutorial.payment;
import javax.ejb.Remote;

@Remote
public interface PaymentAccountingRemote {
	
	public String validatePayment(String payment);

}
