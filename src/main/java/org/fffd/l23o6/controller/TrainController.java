package org.fffd.l23o6.controller;

import java.util.List;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;

import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.seat.SaveSeatRequest;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@AllArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("train")
    public CommonResponse<List<TrainVO>> listTrains(@Valid ListTrainRequest request) {
        return CommonResponse.success(trainService.listTrains(request.getStartStationId(), request.getEndStationId(), request.getDate()));
    }

    @GetMapping("train/{trainId}")
    public CommonResponse<TrainDetailVO> getTrain(@PathVariable Long trainId) {
        return CommonResponse.success(trainService.getTrain(trainId));
    }

    @PostMapping("admin/train")
    public CommonResponse<?> addTrain(@Valid @RequestBody AddTrainRequest request) {
        trainService.addTrain(request.getName(), request.getRouteId(), request.getTrainType(), request.getDate(), request.getArrivalTimes(), request.getDepartureTimes());
        return CommonResponse.success();
    }

    @GetMapping("admin/train")
    public CommonResponse<List<AdminTrainVO>> listTrainsAdmin() {
        return CommonResponse.success(trainService.listTrainsAdmin());
    }

    @GetMapping("admin/train/{trainId}")
    public CommonResponse<AdminTrainVO> getTrainAdmin(@PathVariable Long trainId) {
        return CommonResponse.success();
    }

    @PutMapping("admin/train/{trainId}")
    public CommonResponse<?> changeTrain(@PathVariable Long trainId, @Valid @RequestBody AddTrainRequest request) {
        trainService.changeTrain(trainId, request.getName(), request.getRouteId(), request.getTrainType(),
                request.getDate(), request.getArrivalTimes(), request.getDepartureTimes());
        return CommonResponse.success();
    }

    @DeleteMapping("admin/train/{trainId}")
    public CommonResponse<?> deleteTrain(@PathVariable Long trainId) {
        trainService.deleteTrain(trainId);
        return CommonResponse.success();
    }

    @PutMapping("admin/train/change/{trainId}")
    public CommonResponse<?> changeTrainStatus(@PathVariable Long trainId, @RequestParam int stationIdx) {
        trainService.changeTrainStatus(trainId, stationIdx);
        return CommonResponse.success();
    }

    @GetMapping("seat/save/G")
    public CommonResponse<?> saveSeatG(@RequestParam Long trainId,@RequestParam int businessSeat,@RequestParam int firstClassSeat,@RequestParam int secondClassSeat) {
        trainService.saveSeatsG(trainId,businessSeat,firstClassSeat,secondClassSeat);
        return CommonResponse.success();
    }

    @GetMapping("seat/save/K")
    public CommonResponse<?> saveSeatK(@RequestParam Long trainId,@RequestParam int softSleepSeat,@RequestParam int hardSleepSeat,@RequestParam int softSeat,@RequestParam int hardSeat) {
        trainService.saveSeatsK(trainId,softSleepSeat,hardSleepSeat,softSeat,hardSeat);
        return CommonResponse.success();
    }

    @GetMapping("seat/release/G")
    public CommonResponse<?> releaseSeatG(@RequestParam Long trainId,@RequestParam int businessSeat,@RequestParam int firstClassSeat,@RequestParam int secondClassSeat) {
        trainService.releaseSeatsG(trainId,businessSeat,firstClassSeat,secondClassSeat);
        return CommonResponse.success();
    }
    @GetMapping("seat/release/K")
    public CommonResponse<?> releaseSeatK(@RequestParam Long trainId,@RequestParam int softSleepSeat,@RequestParam int hardSleepSeat,@RequestParam int softSeat,@RequestParam int hardSeat) {
        trainService.releaseSeatsK(trainId,softSleepSeat,hardSleepSeat,softSeat,hardSeat);
        return CommonResponse.success();
    }

    @GetMapping("seat/save/G/{trainId}")
    public CommonResponse<?> getSaveNumG(@PathVariable Long trainId) {
        return CommonResponse.success(trainService.getSeatsNumG(trainId));
    }


    @GetMapping("seat/save/K/{trainId}")
    public CommonResponse<?> getSaveNumK(@PathVariable Long trainId) {
        return CommonResponse.success(trainService.getSeatsNumK(trainId));
    }
}
