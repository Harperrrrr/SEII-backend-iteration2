package org.fffd.l23o6.util.strategy.payment;

import java.util.HashMap;
import java.util.Map;


public abstract class PaymentStrategy {

    public abstract void pay(double amount);

    public abstract void refund(double amount);
}
