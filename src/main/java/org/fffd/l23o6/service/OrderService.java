package org.fffd.l23o6.service;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderDetailVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;

public interface OrderService {
    Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType, Long seatNumber);
    /**
     * input : username
     * output : List<OrderDetailVO>
     * find all the orders the user has through username
     */
    List<OrderDetailVO> listOrders(String username);
    /**
     * input :
     * output : List<OrderDetailVO>
     * Managers find all the orders
     */
    List<OrderDetailVO> listAllOrders();
    OrderDetailVO getOrder(Long id);


    void cancelOrder(Long id);
    /**
     * input : id, type
     * output :
     * orderID + paymentType => paymentStrategy
     */
    void payOrder(Long id, int type);
    void completeOrder(Long id);
}
