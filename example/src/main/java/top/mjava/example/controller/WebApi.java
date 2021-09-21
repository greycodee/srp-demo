package top.mjava.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mjava.example.service.SRPService;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class WebApi {

    @Resource
    SRPService service;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> params){
        String username = params.get("userName").toString();
        String password = params.get("password").toString();
        service.registerUser(username,password);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code",200);
        result.put("msg","success");
        return result;
    }

    @PostMapping("/exchange")
    public Map<String, Object> exchange(@RequestBody Map<String, Object> params){
        String username = params.get("userName").toString();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code",200);
        result.put("msg","success");
        result.put("data",service.exchangeInfo(username));
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> params){
        String username = params.get("userName").toString();
        String M1 = params.get("M1").toString();
        String A = params.get("A").toString();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code",200);
        result.put("msg","success");
        result.put("data",service.login(username,A,M1));
        return result;
    }
}
