package com.example.util;

/**
 * Created by szjdj on 2016-09-27.
 */
public class getTipUtil {

    public  static String getTip(String data) {
        if (data.indexOf("400") != -1) {
            return "无效请求";
        } else if (data.indexOf("407") != -1) {
            return "缺少数据";
        } else if (data.indexOf("408") != -1) {
            return "无效的参数";
        } else if (data.indexOf("418") != -1) {
            return "内部接口调用失败	";
        } else if (data.indexOf("450") != -1) {
            return "权限不足";
        } else if (data.indexOf("454") != -1) {
            return "数据格式错误";
        } else if (data.indexOf("455") != -1) {
            return "签名无效";
        } else if (data.indexOf("456") != -1) {
            return "手机号码为空";
        } else if (data.indexOf("457") != -1) {
            return "手机号码格式错误";
        } else if (data.indexOf("458") != -1) {
            return "手机号码在黑名单中	";
        } else if (data.indexOf("459") != -1) {
            return "无appKey的控制数据	";
        } else if (data.indexOf("460") != -1) {
            return "无权限发送短信";
        } else if (data.indexOf("461") != -1) {
            return "不支持该地区发送短信";
        } else if (data.indexOf("462") != -1) {
            return "每分钟发送次数超限";
        } else if (data.indexOf("463") != -1) {
            return "手机号码每天发送次数超限";
        } else if (data.indexOf("464") != -1) {
            return "每台手机每天发送次数超限";
        } else if (data.indexOf("465") != -1) {
            return "号码在App中每天发送短信的次数超限";
        } else if (data.indexOf("466") != -1) {
            return "校验的验证码为空";
        } else if (data.indexOf("467") != -1) {
            return "校验验证码请求频繁";
        } else if (data.indexOf("468") != -1) {
            return "需要校验的验证码错误";
        } else if (data.indexOf("469") != -1) {
            return "未开启web发送短信";
        } else if (data.indexOf("470") != -1) {
            return "账户余额不足";
        } else if (data.indexOf("471") != -1) {
            return "请求IP错误";
        } else if (data.indexOf("472") != -1) {
            return "客户端请求发送短信验证过于频繁";
        } else if (data.indexOf("473") != -1) {
            return "服务端根据duid获取平台错误";
        } else if (data.indexOf("474") != -1) {
            return "没有打开服务端验证开关";
        } else if (data.indexOf("475") != -1) {
            return "appKey的应用信息不存在";
        } else if (data.indexOf("476") != -1) {
            return "当前appkey发送短信的数量超过限额";
        } else if (data.indexOf("477") != -1) {
            return "当前手机号发送短信的数量超过限额";
        } else if (data.indexOf("478") != -1) {
            return "当前手机号在当前应用内发送超过限额";
        } else if (data.indexOf("500") != -1) {
            return "服务器内部错误";
        } else if (data.indexOf("600") != -1) {
            return "API使用受限制";
        } else if (data.indexOf("601") != -1) {
            return "短信发送受限";
        } else if (data.indexOf("602") != -1) {
            return "无法发送此地区短信";
        } else if (data.indexOf("603") != -1) {
            return "请填写正确的手机号码";
        } else if (data.indexOf("604") != -1) {
            return "当前服务暂不支持此国家";
        } else {
            return "错误";
        }
    }
}
