package org.fffd.l23o6.mapper;

import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.vo.user.UserVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserMapper {
    public UserVO toUserVO(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserVO.UserVOBuilder userVO = UserVO.builder();

        userVO.username( userEntity.getUsername() );
        userVO.name( userEntity.getName() );
        userVO.phone( userEntity.getPhone() );
        userVO.idn( userEntity.getIdn() );
        userVO.userType(userEntity.getUserType());
        userVO.type( userEntity.getType() );
        userVO.mileagePoints(userEntity.getMileagePoints());

        return userVO.build();

    }
}
