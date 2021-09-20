package top.mjava.srp;

import top.mjava.entity.SRPGroupEntity;
import top.mjava.exception.SRPException;
import top.mjava.util.SRPCommonUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Base on SRP-6a
 * See RFC-5054
 *
 * SRP-6a 中的客户端实现
 * 一般实际运用中，客户端由前端来实现
 *
 * 客户端主要计算一下字段：
 *  A           客户端公钥
 *  x           计算私钥时的参数 x
 *  session     客户端私钥
 *  M1
 *
 * 其中 N,g,slat,B 从服务端获取
 * */
public class SRPClient {

    private String userName;
    private String password;
    private BigInteger a;

    public SRPClient(String userName, String password, BigInteger a) {
        this.userName = userName;
        this.password = password;
        this.a = a;
    }

    /**
     * 计算客户端公钥 A
     * @param groups 素数 N 和 g
     * */
    public BigInteger calculateA(SRPGroupEntity groups){
        return groups.getG().modPow(a,groups.getN());
    }



    /**
     * 计算客户端密钥
     * @param digest 加密器
     * @param groups SRP-6a Group
     * @param A 客户端公钥
     * @param B 服务端公钥
     * @param slat 盐值
     * */
    public BigInteger calculateSecretKey(MessageDigest digest,
                                         SRPGroupEntity groups,
                                         BigInteger A,
                                         BigInteger B,
                                         String slat){
        BigInteger N = groups.getN();
        BigInteger g = groups.getG();
        BigInteger u = SRPCommonUtils.calculate_u(digest, A, B, groups);
        BigInteger k = SRPCommonUtils.calculate_k(digest,groups);
        BigInteger x = SRPCommonUtils.calculate_x(digest,this.userName,this.password,slat);

        BigInteger tmp_one = k.multiply(g.modPow(x,N)).add(N);
        BigInteger tmp_two = a.add(u.multiply(x).mod(N)).mod(N);
        BigInteger tmp_three = B.subtract(tmp_one).mod(N);

        return tmp_three.modPow(tmp_two,N);
    }

    /**
     * 计算 M1
     * @param digest 加密器
     * @param A 客户端公钥
     * @param B 服务端公钥
     * @param clientSecretKey 客户端私钥
     * */
    public BigInteger calculatedM1(MessageDigest digest,
                                   BigInteger A,
                                   BigInteger B,
                                   BigInteger clientSecretKey){
        digest.update(A.toByteArray());
        digest.update(B.toByteArray());
        digest.update(clientSecretKey.toByteArray());
        return new BigInteger(1,digest.digest());
    }

    /**
     * 验证服务端返回的 M2
     * @param digest 加密器
     * @param A 客户端公钥
     * @param M1 客户端计算的 M1
     * @param clientSecretKey 客户端私钥
     * */
    public Boolean verifyM2(MessageDigest digest,
                            BigInteger M2,
                            BigInteger A,
                            BigInteger M1,
                            BigInteger clientSecretKey){
        if (A==null || M1==null || clientSecretKey==null){
            throw new SRPException("verifyM2：验证 M2 时参数有误，请检查");
        }
        digest.update(A.toByteArray());
        digest.update(M1.toByteArray());
        digest.update(clientSecretKey.toByteArray());

        BigInteger verifyKey = new BigInteger(1,digest.digest());
        return verifyKey.equals(M2);
    }

}
