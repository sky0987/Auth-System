package com.auth.mapper;


import com.auth.po.XcRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper

public interface XcRoleMapper extends BaseMapper<XcRole> {

    @Select("select * from xc_role")
    public List<XcRole> getall();
}
