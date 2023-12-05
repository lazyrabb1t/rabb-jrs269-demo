package xyz.lazyrabbit;

import xyz.lazyrabbit.pojo.domain.UserDO;
import xyz.lazyrabbit.pojo.qo.UserQO;
import xyz.lazyrabbit.pojo.vo.UserVO;
import xyz.lazyrabbit.struct.UserStruct;

import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {
        UserQO userQO = new UserQO();
        userQO.setName("拉布拉多");
        userQO.setPassword("123456");
        userQO.setAge(18);
        userQO.setCreateTime(LocalDateTime.now().minusYears(18l));

        UserDO userDO = UserStruct.INSTANCE.toDO(userQO);
        System.out.println(userDO.toString());

        UserVO userVO = UserStruct.INSTANCE.toVO(userDO);
        System.out.println(userVO.toString());
    }
}
