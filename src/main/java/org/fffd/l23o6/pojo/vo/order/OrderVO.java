package org.fffd.l23o6.pojo.vo.order;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderVO {
    private Long id;
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
