package com.epam.osb.tutorial.payment;
import javax.ejb.Local;

@Local
public interface PaymentAccountingLocal {
	
	public long validatePayment(String payment);

}
