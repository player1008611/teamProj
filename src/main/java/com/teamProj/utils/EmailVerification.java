package com.teamProj.utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

public class EmailVerification {
    private SendEmail sendEmail;

    public SendEmail getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(SendEmail sendEmail) {
        this.sendEmail = sendEmail;
    }

    private boolean sendEmail(String toMail, String subject, String content) {
        MailMessage message = new MailMessage();
        message.setFrom("player10086@qq.com"); // 发件人
        message.setTo(toMail); // 收件人
        String server = "smtp.qq.com"; // 邮件服务器
        message.setSubject(subject); // 邮件主题
        message.setContent(content); // 邮件内容
        message.setDatafrom("player10086@qq.com"); // 发件人，在邮件的发件人栏目中显示
        message.setDatato(toMail); // 收件人，在邮件的收件人栏目中显示
        message.setUser("player10086@qq.com"); // 登陆邮箱的用户名
        message.setPassword("eqvkgpoubscybjga"); // 登陆邮箱的密码

        SendEmail smtp;
        try {
            smtp = new SendEmail(server, 25);
            boolean flag;
            flag = smtp.sendMail(message, server);
            if (flag) {
                //System.out.println("邮件发送成功！");
                return true;
            } else {
                //System.out.println("邮件发送失败！");
                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String verificationService(String email) {
        Random random = new Random();
        String subject = "B5就业平台";
        String captcha = String.valueOf(random.nextInt(899999) + 100000);
        String content = "验证码为：" + captcha;
        boolean state = sendEmail(email, subject, content);
        if (state) {
            return captcha;
        } else {
            return null;
        }
    }

    public Boolean interviewReminderService(String email, String content) {
        if (!email.matches(
                "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
            return false;
        }
        return sendEmail(email, "B5就业平台", content);
    }
}
