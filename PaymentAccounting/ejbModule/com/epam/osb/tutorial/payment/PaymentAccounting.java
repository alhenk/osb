package com.epam.osb.tutorial.payment;

import java.util.UUID;

import javax.ejb.Stateless;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.osb.cits.kz.test.registry.PaymentDocument;

/**
 * Session Bean implementation class PaymentAccounting
 */
@Stateless(mappedName = "paymentAccounting")
public class PaymentAccounting implements PaymentAccountingRemote, PaymentAccountingLocal {
	private static Long FAIL = -1L;

	/**
	 * Default constructor.
	 */
	public PaymentAccounting() {
	}

	@Override
	public String validatePayment(XmlObject payment) {
		Long total = 0L;
		Long grandTotal = 0L;
		XmlCursor cursor = null;
		UUID id = UUID.randomUUID();
		PaymentDocument paymentDoc;
		try {
			paymentDoc = PaymentDocument.Factory.parse(payment.xmlText());
			cursor = paymentDoc.newCursor();
			while (!cursor.toNextToken().isNone()) {
				if (cursor.currentTokenType().intValue() == XmlCursor.TokenType.INT_START
						&& cursor.getName().getLocalPart().equals("item")) {
					total = total + new Long(cursor.getTextValue());
				}
				if (cursor.currentTokenType().intValue() == XmlCursor.TokenType.INT_START
						&& cursor.getName().getLocalPart().equals("total")) {
					grandTotal = new Long(cursor.getTextValue());
				}
			}
		} catch (NumberFormatException e) {
			e.getMessage();
			return FAIL.toString();
		} catch (XmlException e) {
			e.printStackTrace();
			return FAIL.toString();
		} finally {
			if (cursor != null) {
				cursor.dispose();
			}
		}
		return total.longValue() == grandTotal.longValue() ? id.toString()
				: FAIL.toString();
	}
}
