package org.fffd.l23o6.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.mapper.*;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderDetailVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.user.UserVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.DiscountStrategy;
import org.fffd.l23o6.util.strategy.payment.AlipayPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.WeChatPaymentStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.data.domain.Sort;
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

    private Integer moneyPerStation = 50;

    private final Integer mileAgePointsPer = 1;

    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
                            Long seatNumber) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        UserEntity user = userDao.findByUsername(username);
        Long userId = user.getId();

        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        int miles = endStationIndex - startStationIndex;

        switch (seatType) {
            case "软卧":
                moneyPerStation = 250;
                break;
            case "硬卧":
                moneyPerStation = 200;
                break;
            case "软座":
                moneyPerStation = 150;
                break;
            case "硬座":
                moneyPerStation = 100;
                break;
            case "商务座":
                moneyPerStation = 200;
                break;
            case "一等座":
                moneyPerStation = 150;
                break;
            case "二等座":
                moneyPerStation = 100;
                break;
            case "无座":
                moneyPerStation = 50;
                break;
        }
        String seat = null;
        int originalPrice = moneyPerStation * miles;
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                if (seatType.equals("无座")) {
                    train.noSeatNum--;
                } else {
                    int count = GSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
                    for (int i = startStationIndex; i < endStationIndex; ++i) {
                        train.seats[i][count] = true;
                    }
                }
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                if (seatType.equals("无座")) {
                    train.noSeatNum--;
                } else {
                    int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
                    for (int i = startStationIndex; i < endStationIndex; ++i) {
                        train.seats[i][count] = true;
                    }
                }
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }

        double result[] = DiscountStrategy.INSTANCE.getDiscountWithPoints(user.getMileagePoints(), originalPrice);

        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat).originalPrice(originalPrice)
                .caculatedPrice(originalPrice - result[0])
                .consumeMileagePoints((int) result[1])
                .generateMileagePoints((int) (originalPrice - result[0]) * mileAgePointsPer)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        //user.setMileagePoints(user.getMileagePoints() + order.getGenerateMileagePoints());
        userDao.save(user);
        return order.getId();
    }

    public List<OrderDetailVO> listOrders(String username) {
        UserEntity user = userDao.findByUsername(username);
        List<OrderEntity> orders = orderDao.findByUserId(user.getId());
        List<OrderDetailVO> orderDetailVOS = new ArrayList<>();
        for(OrderEntity order: orders){
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            orderDetailVOS.add(OrderDetailVO.builder()
                    .id(order.getId())
                    .name(user.getName())
                    .idn(user.getIdn())
                    .trainId(order.getTrainId())
                    .originalPrice(order.getOriginalPrice())
                    .caculatedPrice(order.getCaculatedPrice())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .consumeMileagePoints(order.getConsumeMileagePoints())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .build());
        }
        return orderDetailVOS;
    }

    @Override
    public List<OrderDetailVO> listAllOrders() {
        List<UserVO> users = userDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(UserMapper.INSTANCE::toUserVO).collect(Collectors.toList());
        List<OrderDetailVO> orderDetailVOS = new ArrayList<>();
        for (UserVO user: users){
            List<OrderDetailVO> orders = listOrders(user.getUsername());
            orderDetailVOS.addAll(orders);
        }
        return orderDetailVOS;
    }

    public OrderDetailVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderDetailVO.builder().id(order.getId())
                .name(user.getName())
                .idn(user.getIdn())
                .trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .originalPrice(order.getOriginalPrice())
                .consumeMileagePoints(order.getConsumeMileagePoints())
                .caculatedPrice(order.getCaculatedPrice())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .build();
    }


    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        String seat = order.getSeat();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endStationIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        if(!seat.equals("无座")){
            if(train.getTrainType().getText().equals("高铁")){
                int count = GSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
                for (int i = startStationIndex; i < endStationIndex; ++i) {
                    train.seats[i][count] = false;
                }
            }else{
                int count = KSeriesSeatStrategy.INSTANCE.SEAT_MAP.get(seat);
                for (int i = startStationIndex; i < endStationIndex; ++i) {
                    train.seats[i][count] = false;
                }
            }
        }

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        if (order.getStatus() == OrderStatus.PAID) {
            // refund credits
            UserEntity user = userDao.findById(order.getUserId()).get();
            user.setMileagePoints((int) (user.getMileagePoints() + order.getConsumeMileagePoints()));
            userDao.save(user);

            // refund money
            PaymentType type = order.getPaymentType();
            PaymentStrategy strategy = null;

            switch (type) {
                case Alipay:
                    strategy = AlipayPaymentStrategy.INSTANCE;
                    //AlipayPaymentStrategy.INSTANCE.refund(order.getCaculatedPrice());
                    break;
                case WeChat:
                    strategy = WeChatPaymentStrategy.INSTANCE;
                    //WeChatPaymentStrategy.INSTANCE.refund(order.getCaculatedPrice());
                    break;
            }
            strategy.refund(order.getCaculatedPrice());
        }
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);

        order.setStatus(OrderStatus.CANCELLED);
        orderDao.save(order);
    }


    @Override
    public void payOrder(Long id, int type) {
        OrderEntity order = orderDao.findById(id).get();
        PaymentStrategy strategy = null;
        PaymentType pt = type == 0 ? PaymentType.Alipay : PaymentType.WeChat;
        order.setPaymentType(pt);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        UserEntity user = userDao.findById(order.getUserId()).get();

        //  use payment strategy to pay!
        switch (pt) {
            case Alipay:
                strategy = AlipayPaymentStrategy.INSTANCE;
                //AlipayPaymentStrategy.INSTANCE.pay(order.getCaculatedPrice());
                break;
            case WeChat:
                strategy = WeChatPaymentStrategy.INSTANCE;
                //WeChatPaymentStrategy.INSTANCE.pay(order.getCaculatedPrice());
                break;
        }

        strategy.pay(order.getCaculatedPrice());

        user.setMileagePoints((int) (user.getMileagePoints() - order.getConsumeMileagePoints()));

        // update user's credits, so that user can get discount next time
        user.setMileagePoints((int) (user.getMileagePoints() + order.getGenerateMileagePoints()));

        userDao.save(user);

        order.setStatus(OrderStatus.PAID);
        orderDao.save(order);
    }

    @Override
    public void completeOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        if (order.getStatus() != OrderStatus.PAID){
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderDao.save(order);
    }
}
