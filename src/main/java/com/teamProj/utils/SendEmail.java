package com.teamProj.utils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

// import sun.misc.BASE64Encoder;

public class SendEmail {
    // 用于加密后发送用户名和密码
    private boolean debug = false;
    private Socket socket;

    public SendEmail(String server, int port) throws IOException {
        try {
            socket = new Socket(server, port);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("已经建立连接!");
        }
    }

    // 注册到邮件服务器
    public void helo(String server, BufferedReader in, BufferedWriter out) throws IOException {
        int result;
        result = getResult(in);
        // 连接上邮件服务后,服务器给出220应答
        if (result != 220) {
            throw new IOException("连接服务器失败");
        }
        result = sendServer("HELO " + server, in, out);
        // HELO命令成功后返回250
        if (result != 250) {
            throw new IOException("注册邮件服务器失败！"+result);
        }
        getResult(in);
        getResult(in);
    }

    private int sendServer(String str, BufferedReader in, BufferedWriter out) throws IOException {
        out.write(str+"\r\n");
        out.flush();
        if (debug) {
            System.out.println("已发送命令:" + str);
        }
        return getResult(in);
    }

    public int getResult(BufferedReader in) {
        String line = "";
        try {
            line = in.readLine();
            if (debug) {
                System.out.println("服务器返回状态:" + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 从服务器返回消息中读出状态码,将其转换成整数返回
        String str = line.substring(0, 3);
        return Integer.parseInt(str);
    }

    public void authLogin(MailMessage message, BufferedReader in, BufferedWriter out)
            throws IOException {
        int result;
        result = sendServer("AUTH LOGIN", in, out);
        if (result != 334) {
            throw new IOException("用户验证失败！");
        }
        result = sendServer(Base64.getEncoder().encodeToString(message.getUser().getBytes()), in, out);
        if (result != 334) {
            throw new IOException("用户名错误！");
        }
        result =
                sendServer(Base64.getEncoder().encodeToString(message.getPassword().getBytes()), in, out);
        if (result != 235) {
            throw new IOException("验证失败！");
        }
    }

    // 开始发送消息，邮件源地址
    public void mailFrom(String source, BufferedReader in, BufferedWriter out) throws IOException {
        int result;
        result = sendServer("MAIL FROM:<" + source + ">", in, out);
        if (result != 250) {
            throw new IOException("指定源地址错误");
        }
    }

    // 设置邮件收件人
    public void rcpt(String touchman, BufferedReader in, BufferedWriter out) throws IOException {
        int result;
        result = sendServer("RCPT TO:<" + touchman + ">", in, out);
        if (result != 250) {
            throw new IOException("指定目的地址错误！");
        }
    }

    // 邮件体
    public void data(
            String from, String to, String subject, String content, BufferedReader in, BufferedWriter out)
            throws IOException {
        int result;
        result = sendServer("DATA", in, out);
        // 输入date回车后,若收到354应答后,继续输入邮件内容
        if (result != 354) {
            throw new IOException("不能发送数据");
        }
        out.write("From: " + from);
        out.newLine();
        out.write("To: " + to);
        out.newLine();
        out.write("Subject: " + subject);
        out.newLine();
        out.newLine();
        out.write(content);
        out.newLine();
        // 句点加回车结束邮件内容输入
        result = sendServer(".", in, out);
        System.out.println(result);
        if (result != 250) {
            throw new IOException("发送数据错误");
        }
    }

    // 退出
    public void quit(BufferedReader in, BufferedWriter out) throws IOException {
        int result;
        result = sendServer("QUIT", in, out);
        if (result != 221) {
            throw new IOException("未能正确退出");
        }
    }

    // 发送邮件主程序
    public boolean sendMail(MailMessage message, String server) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            helo(server, in, out); // helo
            authLogin(message, in, out); // auth login
            mailFrom(message.getFrom(), in, out); // mail from
            rcpt(message.getTo(), in, out); // rcpt to
            data(
                    message.getDatafrom(),
                    message.getDatato(),
                    message.getSubject(),
                    message.getContent(),
                    in,
                    out); // DATA
            quit(in, out); // quit
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
