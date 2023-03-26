package com.auth.dto;

import com.auth.po.XcMenu;
import com.auth.po.XcPermission;
import com.auth.po.XcRole;
import lombok.Data;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

@Data
public class XcRoleDto extends XcRole {

    List<XcMenu> xcMenus=new ArrayList<>();

    List<XcPermission> permissions=new ArrayList<>();
}
