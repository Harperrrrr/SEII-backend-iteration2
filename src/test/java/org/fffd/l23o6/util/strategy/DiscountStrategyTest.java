package org.fffd.l23o6.util.strategy;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import jakarta.annotation.Resource;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStrategyTest {
    private DiscountStrategy discountStrategy = new DiscountStrategy();

    @Test
    public void below1k(){
        double[] actual = discountStrategy.getDiscountWithPoints(798,100);
        double[] expected = {798*0.1*0.01,798};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from1kTo3k(){
        double[] actual = discountStrategy.getDiscountWithPoints(1598,150);
        double[] expected = {1000*0.1*0.01 + 598*0.15*0.01,1598};
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
        double[] actual = discountStrategy.getDiscountWithPoints(5598,150);
        double[] expected = {1000*0.1*0.01 + 2000*0.15*0.01 + 2598*0.2*0.01,5598};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from10kTo50k(){
        double[] actual = discountStrategy.getDiscountWithPoints(25598,150);
        double[] expected = {1000*0.1*0.01 + 2000*0.15*0.01 + 7000*0.2*0.01 + 15598*0.25*0.01,25598};
        Assertions.assertArrayEquals(expected,actual);
    }

    @Test
    public void from10kTo50kWithLowPrice(){
        double[] actual = discountStrategy.getDiscountWithPoints(49998,100);
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

    @Test
    public void invalidArgument(){
        assertThrows(BizException.class,() ->{
            discountStrategy.getDiscountWithPoints(-1,100);
        });
    }
}