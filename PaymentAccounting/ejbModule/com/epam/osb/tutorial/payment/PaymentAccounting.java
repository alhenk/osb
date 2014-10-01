package com.epam.osb.tutorial.payment;

import javax.ejb.Stateless;

/**
 * Session Bean implementation class PaymentAccounting
 */
@Stateless(mappedName = "paymentAccounting")
public class PaymentAccounting implements PaymentAccountingRemote, PaymentAccountingLocal {

    /**
     * Default constructor. 
     */
    public PaymentAccounting() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public long validatePayment(String payment) {
		// TODO Auto-generated method stub
		return -1;
	}

}
