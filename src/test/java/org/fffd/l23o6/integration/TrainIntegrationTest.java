package org.fffd.l23o6.integration;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.controller.TrainController;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;



@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TrainIntegrationTest {
    private TrainService trainService = Mockito.mock(TrainService.class);

    private TrainController trainController = new TrainController(trainService);


    @Test
    public void testGetTrain() {
        long trainId = 1L;
        TrainDetailVO trainDetailVO = TrainDetailVO.builder().id(trainId).date("2023-07-08")
                .name("testTrain").build();
        Mockito.when(trainService.getTrain(trainId)).thenReturn(trainDetailVO);
        Mockito.when(trainService.getTrain(2L)).thenReturn(null);

        CommonResponse<TrainDetailVO> response = trainController.getTrain(trainId);
        Assertions.assertEquals(trainDetailVO,response.getData());

        response = trainController.getTrain(2L);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testListTrains(){
        TrainVO trainVO1 = TrainVO.builder().id(1L).name("testTrain1").build();
        TrainVO trainVO2 = TrainVO.builder().id(2L).name("testTrain2").build();
        TrainVO trainVO3 = TrainVO.builder().id(3L).name("testTrain3").build();
        List<TrainVO> trainVOList = new ArrayList<>();
        trainVOList.add(trainVO1);
        trainVOList.add(trainVO2);
        trainVOList.add(trainVO3);

        long startId = 1L;
        long endId = 2L;
        String date = "2023-07-08";
        Mockito.when(trainService.listTrains(startId,endId,date)).thenReturn(trainVOList);

        ListTrainRequest request = new ListTrainRequest();
        request.setStartStationId(startId);
        request.setEndStationId(endId);
        request.setDate(date);
        CommonResponse<List<TrainVO>> response = trainController.listTrains(request);
        Assertions.assertEquals(trainVOList,response.getData());

        long nullId = 3L;
        date = "";
        Mockito.when(trainService.listTrains(nullId,nullId,date)).thenReturn(null);

        request.setStartStationId(nullId);
        request.setEndStationId(nullId);
        request.setDate(date);
        response = trainController.listTrains(request);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testAddTrain(){
        AddTrainRequest request = new AddTrainRequest();
        request.setTrainType(TrainType.HIGH_SPEED);
        request.setName("testTrain");
        request.setRouteId(1L);
        trainController.addTrain(request);

        Mockito.verify(trainService, Mockito.times(1)).
                addTrain(request.getName(),request.getRouteId(),request.getTrainType(),
                        request.getDate(),request.getArrivalTimes(),request.getDepartureTimes());
    }
}
