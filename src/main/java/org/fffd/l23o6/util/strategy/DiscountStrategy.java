package org.fffd.l23o6.util.strategy;

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
        double discount = 0.0;
        int usedMileagePoints = 0;
        for (int i = 0; i < 5; i++) {
            if (mileagePoints > DISCOUNT_TABLE[i][0]) {
                discount = DISCOUNT_TABLE[i][1] + (mileagePoints - DISCOUNT_TABLE[i][0]) * DISCOUNT_TABLE[i][2] * 0.01;
                discount = (discount > DISCOUNT_TABLE[i][3]) ? DISCOUNT_TABLE[i][3] : discount;
                if(discount > amount){
                    usedMileagePoints += (amount - DISCOUNT_TABLE[i][1]) / (DISCOUNT_TABLE[i][2] * 0.01);
                    break;
                }
                usedMileagePoints += (discount - DISCOUNT_TABLE[i][1]) / (DISCOUNT_TABLE[i][2] * 0.01);
            }
        }
        return new double[]{discount,usedMileagePoints};
    }
}
