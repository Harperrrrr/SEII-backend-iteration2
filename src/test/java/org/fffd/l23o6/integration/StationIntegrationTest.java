package org.fffd.l23o6.integration;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.controller.RouteController;
import org.fffd.l23o6.controller.StationController;
import org.fffd.l23o6.controller.TrainController;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.pojo.vo.station.AddStationRequest;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.RouteService;
import org.fffd.l23o6.service.StationService;
import org.fffd.l23o6.service.TrainService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StationIntegrationTest {
    private StationService stationService = Mockito.mock(StationService.class);

    private StationController stationController = new StationController(stationService);

    @Test
    public void testGetStation() {
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long stationId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        String name = "testStation";
        StationVO stationVO = new StationVO(stationId,name);
        Mockito.when(stationService.getStation(stationId)).thenReturn(stationVO);
        Mockito.when(stationService.getStation(1001L)).thenReturn(null);

        CommonResponse<StationVO> response = stationController.getStation(stationId);
        Assertions.assertEquals(stationVO,response.getData());

        response = stationController.getStation(1001L);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testListStations(){
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long stationId1 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long stationId2 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long stationId3 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        StationVO stationVO1 = new StationVO(stationId1,"testStation1");
        StationVO stationVO2 = new StationVO(stationId2,"testStation2");
        StationVO stationVO3 = new StationVO(stationId3,"testStation3");

        List<StationVO> stationVOList = new ArrayList<>();
        stationVOList.add(stationVO1);
        stationVOList.add(stationVO2);
        stationVOList.add(stationVO3);

        Mockito.when(stationService.listStations()).thenReturn(stationVOList);

        CommonResponse<List<StationVO>> response = stationController.listStations();
        Assertions.assertEquals(stationVOList,response.getData());
    }

    @Test
    public void testEditStation(){
        AddStationRequest request = new AddStationRequest();
        request.setName("testStation");

        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long stationId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        Assertions.assertEquals(200,stationController
                .editStation(stationId,request).getHttpCode());

        Mockito.verify(stationService, Mockito.times(1)).
                editStation(stationId,request.getName());
    }
}
