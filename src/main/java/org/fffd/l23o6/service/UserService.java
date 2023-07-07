package org.fffd.l23o6.service;

import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.vo.user.UserVO;

public interface UserService {
    void login(String username, String password, String userType);
    void register(String userType, String username, String password, String name, String idn, String phone, String type);

    /**
     * input : idn
     * output : UserVO
     * find User By Idn
     */
    UserVO findByIdn(String idn);
    void editInfo(String username, String name, String idn, String phone, String type);
    UserVO getUser(String username);
}