package xyz.lazyrabbit.struct;

import xyz.lazyrabbit.annotation.RabbMapper;
import xyz.lazyrabbit.pojo.domain.UserDO;
import xyz.lazyrabbit.pojo.qo.UserQO;
import xyz.lazyrabbit.pojo.vo.UserVO;
import xyz.lazyrabbit.util.MapperUtils;

@RabbMapper
public interface UserStruct {

    UserStruct INSTANCE = MapperUtils.getMapper(UserStruct.class);

    UserDO toDO(UserQO userQO);

    UserVO toVO(UserDO userDO);
}
