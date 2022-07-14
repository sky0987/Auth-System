package com.itcast.reggie.service;

public interface MailService {
    void sendSimpleMail(String mailFrom, String mailFromNick, String mailTo, String cc, String subject, String content);
}
