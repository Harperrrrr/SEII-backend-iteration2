package org.fffd.l23o6.util.strategy;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import jakarta.annotation.Resource;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStrategyTest {
    private DiscountStrategy discountStrategy = new DiscountStrategy();

    @Test
    public void invalidArgument(){
        assertThrows(BizException.class,() ->{
            discountStrategy.getDiscountWithPoints(-1,100);
        });
    }
    @Test
    public void equal0(){
        double[] actual = discountStrategy.getDiscountWithPoints(0,150);
        double[] expected = {0,0};
        Assertions.assertArrayEquals(expected,actual);
    }
    @Test
    public void below1k(){
        Random random = new Random();
        int min = 0; // 最小值（包括）
        int max = 1000; // 最大值（不包括）
        int randomNumber = random.nextInt(max - min) + min;
        double[] actual = discountStrategy.getDiscountWithPoints(randomNumber,100);
        double[] expected = {randomNumber*0.1*0.01,randomNumber};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from1kTo3k(){
        Random random = new Random();
        int min = 1000; // 最小值（包括）
        int max = 3000; // 最大值（不包括）
        int randomNumber = random.nextInt(max - min) + min;
        double[] actual = discountStrategy.getDiscountWithPoints(randomNumber,150);
        double[] expected = {1000*0.1*0.01 + (randomNumber-1000)*0.15*0.01,randomNumber};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void equal3k(){
        double[] actual = discountStrategy.getDiscountWithPoints(3000,150);
        double[] expected = {1000*0.1*0.01 + 2000*0.15*0.01,3000};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from3kTo10k(){
        Random random = new Random();
        int min = 3000; // 最小值（包括）
        int max = 10000; // 最大值（不包括）
        int randomNumber = random.nextInt(max - min) + min;
        double[] actual = discountStrategy.getDiscountWithPoints(randomNumber,150);
        double[] expected = {1000*0.1*0.01 + 2000*0.15*0.01 + (randomNumber-3000)*0.2*0.01,randomNumber};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from10kTo50k(){
        Random random = new Random();
        int min = 10000; // 最小值（包括）
        int max = 26000; // 最大值（不包括）
        int randomNumber = random.nextInt(max - min) + min;
        double[] actual = discountStrategy.getDiscountWithPoints(randomNumber,150);
        double[] expected = {1000*0.1*0.01 + 2000*0.15*0.01 + 7000*0.2*0.01 + (randomNumber-10000)*0.25*0.01,randomNumber};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from10kTo50kWithLowPrice(){
        Random random = new Random();
        int min = 49000; // 最小值（包括）
        int max = 50000; // 最大值（不包括）
        int randomNumber = random.nextInt(max - min) + min;
        double[] actual = discountStrategy.getDiscountWithPoints(randomNumber,100);
        double[] expected = {100,10000 + 82.0/(0.25*0.01)};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void equal50kWithLowPrice(){
        double[] actual = discountStrategy.getDiscountWithPoints(50000,100);
        double[] expected = {100,10000 + 82.0/(0.25*0.01)};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void above50k(){
        double[] actual = discountStrategy.getDiscountWithPoints(68998,250);
        double[] expected = {118 + 18998*0.3*0.01,68998};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void above50kWithLowPrice(){
        double[] actual = discountStrategy.getDiscountWithPoints(168998,150);
        double[] expected = {150,(int)(50000+32/(0.3*0.01))};
        Assertions.assertArrayEquals(expected,actual);
    }
}