package top.mjava.example.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.mjava.example.dao.SRPUserDao;
import top.mjava.example.entity.SRPUser;
import top.mjava.example.service.SRPService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SRPServiceImpl implements SRPService {

    @Resource
    SRPUserDao srpUserDao;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveUser() {
        SRPUser user = new SRPUser();
        user.setUserV("sadasdf");
        user.setUserName("greycode");
        user.setSalt("232dddd");
        srpUserDao.save(user);
    }

    @Override
    public List<SRPUser> getAllUser() {
        return srpUserDao.findAll();
    }

    @Override
    public void redisSet() {
        stringRedisTemplate.opsForValue().set("name","huihiu");
    }

    @Override
    public String redisGet() {
        return stringRedisTemplate.opsForValue().get("name");
    }
}
