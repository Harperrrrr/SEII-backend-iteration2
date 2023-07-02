package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.DiscountStrategy;
import org.fffd.l23o6.util.strategy.payment.AlipayPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.WeChatPaymentStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    private final Integer moneyPerStation = 50;

    private final Integer mileAgePointsPerStation = 200;

    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
                            Long seatNumber) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        int miles = endStationIndex - startStationIndex;
        String seat = null;
        int originalPrice = moneyPerStation * miles;
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                originalPrice *= 1.5;
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }

        UserEntity user = userDao.findByUsername(username);
        double result[] = DiscountStrategy.INSTANCE.getDiscountWithPoints(user.getMileagePoints(),originalPrice);

        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat).originalPrice(originalPrice)
                .caculatedPrice(originalPrice - result[0])
                .consumeMileagePoints(result[1])
                .generateMileagePoints(miles * mileAgePointsPerStation)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: refund user's money and credits if needed
        if (order.getStatus() == OrderStatus.PAID) {
            // refund credits
            UserEntity user = userDao.findById(order.getUserId()).get();
            user.setMileagePoints((int)(user.getMileagePoints() + order.getConsumeMileagePoints()));
            userDao.save(user);

            // refund money
            PaymentType type = order.getPaymentType();
            switch (type){
                case Alipay:
                    AlipayPaymentStrategy.INSTANCE.refund(order.getCaculatedPrice());
                    break;
                case WeChat:
                    WeChatPaymentStrategy.INSTANCE.refund(order.getCaculatedPrice());
                    break;
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderDao.save(order);
    }

    public void payOrder(Long id, PaymentType type) {
        OrderEntity order = orderDao.findById(id).get();

        order.setPaymentType(type);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        UserEntity user = userDao.findById(order.getUserId()).get();

        //  use payment strategy to pay!
        switch (type) {
            case Alipay:
                AlipayPaymentStrategy.INSTANCE.pay(order.getCaculatedPrice());
                break;
            case WeChat:
                WeChatPaymentStrategy.INSTANCE.pay(order.getCaculatedPrice());
                break;
        }
        user.setMileagePoints((int) (user.getMileagePoints() - order.getConsumeMileagePoints()));

        // update user's credits, so that user can get discount next time
        user.setMileagePoints((int) (user.getMileagePoints() + order.getGenerateMileagePoints()));
        userDao.save(user);

        order.setStatus(OrderStatus.COMPLETED);
        orderDao.save(order);
    }

}
