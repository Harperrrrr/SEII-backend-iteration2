package org.fffd.l23o6.mapper;

import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.vo.user.UserVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserMapper {
    public static UserVO toUserVO(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserVO userVO = new UserVO();

        userVO.setUsername( userEntity.getUsername() );
        userVO.setName( userEntity.getName() );
        userVO.setPhone( userEntity.getPhone() );
        userVO.setIdn( userEntity.getIdn() );
        userVO.setType( userEntity.getType() );

        return userVO;
    }
}
