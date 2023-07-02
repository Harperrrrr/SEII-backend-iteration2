package org.fffd.l23o6.mapper;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyMapper {
    private final RouteDao routeDao;

    public TrainVO toTrainVO(TrainEntity TrainEntity,Long startStationId,Long endStationId) {
        if (TrainEntity == null) {
            return null;
        }

        TrainVO.TrainVOBuilder trainVO = TrainVO.builder();

        trainVO.id(TrainEntity.getId());
        trainVO.name(TrainEntity.getName());
        if (TrainEntity.getTrainType() != null) {
            trainVO.trainType(TrainEntity.getTrainType().name());
        }

        List<TicketInfo> infos = new ArrayList<>();

        RouteEntity route = routeDao.findById(TrainEntity.getRouteId()).get();
        int startIdx = route.getStationIds().indexOf(startStationId);
        int endIdx = route.getStationIds().indexOf(endStationId);
        int miles = endIdx - startIdx;

        if (TrainEntity.getTrainType().getText().equals("高铁")) {
            List<GSeriesSeatStrategy.GSeriesSeatType> types =
                    new ArrayList<>(GSeriesSeatStrategy.INSTANCE.TYPE_MAP.keySet());
            Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> map
                    = GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startIdx,endIdx,TrainEntity.getSeats());
            for (GSeriesSeatStrategy.GSeriesSeatType type : types) {
                TicketInfo info = new TicketInfo(type.getText(), map.get(type), miles*type.getMoneyPerStation());
                infos.add(info);
            }
            TicketInfo info = new TicketInfo("无座", TrainEntity.noSeatNum, 50*miles);
            infos.add(info);
        }else{
            List<KSeriesSeatStrategy.KSeriesSeatType> types =
                    new ArrayList<>(KSeriesSeatStrategy.INSTANCE.TYPE_MAP.keySet());
            Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> map
                    = KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startIdx,endIdx,TrainEntity.getSeats());
            for (KSeriesSeatStrategy.KSeriesSeatType type : types) {
                TicketInfo info = new TicketInfo(type.getText(), map.get(type), miles* type.getMoneyPerStation());
                infos.add(info);
            }
            TicketInfo info = new TicketInfo("无座", TrainEntity.noSeatNum, 50*miles);
            infos.add(info);
        }
        trainVO.ticketInfo(infos);

        trainVO.startStationId(startStationId);
        trainVO.endStationId(endStationId);
        trainVO.departureTime(TrainEntity.getDepartureTimes().get(startIdx));
        trainVO.arrivalTime(TrainEntity.getArrivalTimes().get(endIdx));

        return trainVO.build();
    }

}
