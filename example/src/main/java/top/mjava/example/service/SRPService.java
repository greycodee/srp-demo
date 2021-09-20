package top.mjava.example.service;

import top.mjava.example.entity.SRPUser;

import java.util.List;

public interface SRPService {
    void saveUser();

    List<SRPUser> getAllUser();

    void redisSet();

    String redisGet();

    /**
     * 认证 接口
     * */

}
