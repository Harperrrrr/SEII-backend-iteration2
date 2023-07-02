package org.fffd.l23o6.pojo.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    private String username;
    private String name;
    private String phone;
    private String idn;
    private String type;
    private int mileagePoints;
}
