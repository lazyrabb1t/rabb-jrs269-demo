package xyz.lazyrabbit;

import xyz.lazyrabbit.pojo.domain.UserDO;
import xyz.lazyrabbit.pojo.vo.UserVO;
import xyz.lazyrabbit.struct.UserStruct;

import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {
        UserDO userDO = new UserDO();
        userDO.setName("拉布拉多");
        userDO.setAge(18);
        userDO.setCreateTime(LocalDateTime.now().minusYears(18l));
        UserVO userVO = UserStruct.INSTANCE.to(userDO);
        System.out.println(userVO.toString());
    }
}
