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

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RouteIntegrationTest {
    private RouteService routeService = Mockito.mock(RouteService.class);

    private RouteController routeController = new RouteController(routeService);

    @Test
    public void testGetRoute() {
        long routeId = 1L;
        RouteVO routeVO = new RouteVO();
        routeVO.setId(routeId);
        Mockito.when(routeService.getRoute(routeId)).thenReturn(routeVO);
        Mockito.when(routeService.getRoute(2L)).thenReturn(null);

        CommonResponse<RouteVO> response = routeController.getRoute(routeId);
        Assertions.assertEquals(routeVO,response.getData());

        response = routeController.getRoute(2L);
        Assertions.assertEquals(null,response.getData());
    }

    @Test
    public void testGetRoutes(){
        RouteVO routeVO1 = new RouteVO();
        routeVO1.setId(1L);
        RouteVO routeVO2 = new RouteVO();
        routeVO2.setId(2L);
        RouteVO routeVO3 = new RouteVO();
        routeVO3.setId(3L);

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
        routeController.editRoute(1L,request);

        Mockito.verify(routeService, Mockito.times(1)).
                editRoute(1L,request.getName(),request.getStationIds());
    }
}
