package org.fffd.l23o6.util.strategy.payment;

public class AlipayPaymentStrategy extends PaymentStrategy{
    public static final AlipayPaymentStrategy INSTANCE = new AlipayPaymentStrategy();

    private double acount;

    @Override
    public void pay(double amount) {
        acount -= amount;
    }

    @Override
    public void refund(double amount) {
        acount += amount;
    }
}
