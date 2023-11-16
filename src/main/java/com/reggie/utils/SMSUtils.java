package com.reggie.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;

import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){

		Config config = new Config();
		config.setAccessKeyId("阿里云输入您的key");
		config.setAccessKeySecret("阿里云设置输入您的secret");

		try {
			Client client = new Client(config);
			SendSmsRequest request = new SendSmsRequest();

			request.setSignName(signName);

			request.setPhoneNumbers(phoneNumbers);

			request.setTemplateCode(templateCode);

			request.setTemplateParam("{\"code\":\""+param+"\"}");

			SendSmsResponse response = client.sendSms(request);

			System.out.println(new Gson().toJson(response.body));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
