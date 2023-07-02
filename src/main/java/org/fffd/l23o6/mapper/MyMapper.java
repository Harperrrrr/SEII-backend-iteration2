package org.fffd.l23o6.mapper;

import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;

import java.util.ArrayList;
import java.util.List;

public class MyMapper {
    public static MyMapper INSTANCE = new MyMapper();

    public TrainVO toTrainVO(TrainEntity TrainEntity) {
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
        if (TrainEntity.getTrainType().getText().equals("高铁")) {
            List<GSeriesSeatStrategy.GSeriesSeatType> types =
                    new ArrayList<>(GSeriesSeatStrategy.INSTANCE.TYPE_MAP.keySet());
            int price = 150;
            for (GSeriesSeatStrategy.GSeriesSeatType type : types) {
                TicketInfo info = new TicketInfo(type.getText(), GSeriesSeatStrategy.INSTANCE.TYPE_MAP.get(type).size(), price);
                infos.add(info);
                price -= 50;
            }

        }else{
            List<KSeriesSeatStrategy.KSeriesSeatType> types =
                    new ArrayList<>(KSeriesSeatStrategy.INSTANCE.TYPE_MAP.keySet());
            int price = 200;
            for (KSeriesSeatStrategy.KSeriesSeatType type : types) {
                TicketInfo info = new TicketInfo(type.getText(), KSeriesSeatStrategy.INSTANCE.TYPE_MAP.get(type).size(), price);
                infos.add(info);
                price -= 50;
            }
        }
        trainVO.ticketInfo(infos);
        return trainVO.build();
    }

}
