package com.teamProj.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zhenzi.sms.ZhenziSmsClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class SMS {
    @Value("${sms.apiUrl}")
    // 访问路径（个人开发者使用https://sms_developer.zhenzikj.com，企业开发者使用https://sms.zhenzikj.com）
    private String apiUrl;

    /*
     *注册账号，进入榛子云短信平台用户中心，打开应用管理，我的应用，
     *即可看到默认有个应用名称为云短信体验的应用，输入对应的appId和appSecret
     *需要先充值至少20块钱，一条验证码短信预计3分钱左右
     */
    @Value("${sms.appId}")
    private String appId;

    @Value("${sms.appSecret}")
    private String appSecret;

    // 设置过期时间
    @Value("${sms.timeOut}")
    private Integer timeOut;

    // 设置验证码长度
    @Value("${sms.codeLength}")
    private Integer codeLength;

    private ZhenziSmsClient client;

    @Bean
    public ZhenziSmsClient initSMS() {
        if (client == null) {
            client = new ZhenziSmsClient(apiUrl, appId, appSecret);
            return client;
        }
        return client;
    }

    // 查询榛子云剩余当前账户可发验证码短信条数
    public Integer checkBalance() throws Exception {
        String balance = client.balance(); // {"code":0,"data":537}
        isSendSuccess isSendSuccess = JSONObject.parseObject(balance, isSendSuccess.class);
        return (int) isSendSuccess.getData();
    }

    // 生成验证码和验证码id
    private Map<String, Object> createVerificationCode() {
        HashMap<String, Object> map = new HashMap<>();
        // 这个是榛子云短信平台用户中心下的短信管理的短信模板的模板id
        map.put("templateId", "12405");
        // 生成验证码
        int pow = (int) Math.pow(10, codeLength - 1);
        String verificationCode = String.valueOf((int) (Math.random() * 9 * pow + pow));
        // 随机生成messageId，验证验证码的时候，需要携带这个参数去取验证码
        String messageId = UUID.randomUUID().toString();
        map.put("messageId", messageId);
        String[] templateParams = new String[2];
        // 两个参数分别为验证码和过期时间
        templateParams[0] = verificationCode;
        templateParams[1] = String.valueOf(timeOut);
        map.put("templateParams", templateParams);
        return map;
    }

    // 发送验证码（如果params的success为true，则发送成功，则需要把params中的messageId和验证码存起来，验证验证码的时候用到）
    public Map<String, Object> sendMessage(String phoneNumber, String clientIp) throws Exception {
        Map<String, Object> params = createVerificationCode();
        // 发送手机目标（number字段不可修改）
        params.put("number", phoneNumber);
        // 防止一个客户端多次刷验证码，防刷专用,这个clientIp只是个防刷标记，
        // 不一定是客户端ip，也可以是客户端登录的账号，或者能鉴权的属性
        if (StringUtils.isNotBlank(clientIp)) {
            params.put("clientIp", clientIp);
        }
        String result = client.send(params);
        isSendSuccess success = JSONObject.parseObject(result, isSendSuccess.class);
        if (success.getCode() == 0) {
            params.put("success", true);
        } else {
            params.put("success", false);
        }
        return params;
    }

    // 验证短信验证码,传入缓存中存入的messageId和验证码
    public boolean checkMessage(String messageId, String cacheCode) throws Exception {
        String result = client.findSmsByMessageId(messageId);
        Verification verification = JSONObject.parseObject(result, Verification.class);
        if (verification.getCode() == 0) {
            // 取到了数据，开始验证码是否正确
            String veificationCode = verification.getData().getMessage();
            int index = veificationCode.indexOf("验证码:");
            String code = veificationCode.substring(index + 4, index + 4 + codeLength);
            // 验证验证码
            if (!code.equals(cacheCode)) {
                return false;
            }
            // 验证时间是否过期
            String createTimeTemp = verification.getData().getCreateTime();
            Date createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTimeTemp);
            long spent = new Date().getTime() - createTime.getTime();
            if (spent / (1000 * 60) > timeOut) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // 向榛子云发送验证码请求时候，榛子云返回的报文,code=0为成功
    // 实例：{"code":0,"data":"发送成功"}
    @Data
    static class isSendSuccess implements Serializable {
        private Integer code;
        private Object data;
    }

    // 验证验证码的时候，榛子云返回的报文
    // 实例：{"code":0,"data":{"message":"验证码:33356，2分钟内有效，请勿泄漏给他人使用。","appName":"云短信体验","createTime":"2021-03-10 19:24:19","status":"success","toNumber":"15871770252","messageId":"123"}}
    @Data
    static class Verification implements Serializable {
        private Integer code;
        private Message data;
    }

    @Data
    static class Message implements Serializable {
        private String message;
        private String appName;
        private String createTime;
        private String status;
        private String toNumber;
        private String messageId;
    }
}
