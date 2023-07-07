package org.fffd.l23o6.util.strategy.train;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class KSeriesSeatStrategyTest {
    public int stationNum = 5;

    @Test
    public void leftSeatCount1() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(0, 4, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSleepSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSleepSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSeatNum());
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount2() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
                map[i][j] = true;
            }
        }
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(0, 4, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, 0);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, 0);
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, 0);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, 0);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount3() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(2, 3, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSleepSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSleepSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSeatNum()/2);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount4() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 2, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSleepSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSleepSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSeatNum());
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSeatNum());
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount5() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSleepSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSleepSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSeatNum()/2);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount6() {
        boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
        for (int i = 2; i < 4; ++i) {
            for (int j = 0; j < map[0].length; j += 2) {
                map[i][j] = true;
            }
        }
        map[2][5] = true;
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> actual =
                KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, map);
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> expected = new HashMap<>() {{
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSleepSeatNum()/2 - 1);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSleepSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, KSeriesSeatStrategy.INSTANCE.getSoftSeatNum()/2);
            put(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, KSeriesSeatStrategy.INSTANCE.getHardSeatNum()/2);
        }};
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leftSeatCount7() {
        assertThrows(BizException.class,() ->{
            boolean[][] map = new boolean[stationNum][KSeriesSeatStrategy.INSTANCE.getSeatNum()];
            KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(4, 4, map);
        });
    }

    @Test
    public void leftSeatCount8() {
        assertThrows(BizException.class,() ->{
            KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(1, 4, null);
        });
    }
}