package org.fffd.l23o6.pojo.vo.seat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.fffd.l23o6.pojo.enum_.SeatType;
import lombok.Data;
@Data
public class SaveSeatRequest {
    @Schema(description = "车次id", required = true)
    @NotNull
    private Long trainId;

    @Schema(description = "坐席类型", required = true)
//    @Pattern(regexp = "^(高铁|普通列车)$", message = "车类型目前只能为高铁或普通列车")
    @NotNull
    private SeatType seatType;

    @Schema(description = "保留数量", required = true)
    @NotNull
    private int saveNum;
}
