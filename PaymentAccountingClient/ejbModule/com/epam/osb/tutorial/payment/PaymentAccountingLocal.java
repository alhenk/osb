package com.epam.osb.tutorial.payment;
import javax.ejb.Local;

@Local
public interface PaymentAccountingLocal {
	
	public String validatePayment(String payment);

}
