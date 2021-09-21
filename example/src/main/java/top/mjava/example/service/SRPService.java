package top.mjava.example.service;

import top.mjava.example.entity.SRPUser;

import java.util.List;
import java.util.Map;

public interface SRPService {
    void saveUser();

    List<SRPUser> getAllUser();

    void redisSet();

    String redisGet();

    /**
     * 注册接口
     * */
    void registerUser(String username,String password);

    /**
     * 交换公钥接口
     * 返回服务器公钥 和 注册时的加密盐值salt
     * @param username 用户名
     * */
    Map<String, Object> exchangeInfo(String username);

    /**
     * 登陆接口
     * */
    Map<String, Object> login(String username, String A, String M1);

}
