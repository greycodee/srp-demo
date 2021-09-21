package top.mjava.example.service.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.mjava.example.dao.SRPUserDao;
import top.mjava.example.entity.SRPUser;
import top.mjava.example.service.SRPService;
import top.mjava.srp.SRPGroups;
import top.mjava.srp.SRPServer;
import top.mjava.util.SRPCommonUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SRPServiceImpl implements SRPService {

    @Resource
    SRPUserDao srpUserDao;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void registerUser(String username, String password) {
        String slat = UUID.randomUUID().toString();
        // 计算 v 值
        BigInteger v = SRPServer.calculate_v(
                DigestUtils.getSha1Digest(),
                SRPGroups.rfc5054_1024,
                username,
                password,
                slat);
        // 持久化 用户名 盐值salt 验证值v
        SRPUser user = new SRPUser();
        user.setSalt(slat);
        user.setUserName(username);
        user.setUserV(Hex.encodeHexString(v.toByteArray()));
        System.out.println(user.toString());
        srpUserDao.save(user);
    }

    @Override
    public Map<String, Object> exchangeInfo(String username) {
        SRPUser user = srpUserDao.findByUserName(username);
        BigInteger random_b = SRPCommonUtils.random_b();
        System.out.println(user.toString());

        BigInteger v = null;
        try {
            v = new BigInteger(1,Hex.decodeHex(user.getUserV()));
        }catch (DecoderException e){
            throw new RuntimeException(e.getMessage());
        }
        BigInteger server_B = SRPServer.calculateB(
                DigestUtils.getSha1Digest(),
                SRPGroups.rfc5054_1024,
                v,
                random_b
        );

        // 把随机数 b 和 公钥 B 存到 redis 供下次登陆时使用
        String redisKey = username+"_auth_info";
        stringRedisTemplate.opsForHash().put(redisKey,"random_b",String.valueOf(random_b));
        stringRedisTemplate.opsForHash().put(redisKey,"B",String.valueOf(server_B));
        stringRedisTemplate.opsForHash().put(redisKey,"v",user.getUserV());
        stringRedisTemplate.expire(redisKey,5, TimeUnit.MINUTES);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("salt",user.getSalt());
        result.put("B",server_B);
        return result;
    }

    @Override
    public Map<String, Object> login(String username, String A, String M1) {
        // 从 Redis 获取缓存
        String cache_random_b = (String) stringRedisTemplate.opsForHash().get(username+"_auth_info","random_b");
        String cache_B = (String) stringRedisTemplate.opsForHash().get(username+"_auth_info","B");
        String cache_v = (String) stringRedisTemplate.opsForHash().get(username+"_auth_info","v");

        if (cache_random_b == null || cache_B == null || cache_v == null){
            throw new RuntimeException("参数获取失败");
        }

        BigInteger v = null;
        try {
            v = new BigInteger(1,Hex.decodeHex(cache_v));
        }catch (DecoderException e){
            throw new RuntimeException(e.getMessage());
        }
        BigInteger random_b = new BigInteger(cache_random_b);
        BigInteger B = new BigInteger(cache_B);
        // 计算服务端私钥
        BigInteger serverSecret = SRPServer.calculateSecretKey(
                DigestUtils.getSha1Digest(),
                SRPGroups.rfc5054_1024,
                new BigInteger(A),
                B,
                v,
                random_b
        );

        boolean flag = SRPServer.verifyM1(
                DigestUtils.getSha1Digest(),
                new BigInteger(M1),
                new BigInteger(A),
                B,
                serverSecret
        );

        if (!flag) {
            throw new RuntimeException("M1 验证失败");
        }
        // 缓存服务端私钥
        stringRedisTemplate.opsForValue().set(username+"_server_secret",
                DigestUtils.sha1Hex(
                        Arrays.toString(
                                Hex.encodeHex(serverSecret.toByteArray())
                        )
                ),1,TimeUnit.DAYS
        );
        // 计算 M2
        BigInteger M2 = SRPServer.calculateM2(
                DigestUtils.getSha1Digest(),
                new BigInteger(A),
                new BigInteger(M1),
                serverSecret
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("M2",M2);
        return result;
    }
}
