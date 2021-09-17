package top.mjava.srp;

import org.apache.commons.codec.digest.DigestUtils;
import top.mjava.entity.SRPGroupEntity;
import top.mjava.util.SRPCommonUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Test {

    public static void main(String[] args) {
        String userName = "greycodee";
        String password = "huihiu123.";

        // 盐值
        String slat = "jdskljlkasd";
        // 设置 Group 和加密方法
        SRPGroupEntity groups = SRPGroups.rfc5054_1024;
        MessageDigest digest = DigestUtils.getSha1Digest();

        SRPClient client = new SRPClient(userName,password,SRPCommonUtils.random_a());

        SRPServer server = new SRPServer(SRPCommonUtils.random_b());
        BigInteger v = server.calculate_v(digest,groups,userName,password,slat);


        // change 接口
        BigInteger B = server.calculateB(digest,groups,v);

        // auth 接口
        // 客户端做的事 做完发给服务端
        BigInteger A = client.calculateA(groups);
        BigInteger client_secret = client.calculateSecretKey(digest,groups,A,B,slat);
        BigInteger M1 = client.calculatedM1(digest,A,B,client_secret);


        // 服务端接收到客户端请求后做的事
        BigInteger server_secret = server.calculateSecretKey(digest,groups,A,B,v);
        boolean flag_m1 = server.verifyM1(digest,M1,A,B,server_secret);
        System.out.println(flag_m1);
        BigInteger M2 = server.calculateM2(digest,A,M1,server_secret);

        // 客户端收到返回的M2 验证 M2
        boolean flag_m2 = client.verifyM2(digest,M2,A,M1,server_secret);
        System.out.println(flag_m2);

        System.out.println(client_secret);
        System.out.println(server_secret);


    }
}
