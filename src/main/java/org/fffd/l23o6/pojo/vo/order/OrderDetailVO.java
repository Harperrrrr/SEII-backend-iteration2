package org.fffd.l23o6.pojo.vo.order;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Builder
@Data
public class OrderDetailVO {
    private Long id;
    private String name;
    private String idn;
    private Long trainId;
    private double originalPrice;
    private double caculatedPrice;
    private Long startStationId;
    private Long endStationId;
    private Date departureTime;
    private Date arrivalTime;
    private int consumeMileagePoints;
    private String status;
    private Date createdAt;
    private String seat;
}
