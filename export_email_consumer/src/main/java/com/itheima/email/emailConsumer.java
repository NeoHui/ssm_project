package com.itheima.email;

import com.alibaba.fastjson.JSON;
import com.itheima.utils.MailUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.Map;

public class emailConsumer implements MessageListener {
    @Override
    public void onMessage(Message message) {
        byte[] body = message.getBody();
        //字节数组转map
//        阿里巴巴的fastjson,将数组转为map
        Map<String,String> map = JSON.parseObject(body, Map.class);
        String to = map.get("to");
        String subject = map.get("subject");
        String content = map.get("content");
        try {
            MailUtil.sendMsg(to,subject,content);
            System.out.println("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("邮件发送失败");
        }
    }
}
