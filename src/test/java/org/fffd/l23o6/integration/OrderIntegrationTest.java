package org.fffd.l23o6.integration;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.controller.OrderController;
import org.fffd.l23o6.controller.RouteController;
import org.fffd.l23o6.controller.TrainController;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.order.OrderDetailVO;
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
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest //启动完整的应用程序上下文
@AutoConfigureMockMvc
public class OrderIntegrationTest {
    /**
     * 设置环境
     * 输入
     * 比较输出
     * 给出结论
     *
     */
    private OrderService orderService = Mockito.mock(OrderService.class);

    private OrderController orderController = new OrderController(orderService);

    @Test
    public void testGetOrder() {
        Random random = new Random();
        long lowerBound = 1L; // 下限（包括）
        long upperBound = 1000L; // 上限（不包括）
        long orderId = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));

        OrderDetailVO.OrderDetailVOBuilder orderVO = OrderDetailVO.builder();
        orderVO.id(orderId);
        Mockito.when(orderService.getOrder(orderId)).thenReturn(orderVO.build());
        Mockito.when(orderService.getOrder(1001L)).thenReturn(null);

        CommonResponse<OrderDetailVO> response = orderController.getOrder(orderId);
        Assertions.assertEquals(orderVO.build(),response.getData());

        response = orderController.getOrder(1001L);
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
