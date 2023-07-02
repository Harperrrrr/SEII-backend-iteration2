package org.fffd.l23o6.util.strategy.payment;

public class WeChatPaymentStrategy extends PaymentStrategy{
    public static final WeChatPaymentStrategy INSTANCE = new WeChatPaymentStrategy();
    private double acount;

    @Override
    public void pay(double amount) {
        if (amount > acount){

        }
        acount -= amount;
    }

    @Override
    public void refund(double amount) {
        acount += amount;
    }
}
