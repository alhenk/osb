package com.epam.osb.tutorial.payment;

import java.util.UUID;

import javax.ejb.Stateless;

/**
 * Session Bean implementation class PaymentAccounting
 */
@Stateless(mappedName = "paymentAccounting")
public class PaymentAccounting implements PaymentAccountingRemote,
		PaymentAccountingLocal {

	/**
	 * Default constructor.
	 */
	public PaymentAccounting() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String validatePayment(String payment) {
		UUID uid = UUID.randomUUID();
		return "Pay for " + payment + " "+uid;
	}

}
