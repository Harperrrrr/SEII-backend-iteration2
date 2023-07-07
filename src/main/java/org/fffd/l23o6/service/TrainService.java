package org.fffd.l23o6.service;

import java.util.Date;
import java.util.List;

import org.fffd.l23o6.pojo.enum_.SeatType;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.springframework.web.bind.annotation.RequestParam;

public interface TrainService {
    public TrainDetailVO getTrain(Long trainId);

    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date);
    /**
     * input :
     * output : List<AdminTrainVO>
     * Managers find all the detailed Train Information
     */
    public List<AdminTrainVO> listTrainsAdmin();

    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                         List<Date> departureTimes);

    public void changeTrain(Long trainId, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes);

    public void deleteTrain(Long trainId);

    public void changeTrainStatus(Long trainId,int stationIdx);

    public void saveSeatsG(Long trainId, int businessSeat, int firstClassSeat, int secondClassSeat);
    public void saveSeatsK(Long trainId,int softSleepSeat,int hardSleepSeat,int softSeat,int hardSeat);

    public void releaseSeatsG(Long trainId, int businessSeat, int firstClassSeat, int secondClassSeat);
    public void releaseSeatsK(Long trainId,int softSleepSeat,int hardSleepSeat,int softSeat,int hardSeat);

    public int[] getSeatsNumG(Long trainId);
    public int[] getSeatsNumK(Long trainId);
}
