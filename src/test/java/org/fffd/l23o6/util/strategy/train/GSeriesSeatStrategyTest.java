package org.fffd.l23o6.util.strategy.train;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class GSeriesSeatStrategyTest {
    public int stationNum = 5;

    @Test
    public void leftSeatCount1() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(0, 4, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, GSeriesSeatStrategy.INSTANCE.getBusinessSeatNum());
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getFirstSeatNum());
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getSecondSeatNum());
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount2() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 0; i < stationNum; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
                map[i][j] = true;
            }
        }
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(2, 3, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, 0);
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, 0);
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, 0);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount3() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(2, 3, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, GSeriesSeatStrategy.INSTANCE.getBusinessSeatNum()/2);
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getFirstSeatNum()/2);
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getSecondSeatNum()/2 + 1);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount4() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 2, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, GSeriesSeatStrategy.INSTANCE.getBusinessSeatNum());
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getFirstSeatNum());
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getSecondSeatNum());
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount5() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, GSeriesSeatStrategy.INSTANCE.getBusinessSeatNum()/2);
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getFirstSeatNum()/2);
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getSecondSeatNum()/2 + 1);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount6() {
        boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        map[2][5] = true;
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> actual =
                GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, map);
        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, GSeriesSeatStrategy.INSTANCE.getBusinessSeatNum()/2);
            put(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getFirstSeatNum()/2 - 1);
            put(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, GSeriesSeatStrategy.INSTANCE.getSecondSeatNum()/2 + 1);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount7() {
        assertThrows(BizException.class,() ->{
            boolean[][] map = new boolean[stationNum][GSeriesSeatStrategy.INSTANCE.getSeatNum()];
            GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(4, 4, map);
        });
    }

    @Test
    public void leftSeatCount8() {
        assertThrows(BizException.class,() ->{
            GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, null);
        });
    }
}