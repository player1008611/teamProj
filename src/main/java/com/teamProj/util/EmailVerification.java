package com.teamProj.util;

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

  private void sendEmail(String toMail, String subject, String content) {
    MailMessage message = new MailMessage();
    message.setFrom("player10086@qq.com"); // 发件人
    message.setTo(toMail); // 收件人
    String server = "smtp.qq.com"; // 邮件服务器
    message.setSubject(subject); // 邮件主题
    message.setContent(content); // 邮件内容
    message.setDatafrom("player10086@qq.com"); // 发件人，在邮件的发件人栏目中显示
    message.setDatato(toMail); // 收件人，在邮件的收件人栏目中显示
    message.setUser("player10086@qq.com"); // 登陆邮箱的用户名
    message.setPassword("pflocreyskfiddaj"); // 登陆邮箱的密码

    SendEmail smtp;
    try {
      smtp = new SendEmail(server, 25);
      boolean flag;
      flag = smtp.sendMail(message, server);
      if (flag) {
        System.out.println("邮件发送成功！");
      } else {
        System.out.println("邮件发送失败！");
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String verificationService(String email) {
    Random random = new Random();
    String subject = "B5就业平台";
    String captcha = String.valueOf(random.nextInt(899999) + 100000);
    String content = "验证码为：" + captcha;
    sendEmail(email, subject, content);
    return captcha;
  }
}
