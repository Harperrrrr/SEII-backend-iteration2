package org.fffd.l23o6.exception;

import io.github.lyc8503.spring.starter.incantation.exception.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizError implements ErrorType {

    USERNAME_EXISTS(200001, "用户名已存在", 400),
    INVALID_CREDENTIAL(200002, "用户名或密码错误", 400),
    STATIONNAME_EXISTS(200003, "同名站点已存在", 400),
    INVALID_IDENTITY(200004, "用户身份不匹配", 400),
    TRAINID_EXISTS(20005,"同id车次已存在", 400),
    OUT_OF_SEAT(300001, "无可用座位", 400),
    ILLEAGAL_ORDER_STATUS(400001, "非法的订单状态", 400),

    ILLEGAL_STATION_ID(500001,"不存在的车站",400),
    ILLEGAL_TRAIN_ID(500002,"不存在的车次",400);

    final int code;
    final String message;
    final int httpCode;
}
