package org.fffd.l23o6.util.strategy.train;

import java.util.*;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import jakarta.annotation.Nullable;
import org.fffd.l23o6.pojo.entity.TrainEntity;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();

    private final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();

    public final Map<String,Integer> SEAT_MAP = new HashMap<>();
    public final Map<GSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
    }};

    private GSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("1车1A", "1车1C", "1车1F")) {
            SEAT_MAP.put(s,counter);
            BUSINESS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("2车1A", "2车1C", "2车1D", "2车1F", "2车2A", "2车2C", "2车2D", "2车2F", "3车1A", "3车1C", "3车1D", "3车1F")) {
            SEAT_MAP.put(s,counter);
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("4车1A", "4车1B", "4车1C", "4车1D", "4车1F", "4车2A", "4车2B", "4车2C", "4车2D", "4车2F", "4车3A", "4车3B", "4车3C", "4车3D", "4车3F")) {
            SEAT_MAP.put(s,counter);
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }

    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座",200), FIRST_CLASS_SEAT("一等座",150), SECOND_CLASS_SEAT("二等座",100), NO_SEAT("无座",50);
        private String text;

        private int moneyPerStation;

        GSeriesSeatType(String text,int money) {
            this.text = text;
            this.moneyPerStation = money;
        }

        public String getText() {
            return this.text;
        }

        public int getMoneyPerStation(){
            return this.moneyPerStation;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
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
    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, GSeriesSeatType type, boolean[][] seatMap) {
        if(type.getText().equals("无座")){
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
    public Map<GSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        if(seatMap == null){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS,"座位为空");
        }
        if(startStationIndex >= endStationIndex || startStationIndex> seatMap.length){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS,"起点与终点不合法");
        }
        Map<GSeriesSeatType, Integer> result = new HashMap<>();
        List<GSeriesSeatType> types = new ArrayList<>(TYPE_MAP.keySet());
        for (GSeriesSeatType type : types) {
            Map<Integer, String> map = TYPE_MAP.get(type);
            List<Integer> seatCount = new ArrayList<>(map.keySet());
            int leftCount = 0;
            for (Integer i : seatCount) {
                for (int j = startStationIndex; j < endStationIndex; j++) {
                    if (seatMap[j][i]) {
                        break;
                    }
                    if(j == endStationIndex - 1){
                        leftCount++;
                    }
                }
            }
            result.put(type, leftCount);
        }
        return result;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size()];
    }

    public int getSeatNum(){
        return BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size();
    }

    public int getBusinessSeatNum(){
        return BUSINESS_SEAT_MAP.size();
    }

    public int getFirstSeatNum(){
        return FIRST_CLASS_SEAT_MAP.size();
    }

    public int getSecondSeatNum(){
        return SECOND_CLASS_SEAT_MAP.size();
    }

}
