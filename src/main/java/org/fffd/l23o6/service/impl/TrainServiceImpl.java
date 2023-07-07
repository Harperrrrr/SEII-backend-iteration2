package org.fffd.l23o6.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.MyTrainMapper;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.SeatType;
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

                if(sameDay){
                    result.add(myTrainMapper.toTrainVO(train,startStationId,endStationId));
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
                entity.setSaveSeats(entity.getSeats());
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                entity.setSaveSeats(entity.getSeats());
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes) {
        TrainEntity entity = trainDao.findById(id).get();
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
    public void changeTrainStatus(Long trainId,int stationIdx) {
        TrainEntity entity = trainDao.findById(trainId).get();
        List<String> extraInfos = entity.getExtraInfos();
        if(extraInfos.get(stationIdx).equals("预计正点")){
            extraInfos.set(stationIdx,"预计晚点");
        }else{
            extraInfos.set(stationIdx,"预计正点");
        }
        entity.setExtraInfos(extraInfos);
        trainDao.save(entity);
    }

    @Override
    public void saveSeats(Long trainId, SeatType seatType, int saveNum) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        if(train.getTrainType().getText().equals("高铁")){
//            GSeriesSeatStrategy.INSTANCE.allocSeat(0,route.getStationIds().size()-1,)
        }
    }
}
