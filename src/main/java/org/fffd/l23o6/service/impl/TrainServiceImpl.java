package org.fffd.l23o6.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.mapper.my.MyTrainMapper;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    private final MyTrainMapper myTrainMapper;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        if (train == null){
            throw new BizException(BizError.ILLEGAL_TRAIN_ID);
        }
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {
        // First, get all routes contains [startCity, endCity]
        // Then, Get all trains on that day with the wanted routes
        List<RouteEntity> routes = routeDao.findAll();
        List<Long> routeIds = new ArrayList<>();
        for (RouteEntity route : routes) {
            if (route.getStationIds().contains(startStationId)
                    && route.getStationIds().contains(endStationId)
                    && route.getStationIds().indexOf(startStationId) < route.getStationIds().indexOf(endStationId)) {
                routeIds.add(route.getId());
            }
        }
        List<TrainVO> result = new ArrayList<>();
        List<TrainEntity> trains = trainDao.findAll();
        for (TrainEntity train : trains) {
            if (routeIds.contains(train.getRouteId())) {
                RouteEntity route = routeDao.findById(train.getRouteId()).get();

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date1;
                try {
                    date1 = dateFormat.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                int startIdx = route.getStationIds().indexOf(startStationId);
                Date date2 = train.getArrivalTimes().get(startIdx);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date1);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(date2);
                boolean sameDay = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);

                if (sameDay) {
                    result.add(myTrainMapper.toTrainVO(train, startStationId, endStationId));
                }
            }
        }
        return result;
    }

    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                         List<Date> departureTimes) {
        TrainEntity trainEntity = trainDao.findTrainByName(name);
        if(trainEntity != null){
            throw new BizException(BizError.TRAINID_EXISTS);
        }
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        entity.setNoSeatNum(20);
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                entity.setSaveSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                entity.setSaveSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes) {
        TrainEntity entity = trainDao.findById(id).get();
        if (entity == null) {
            throw new BizException(BizError.ILLEGAL_TRAIN_ID);
        }
        RouteEntity route = routeDao.findById(routeId).get();

        entity.setName(name);
        entity.setRouteId(routeId);
        entity.setTrainType(type);
        entity.setDate(date);
        entity.setArrivalTimes(arrivalTimes);
        entity.setDepartureTimes(departureTimes);

        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }

        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));

        trainDao.save(entity);
    }

    @Override
    public void deleteTrain(Long id) {
        trainDao.deleteById(id);
    }

    @Override
    public void changeTrainStatus(Long trainId, int stationIdx) {
        TrainEntity entity = trainDao.findById(trainId).get();
        List<String> extraInfos = entity.getExtraInfos();
        if (extraInfos.get(stationIdx).equals("预计正点")) {
            extraInfos.set(stationIdx, "预计晚点");
        } else {
            extraInfos.set(stationIdx, "预计正点");
        }
        entity.setExtraInfos(extraInfos);
        trainDao.save(entity);
    }

    @Override
    public void saveSeatsG(Long trainId, int businessSeat, int firstClassSeat, int secondClassSeat) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        System.out.println("here "+businessSeat);
        for (int i = 0; i < businessSeat; i++) {
            String seat =
                    GSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, train.getSeats());
            int count = GSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
                System.out.println("here1 "+count);
            }
        }
        for (int i = 0; i < firstClassSeat; i++) {
            String seat =
                    GSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, train.getSeats());
            int count = GSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
                System.out.println("here2 "+count);

            }
        }
        for (int i = 0; i < secondClassSeat; i++) {
            String seat =
                    GSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, train.getSeats());
            int count = GSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
                System.out.println("here3 "+count);

            }
        }
        train.setUpdatedAt(null);
        trainDao.save(train);
    }

    @Override
    public void saveSeatsK(Long trainId, int softSleepSeat, int hardSleepSeat, int softSeat, int hardSeat) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        for (int i = 0; i < softSleepSeat; i++) {
            String seat =
                    KSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, train.getSeats());
            int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
            }
        }
        for (int i = 0; i < hardSleepSeat; i++) {
            String seat =
                    KSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, train.getSeats());
            int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
            }
        }
        for (int i = 0; i < softSeat; i++) {
            String seat =
                    KSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, train.getSeats());
            int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
            }
        }
        for (int i = 0; i < hardSeat; i++) {
            String seat =
                    KSeriesSeatStrategy.INSTANCE.allocSeat(0,
                            route.getStationIds().size() - 1,
                            KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, train.getSeats());
            int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
            for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                train.seats[j][count] = true;
                train.saveSeats[j][count] = true;
            }
        }
        train.setUpdatedAt(null);
        trainDao.save(train);
    }

    @Override
    public void releaseSeatsG(Long trainId, int businessSeat, int firstClassSeat, int secondClassSeat) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        boolean[][] saveMap = train.getSaveSeats();
        boolean[][] seatMap = train.getSeats();
        Map<GSeriesSeatStrategy.GSeriesSeatType, Map<Integer, String>> typeMap
                = GSeriesSeatStrategy.INSTANCE.TYPE_MAP;

        Map<Integer, String> map1 = typeMap.get(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT);
        for (Integer i : map1.keySet()) {
            if(businessSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                businessSeat--;
            }
        }

        Map<Integer, String> map2 = typeMap.get(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT);
        for (Integer i : map2.keySet()) {
            if(firstClassSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                firstClassSeat--;
            }
        }

        Map<Integer, String> map3 = typeMap.get(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT);
        for (Integer i : map3.keySet()) {
            if(secondClassSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                secondClassSeat--;
            }
        }

        train.setSaveSeats(saveMap);
        train.setSeats(seatMap);
        train.setUpdatedAt(null);
        trainDao.save(train);
    }

    @Override
    public void releaseSeatsK(Long trainId, int softSleepSeat, int hardSleepSeat, int softSeat, int hardSeat) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        boolean[][] saveMap = train.getSaveSeats();
        boolean[][] seatMap = train.getSeats();
        Map<KSeriesSeatStrategy.KSeriesSeatType, Map<Integer, String>> typeMap
                = KSeriesSeatStrategy.INSTANCE.TYPE_MAP;

        Map<Integer, String> map1 = typeMap.get(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT);
        for (Integer i : map1.keySet()) {
            if(softSleepSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                softSleepSeat--;
            }
        }

        Map<Integer, String> map2 = typeMap.get(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT);
        for (Integer i : map2.keySet()) {
            if(hardSleepSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                hardSleepSeat--;
            }
        }

        Map<Integer, String> map3 = typeMap.get(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT);
        for (Integer i : map3.keySet()) {
            if(softSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                softSeat--;
            }
        }

        Map<Integer, String> map4 = typeMap.get(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT);
        for (Integer i : map4.keySet()) {
            if(hardSeat == 0){
                break;
            }
            if (saveMap[0][i]) {
                for (int j = 0; j < route.getStationIds().size() - 1; ++j) {
                    saveMap[j][i] = false;
                    seatMap[j][i] = false;
                }
                hardSeat--;
            }
        }
        train.setSaveSeats(saveMap);
        train.setSeats(seatMap);
        train.setUpdatedAt(null);
        trainDao.save(train);
    }

    @Override
    public int[] getSeatsNumG(Long trainId) {
        int[] result = {0,0,0};
        boolean[][] saveMap = trainDao.findById(trainId).get().getSaveSeats();
        Map<GSeriesSeatStrategy.GSeriesSeatType, Map<Integer, String>> typeMap
                = GSeriesSeatStrategy.INSTANCE.TYPE_MAP;
        List<GSeriesSeatStrategy.GSeriesSeatType> types = new ArrayList<>(typeMap.keySet());
        for(GSeriesSeatStrategy.GSeriesSeatType type:types){
            Map<Integer, String> map = typeMap.get(type);
            List<Integer> seatCount = new ArrayList<>(map.keySet());
            for(Integer i:seatCount){
                if(saveMap[0][i]){
                    switch (type){
                        case BUSINESS_SEAT:
                            result[0]++;
                            break;
                        case FIRST_CLASS_SEAT:
                            result[1]++;
                            break;
                        case SECOND_CLASS_SEAT:
                            result[2]++;
                            break;
                    }
                }
            }
        }
        System.out.println("aa"+result[0]+" "+result[1]+" "+result[2]);
        return result;
    }

    @Override
    public int[] getSeatsNumK(Long trainId) {
        int[] result = {0,0,0,0};
        boolean[][] saveMap = trainDao.findById(trainId).get().getSaveSeats();
        Map<KSeriesSeatStrategy.KSeriesSeatType, Map<Integer, String>> typeMap
                = KSeriesSeatStrategy.INSTANCE.TYPE_MAP;
        List<KSeriesSeatStrategy.KSeriesSeatType> types = new ArrayList<>(typeMap.keySet());
        for(KSeriesSeatStrategy.KSeriesSeatType type:types){
            Map<Integer, String> map = typeMap.get(type);
            List<Integer> seatCount = new ArrayList<>(map.keySet());
            for(Integer i:seatCount){
                if(saveMap[0][i]){
                    switch (type){
                        case SOFT_SLEEPER_SEAT:
                            result[0]++;
                            break;
                        case HARD_SLEEPER_SEAT:
                            result[1]++;
                            break;
                        case SOFT_SEAT:
                            result[2]++;
                            break;
                        case HARD_SEAT:
                            result[3]++;
                            break;
                    }
                }
            }
        }
        return result;
    }
}
