package org.fffd.l23o6.controller;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.*;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse.success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(), request.getStartStationId(), request.getEndStationId(), request.getSeatType(), null)));
    }

    @GetMapping("order")
    public CommonResponse<List<OrderDetailVO>> listOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    @GetMapping("allOrder")
    public CommonResponse<List<OrderDetailVO>> listAllOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listAllOrders());
    }

    @GetMapping("order/{orderId}")
    public CommonResponse<OrderDetailVO> getOrder(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

//    @PatchMapping("order/{orderId}")
//    public CommonResponse<?> patchOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody PatchOrderRequest request) {
//
//        switch (request.getStatus()) {
//            case PAID:
//                orderService.payOrder(orderId);
//                break;
//            case CANCELLED:
//                orderService.cancelOrder(orderId);
//                break;
//            default:
//                throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "Invalid order status.");
//        }
//
//        return CommonResponse.success();
//    }

    @PostMapping("order/{orderId}")
    public CommonResponse<?> payOrder(@PathVariable("orderId") Long orderId,@RequestParam("type") int type) {
        orderService.payOrder(orderId,type);
        return CommonResponse.success();
    }

    @PostMapping("completeOrder/{orderId}")
    public CommonResponse<?> completeOrder(@PathVariable("orderId") Long orderId) {
        orderService.completeOrder(orderId);
        return CommonResponse.success();
    }


    @PatchMapping("order/{orderId}")
    public CommonResponse<?> cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return CommonResponse.success();
    }
}