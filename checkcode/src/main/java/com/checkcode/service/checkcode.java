package com.checkcode.service;

import javax.servlet.http.HttpServletResponse;

public interface checkcode {


    Object verify(HttpServletResponse resp);
    boolean  checkcode(String code);

    String  code(String tel);

}
