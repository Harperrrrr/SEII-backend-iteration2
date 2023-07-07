package org.fffd.l23o6.service;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderDetailVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;

public interface OrderService {
    Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType, Long seatNumber);
    List<OrderDetailVO> listOrders(String username);
    List<OrderDetailVO> listAllOrders();
    OrderVO getOrder(Long id);

    List<OrderVO> listOrdersByTrainID(Long trainID);

    void cancelOrder(Long id);
    void payOrder(Long id, int type);
    void completeOrder(Long id);
}
