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
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long trainId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        TrainDetailVO trainDetailVO = TrainDetailVO.builder().id(trainId).date("2023-07-08")
                .name("testTrain").build();
        Mockito.when(trainService.getTrain(trainId)).thenReturn(trainDetailVO);
        Mockito.when(trainService.getTrain(1001L)).thenReturn(null);

        CommonResponse<TrainDetailVO> response = trainController.getTrain(trainId);
        Assertions.assertEquals(trainDetailVO,response.getData());

        response = trainController.getTrain(1001L);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testListTrains(){
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long trainId1 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long trainId2 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long trainId3 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        TrainVO trainVO1 = TrainVO.builder().id(trainId1).name("testTrain1").build();
        TrainVO trainVO2 = TrainVO.builder().id(trainId2).name("testTrain2").build();
        TrainVO trainVO3 = TrainVO.builder().id(trainId3).name("testTrain3").build();
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

        long nullId = 1001L;
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
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long routeId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        AddTrainRequest request = new AddTrainRequest();
        request.setTrainType(TrainType.HIGH_SPEED);
        request.setName("testTrain");
        request.setRouteId(routeId);

        Assertions.assertEquals(200,trainController
                .addTrain(request).getHttpCode());

        Mockito.verify(trainService, Mockito.times(1)).
                addTrain(request.getName(),request.getRouteId(),request.getTrainType(),
                        request.getDate(),request.getArrivalTimes(),request.getDepartureTimes());
    }
}
