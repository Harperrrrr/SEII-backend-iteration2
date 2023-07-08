package org.fffd.l23o6.util.strategy.train;

import java.util.*;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import jakarta.annotation.Nullable;
import org.fffd.l23o6.pojo.entity.TrainEntity;


public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();

    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();
    public final Map<String,Integer> SEAT_MAP = new HashMap<>();
    public final Map<KSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
    }};


    private KSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            SEAT_MAP.put(s,counter);
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            SEAT_MAP.put(s,counter);
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            SEAT_MAP.put(s,counter);
            SOFT_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            SEAT_MAP.put(s,counter);
            HARD_SEAT_MAP.put(counter++, s);
        }
    }


    public enum KSeriesSeatType implements SeatType {
        SOFT_SLEEPER_SEAT("软卧", 250), HARD_SLEEPER_SEAT("硬卧", 200), SOFT_SEAT("软座", 150), HARD_SEAT("硬座", 100), NO_SEAT("无座", 50);
        private String text;
        private int moneyPerStation;

        KSeriesSeatType(String text, int money) {
            this.text = text;
            this.moneyPerStation = money;
        }

        public String getText() {
            return this.text;
        }

        public int getMoneyPerStation() {
            return this.moneyPerStation;
        }

        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    /**
     * 1.获取相应坐席类型的map
     * 2.获取该种坐席对应的所有座位序号
     * 3.对于每一个序号，遍历所有站点，看是否被占用
     * 4.若均为被占用，则选中符合条件的座位
     *
     * @param startStationIndex
     * @param endStationIndex
     * @param type
     * @param seatMap
     * @return
     */
    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, KSeriesSeatType type, boolean[][] seatMap) {
        if (type.getText().equals("无座")) {
            return type.getText();
        }
        Map<Integer, String> map = TYPE_MAP.get(type);
        List<Integer> seatCount = new ArrayList<>(map.keySet());
        for (Integer i : seatCount) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i]) {
                    break;
                }
                if (j == endStationIndex - 1) {
                    for (int k = startStationIndex; k < endStationIndex - 1; k++) {
                        seatMap[k][i] = true;
                    }
                    return map.get(i);
                }
            }
        }
        return null;
    }

    /**
     * 1. 获取坐席类型集合types
     * 2. 遍历types，对于每一个type，获取对应的作为序号集合
     * 3. 遍历序号集合，对于每一个序号，遍历起点到终点站
     * 4. 若均未被占用，leftCount++
     * @param startStationIndex
     * @param endStationIndex
     * @param seatMap
     * @return
     */
    public Map<KSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        if(seatMap == null){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS,"座位为空");
        }
        if(startStationIndex >= endStationIndex || startStationIndex> seatMap.length){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS,"起点与终点不合法");
        }
        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> result = new HashMap<>();
        List<KSeriesSeatStrategy.KSeriesSeatType> types = new ArrayList<>(TYPE_MAP.keySet());
        for (KSeriesSeatStrategy.KSeriesSeatType type : types) {
            Map<Integer, String> map = TYPE_MAP.get(type);
            List<Integer> seatCount = new ArrayList<>(map.keySet());
            int leftCount = 0;
            for (Integer i : seatCount) {
                for (int j = startStationIndex; j < endStationIndex; j++) {
                    if (seatMap[j][i]) {
                        break;
                    }
                    if (j == endStationIndex - 1) {
                        leftCount++;
                    }
                }
            }
            result.put(type, leftCount);
        }
        return result;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size()];
    }


    public int getSeatNum(){
        return SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size()
                + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size();
    }

    public int getSoftSleepSeatNum(){
        return SOFT_SLEEPER_SEAT_MAP.size();
    }

    public int getHardSleepSeatNum(){
        return HARD_SLEEPER_SEAT_MAP.size();
    }

    public int getSoftSeatNum(){
        return SOFT_SEAT_MAP.size();
    }

    public int getHardSeatNum(){
        return HARD_SEAT_MAP.size();
    }
}
