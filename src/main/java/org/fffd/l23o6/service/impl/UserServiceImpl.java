package org.fffd.l23o6.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.mapper.MyTrainMapper;
import org.fffd.l23o6.mapper.MyUserMapper;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.pojo.vo.user.UserVO;
import org.fffd.l23o6.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private final MyUserMapper myUserMapper;
    @Override
    public void register(String userType, String username, String password, String name, String idn, String phone, String type) {
        UserEntity user = userDao.findByUsername(username);

        if (user != null) {
            throw new BizException(BizError.USERNAME_EXISTS);
        }

        UserType usertype = null;

        switch (userType){
            case "客户":
                usertype = UserType.USER;
                break;
            case "铁路管理员":
                usertype = UserType.TRAIN_MANAGER;
                break;
            case "票务员":
                usertype = UserType.TICKET_MANAGER;
                break;
            case "余票管理员":
                usertype = UserType.REMAINING_TICKET_MANAGER;
                break;
        }

        userDao.save(UserEntity.builder().usertype(usertype).username(username).password(BCrypt.hashpw(password))
                .name(name).idn(idn).phone(phone).type(type).mileagePoints(0).build());
    }

    @Override
    public UserEntity findByUserName(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void login(String username, String password, String userType) {
        UserEntity user = userDao.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new BizException(BizError.INVALID_CREDENTIAL);
        }
        else if (!user.getUsertype().getText().equals(userType)) {
            throw new BizException(BizError.INVALID_IDENTITY);
        }
    }

    @Override
    public void editInfo(String username, String name, String idn, String phone, String type){
        UserEntity user = userDao.findByUsername(username);
        if(user == null){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "用户不存在");
        }
        userDao.save(user.setIdn(idn).setName(name).setPhone(phone).setType(type));
    }

    @Override
    public UserVO getUser(String username) {
        UserEntity user = userDao.findByUsername(username);
        return myUserMapper.toUserVO(user);
    }
}