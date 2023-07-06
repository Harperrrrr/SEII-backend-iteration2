package org.fffd.l23o6.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.mapper.MyUserMapper;
import org.fffd.l23o6.pojo.vo.user.EditUserInfoRequest;
import org.fffd.l23o6.pojo.vo.user.LoginRequest;
import org.fffd.l23o6.pojo.vo.user.RegisterRequest;
import org.fffd.l23o6.pojo.vo.user.UserVO;
import org.fffd.l23o6.service.UserService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("session")
    public CommonResponse<?> login(@Valid @RequestBody LoginRequest request) {
        // Throws BizException if auth failed.
        try {
            userService.login(request.getUsername(), request.getPassword(), request.getUsertype());
            StpUtil.login(request.getUsername());
            return CommonResponse.success();
        } catch (Exception ex) {
            if (ex instanceof BizException) {
                // 业务异常，将异常信息构造为CommonResponse对象返回
                return CommonResponse.error((BizException) ex);
            } else {
                // 非业务异常，抛出原始异常信息
                throw ex;
            }
        }
    }

    @PostMapping("user")
    public CommonResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        // Throws BizException if register failed.
        try {
            userService.register(request.getUsertype(), request.getUsername(), request.getPassword(), request.getName(), request.getIdn(), request.getPhone(), request.getType());
            return CommonResponse.success();
        } catch (Exception ex) {
            if (ex instanceof BizException) {
                // 业务异常，将异常信息构造为CommonResponse对象返回
                return CommonResponse.error((BizException) ex);
            } else {
                // 非业务异常，抛出原始异常信息
                throw ex;
            }
        }
    }

    @DeleteMapping("session")
    public CommonResponse<?> logout() {
        StpUtil.checkLogin();
        return CommonResponse.success(200);
    }

    @GetMapping("user")
    public CommonResponse<UserVO> userInfo() {
        StpUtil.checkLogin();
        UserVO user = userService.getUser(String.valueOf(StpUtil.getLoginId()));
        return CommonResponse.success(user);
    }

    @PostMapping("findUserIDByIdn")
    public CommonResponse<Long> findUserIDByIdn(String idn){
        return CommonResponse.success(userService.findByIdn(idn));
    }

    @PutMapping("user")
    public CommonResponse<?> editInfo(@Valid @RequestBody EditUserInfoRequest request) {
        StpUtil.checkLogin();
        userService.editInfo(StpUtil.getLoginIdAsString(), request.getName(), request.getIdn(), request.getPhone(), request.getType());
        return CommonResponse.success();
    }
}