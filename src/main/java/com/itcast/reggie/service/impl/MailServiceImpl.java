package com.itcast.reggie.service.impl;

import com.itcast.reggie.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleMail(String mailFrom, String mailFromNick, String mailTo, String cc, String subject, String content) {
        try {
            // 多个收件人之间用英文逗号分隔
            String[] mailToArr = mailTo.split(",");
            for (String address : mailToArr) {
                // 简单邮件信息类
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                // HTML邮件信息类
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                // 昵称
                mimeMessageHelper.setFrom(new InternetAddress(mailFromNick + " <" + mailFrom + ">"));
                mimeMessageHelper.setTo(address);
                if (!StringUtils.isEmpty(cc)) {
                    mimeMessageHelper.setCc(cc);
                }
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(content);

                mailSender.send(mimeMessage);
            }
        } catch (Exception e) {
            log.error("发送邮件失败：", e);
        }

    }
}
