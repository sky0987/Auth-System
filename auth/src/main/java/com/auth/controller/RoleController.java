package com.auth.controller;


import com.auth.config.R;
import com.auth.dto.XcRoleDto;
import com.auth.mapper.XcPermissionMapper;
import com.auth.mapper.XcRoleMapper;
import com.auth.mapper.XcUserMapper;
import com.auth.mapper.XcUserRoleMapper;
import com.auth.po.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {


        @Autowired
        XcRoleMapper  xcRoleMapper ;

        @Autowired
        XcPermissionMapper xcPermissionMapper;


        /**
         * 查询所有用户信息
         * @param page
         * @param pagesize
         * @param name
         * @return
         */

        @GetMapping("/page")
        @PreAuthorize("hasAuthority('role:get')")
        public R<Page> get(int  page,int pagesize,String name){
        Page<XcRole>  page1=new Page<>(page,pagesize);
        xcRoleMapper.selectPage(page1,new LambdaQueryWrapper<XcRole>().eq(XcRole::getRoleName,name));
        return  R.success(page1);
    }

       @GetMapping()
       public  R<XcRoleDto> getId(@RequestParam("id") String id){
            //先去查询角色信息
           XcRole xcRole = xcRoleMapper.selectById(id);
           XcRoleDto xcRoleDto=new XcRoleDto();
           BeanUtils.copyProperties(xcRole,xcRoleDto);
           //再去根据角色id查询权限id
           List<XcPermission> xcPermissions = xcPermissionMapper.selectList(new LambdaQueryWrapper<XcPermission>().eq(XcPermission::getRoleId, xcRole.getId()));
           xcRoleDto.setPermissions(xcPermissions);
           return  R.success(xcRoleDto);
       }


        @PostMapping("/save")
        @PreAuthorize("hasAuthority('user:save')")
        public  R<String>  save(@RequestBody XcRoleDto xcRoleDto){
            //先将角色信息存入角色表之中
            xcRoleMapper.insert(xcRoleDto);
            //获取角色id
            String roleId = xcRoleDto.getId();
            //获取权限列表
            List<XcMenu> xcMenus = xcRoleDto.getXcMenus();
            int insert=0;
            for (XcMenu xcMenu : xcMenus) {
                //权限id
                String menuId = xcMenu.getId();
                //将权限id和角色id存入角色权限表中
               insert= xcPermissionMapper.insert(new XcPermission(roleId,menuId));
            }
            if (insert!=0){
            return  R.success("添加成功");
        }
        return R.error("添加失败");
    }

        @DeleteMapping("/del")
        @PreAuthorize("hasAuthority('user:del')")
        public  R<String>  del(@RequestParam("id") String id){
        int i = xcRoleMapper.deleteById(id);
        if (i!=0){
            return  R.success("删除成功");
        }
        return R.error("删除失败");
    }

        @PutMapping("/up")
        @PreAuthorize("hasAuthority('user:update')")
        public R<String> update(@RequestBody XcRole  xcRole){

        int i = xcRoleMapper.updateById(xcRole);
        if (i!=0){
            return  R.success("修改成功");
        }
        return R.error("修改失败");
    }

        /**
         * 用户角色分配
         */

        @PostMapping("/permission")
        @PreAuthorize("hasAuthority('role:permission')")
        public R<String> RolePermission(String roleId,String [] menuId){
            int insert=0;
            for (String s : menuId) {
                XcPermission xcPermission=new XcPermission(roleId,s);
                insert = xcPermissionMapper.insert(xcPermission);
            }
            if (insert!=0){
                return R.success("权限分配成功");
            }
            return R.error("权限分配失败");
        }

    }

