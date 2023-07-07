package org.fffd.l23o6.util.strategy;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;

public class DiscountStrategy {
    public static final DiscountStrategy INSTANCE = new DiscountStrategy();
    private static final double[][] DISCOUNT_TABLE = createDiscountTable();

    private static double[][] createDiscountTable() {
        double[][] discountTable = {
                {0, 0, 0.1, 1},
                {1000, 1, 0.15, 4},
                {3000, 4, 0.2, 18},
                {10000, 18, 0.25, 118},
                {50000, 118, 0.3, Integer.MAX_VALUE}
        };
        return discountTable;
    }

    public double[] getDiscountWithPoints(int mileagePoints, double amount) {
        if (mileagePoints < 0 || amount < 0) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS,"不合法的数据");
        }
        double discount = 0.0;
        int usedMileagePoints = 0;
        for (int i = 0; i < 5; i++) {
            if (mileagePoints > DISCOUNT_TABLE[i][0]) {
                discount = DISCOUNT_TABLE[i][1] + (mileagePoints - DISCOUNT_TABLE[i][0]) * DISCOUNT_TABLE[i][2] * 0.01;
                discount = Math.min(discount, DISCOUNT_TABLE[i][3]);
                if (discount > amount) {
                    usedMileagePoints += (amount - DISCOUNT_TABLE[i][1]) / (DISCOUNT_TABLE[i][2] * 0.01);
                    discount = amount;
                    break;
                }
                usedMileagePoints += (discount - DISCOUNT_TABLE[i][1]) / (DISCOUNT_TABLE[i][2] * 0.01);
            }
        }
        return new double[]{discount, usedMileagePoints};
    }
}
