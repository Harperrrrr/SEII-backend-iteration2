package org.fffd.l23o6.integration;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.controller.OrderController;
import org.fffd.l23o6.controller.RouteController;
import org.fffd.l23o6.controller.TrainController;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.OrderService;
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
public class OrderIntegrationTest {
    private OrderService orderService = Mockito.mock(OrderService.class);

    private OrderController orderController = new OrderController(orderService);

    @Test
    public void testGetOrder() {
        long orderId = 1L;
        OrderVO.OrderVOBuilder orderVO = OrderVO.builder();
        orderVO.id(orderId);
        Mockito.when(orderService.getOrder(orderId)).thenReturn(orderVO.build());
        Mockito.when(orderService.getOrder(2L)).thenReturn(null);

        CommonResponse<OrderVO> response = orderController.getOrder(orderId);
        Assertions.assertEquals(orderVO.build(),response.getData());

        response = orderController.getOrder(2L);
        Assertions.assertEquals(null,response.getData());
    }


    @Test
    public void testCancelOrder(){
        Assertions.assertEquals(200,orderController
                .cancelOrder(1L).getHttpCode());

        Mockito.verify(orderService, Mockito.times(1)).
                cancelOrder(1L);
    }
}
