package top.mjava.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mjava.example.entity.SRPUser;
import top.mjava.example.service.SRPService;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class Test {

    @Resource
    public SRPService srpService;

    @GetMapping("/save")
    public String test(){
        srpService.saveUser();
        return "success";
    }

    @GetMapping("/all")
    public List<SRPUser> getAll(){

        return srpService.getAllUser();
    }

    @GetMapping("/redis-set")
    public String setRedis(){
        srpService.redisSet();
        return "success redis";
    }

    @GetMapping("/redis-get")
    public String getRedisAll(){
        return srpService.redisGet();
    }
}
