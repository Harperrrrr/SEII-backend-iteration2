package org.fffd.l23o6.integration;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.controller.RouteController;
import org.fffd.l23o6.controller.TrainController;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.RouteService;
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
public class RouteIntegrationTest {
    private RouteService routeService = Mockito.mock(RouteService.class);

    private RouteController routeController = new RouteController(routeService);

    @Test
    public void testGetRoute() {
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long routeId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        RouteVO routeVO = new RouteVO();
        routeVO.setId(routeId);
        Mockito.when(routeService.getRoute(routeId)).thenReturn(routeVO);
        Mockito.when(routeService.getRoute(1001L)).thenReturn(null);

        CommonResponse<RouteVO> response = routeController.getRoute(routeId);
        Assertions.assertEquals(routeVO,response.getData());

        response = routeController.getRoute(1001L);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testGetRoutes(){
        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long routeId1 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long routeId2 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        long routeId3 = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        RouteVO routeVO1 = new RouteVO();
        routeVO1.setId(routeId1);
        RouteVO routeVO2 = new RouteVO();
        routeVO2.setId(routeId2);
        RouteVO routeVO3 = new RouteVO();
        routeVO3.setId(routeId3);

        List<RouteVO> routeVOList = new ArrayList<>();
        routeVOList.add(routeVO1);
        routeVOList.add(routeVO2);
        routeVOList.add(routeVO3);

        Mockito.when(routeService.listRoutes()).thenReturn(routeVOList);

        CommonResponse<List<RouteVO>> response = routeController.getRoutes();
        Assertions.assertEquals(routeVOList,response.getData());
    }

    @Test
    public void testEditRoute(){
        AddRouteRequest request = new AddRouteRequest();
        request.setName("testRoute");

        Random random = new Random(System.currentTimeMillis());
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long routeId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));


        Assertions.assertEquals(200,routeController
                .editRoute(routeId,request).getHttpCode());

        Mockito.verify(routeService, Mockito.times(1)).
                editRoute(routeId,request.getName(),request.getStationIds());
    }
}
