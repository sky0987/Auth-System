package com.itcast.reggie.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Employee;
import com.itcast.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     */
     @PostMapping
    public  R<String> add(HttpServletRequest request,@RequestBody Employee employee){
         log.info(employee.toString());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //当前创建用户的 当前登录创建的
         //employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
         //employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));

        boolean save = employeeService.save(employee);
        if (save){
            return R.success("添加成功");
        }
       return R.error("添加失败");

    }

    /**
     * 查询全部员工
     * name  用于姓名查询
     */
   @GetMapping("/page")
    public   R<Page> getAll(int page,int pageSize,String name){
         Page page1=new Page(page,pageSize);
         //构造条件构造器
         LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
         //添加过滤条件  查询条件
         queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
         //添加排序条件
         queryWrapper.orderByDesc(Employee::getUpdateTime);

       Page page2 = employeeService.page(page1, queryWrapper);
       System.out.println(page2);

       return R.success(page1);

     }



    /**
     * 修改员工信息
     * @param employee
     * @return
     */

    @PutMapping
    public  R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
       // Long employee1 = (long)request.getSession().getAttribute("employee");
       // employee.setUpdateTime(LocalDateTime.now());
       // employee.setUpdateUser(employee1);
            employeeService.updateById(employee);

        return R.success("修改成功");
     }

    /**
     *  查询当前修改数据的id 回显数据
     * @param
     * @return
     */

    @GetMapping("/{id}")
    public  R<Employee> getById(@PathVariable Integer id){
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }
}
