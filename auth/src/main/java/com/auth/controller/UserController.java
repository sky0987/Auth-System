package com.auth.controller;



import com.auth.config.R;
import com.auth.dto.XcUserExt;
import com.auth.mapper.XcRoleMapper;
import com.auth.mapper.XcUserMapper;
import com.auth.mapper.XcUserRoleMapper;
import com.auth.po.XcRole;
import com.auth.po.XcUser;
import com.auth.po.XcUserRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    XcUserMapper  xcUserMapper;

    @Autowired
    XcUserRoleMapper  xcUserRoleMapper;

    @Autowired
    XcRoleMapper  xcRoleMapper;


    /**
     * 查询所有用户信息
     * @param page
     * @param pagesize
     * @param name
     * @return
     */

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('user:get')")
    public R<Page> get(int  page,int pagesize,String name){
        Page<XcUser>  page1=new Page<>(page,pagesize);
        //用户信息展示还需要展示角色信息  里面不包含角色的姓名
        Page<XcUserExt> xcUserExtPage=new Page<>();
        xcUserMapper.selectPage(page1, new LambdaQueryWrapper<XcUser>().eq(XcUser::getName, name));

        BeanUtils.copyProperties(page1,xcUserExtPage,"records");
        //重新设置records
        List<XcUser> records = page1.getRecords();
        //设置新的集合来接受角色姓名等用户信息
        List<XcUserExt>  xcUserExts=new ArrayList<>();
        //查询所有的角色信息
        List<XcRole> getall = xcRoleMapper.getall();
        for (XcUser record : records) {
            XcUserExt xcUserExt=new XcUserExt(); //只有用户的信息
            BeanUtils.copyProperties(record,xcUserExt);
            String roleId = xcUserRoleMapper.selectById(record.getId()).getRoleId();//得到每一个用户的角色id
            for (XcRole xcRole : getall) {
                //得到每一个角色信息
                if (xcRole.getId().equals(roleId)){
                    xcUserExt.setRoleName(xcRole.getRoleName());
                    break;
                }
            }
            xcUserExts.add(xcUserExt); //循环一个往集合中添加一个对象
        }
        //查询出来所有的角色信息
        return  R.success(xcUserExtPage);
    }


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('user:save')")
    public  R<String>  save(@RequestBody XcUser xcUser){
        int insert = xcUserMapper.insert(xcUser);
        if (insert!=0){
            return  R.success("添加成功");
        }
        return R.success("添加失败");
    }

    @DeleteMapping("/del")
    @PreAuthorize("hasAuthority('user:del')")
    public  R<String>  del(@RequestParam("id") String id){
        int i = xcUserMapper.deleteById(id);
        if (i!=0){
            return  R.success("删除成功");
        }
        return R.success("删除失败");
    }

    @PutMapping("/up")
    @PreAuthorize("hasAuthority('user:update')")
    public R<String> update(@RequestBody XcUser  xcUser){

        int i = xcUserMapper.updateById(xcUser);
        if (i!=0){
            return  R.success("修改成功");
        }
        return R.success("修改失败");
    }

    /**
     * 用户角色分配
     */

    @PostMapping("/role")
    @PreAuthorize("hasAuthority('user:role')")
    public R<String> UserRole(@RequestParam("userid") String userid,@RequestParam("roleid") String roleid){
        XcUserRole xcUserRole = new XcUserRole(userid, roleid);
        int insert = xcUserRoleMapper.insert(xcUserRole);
        if (insert!=0){
            return  R.success("角色分配成功");
        }
        return R.success("角色分配失败");
    }

}
