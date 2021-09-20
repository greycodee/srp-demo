package top.mjava.srp;

import top.mjava.entity.SRPGroupEntity;
import top.mjava.exception.SRPException;
import top.mjava.util.SRPCommonUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Base on SRP-6a
 * See RFC-5054
 * */
public class SRPServer {
    private BigInteger b;

    public SRPServer(BigInteger b) {
        this.b = b;
    }

    /**
     * 计算服务器公钥 B
     * @param digest 加密器
     * @param groups SRP-6a Group
     * @param v 注册时生成的 v
     * */
    public BigInteger calculateB(MessageDigest digest, SRPGroupEntity groups, BigInteger v){
        BigInteger k = SRPCommonUtils.calculate_k(digest, groups);
        BigInteger N = groups.getN();
        BigInteger g = groups.getG();

        BigInteger tmp_one = k.multiply(v).mod(N);
        BigInteger tmp_two = g.modPow(b,N);

        return tmp_one.add(tmp_two).mod(N);
    }

    /**
     * 计算服务端私钥
     * @param digest 加密器
     * @param groups SRP-6a Group
     * @param A 客户端公钥
     * @param B 服务端公钥
     * @param v 注册时生成的 v
     * */
    public BigInteger calculateSecretKey(MessageDigest digest,
                                         SRPGroupEntity groups,
                                         BigInteger A,
                                         BigInteger B,
                                         BigInteger v){
        BigInteger u = SRPCommonUtils.calculate_u(digest, A, B, groups);
        BigInteger N = groups.getN();
        return A.multiply(v.modPow(u,N)).mod(N).modPow(b,N);
    }

    /**
     * 服务端计算 M2
     * @param digest 加密器
     * @param A 客户端公钥
     * @param M1 客户端计算的 M1
     * @param serverSecretKey 服务端私钥
     * */
    public BigInteger calculateM2(MessageDigest digest,
                                  BigInteger A,
                                  BigInteger M1,
                                  BigInteger serverSecretKey){
        if (A==null || M1==null || serverSecretKey==null){
            throw new SRPException("创建 M2 时参数有误，请检查");
        }
        digest.update(A.toByteArray());
        digest.update(M1.toByteArray());
        digest.update(serverSecretKey.toByteArray());
        return new BigInteger(1,digest.digest());
    }

    /**
     * 用于注册的时候计算 v
     * @param digest 加密器
     * @param groups SRP-6a Group
     * @param userName 登陆用户名
     * @param password 登陆密码
     * @param slat 盐值
     * */
    public BigInteger calculate_v(MessageDigest digest, SRPGroupEntity groups,
                                  String userName, String password, String slat){
        BigInteger x = SRPCommonUtils.calculate_x(digest,userName,password,slat);
        return groups.getG().modPow(x,groups.getN());
    }

    /**
     * 验证客户端的 M1 是否正确
     * @param digest 加密器
     * @param M1 客户端的 M1
     * @param A 客户端公钥
     * @param B 服务端公钥
     * @param serverSecretKey 服务端私钥
     * */
    public Boolean verifyM1(MessageDigest digest,
                            BigInteger M1,
                            BigInteger A,
                            BigInteger B,
                            BigInteger serverSecretKey){
        if (A==null || B==null || serverSecretKey==null){
            throw new SRPException("验证 M1 时参数有误，请检查");
        }
        digest.update(A.toByteArray());
        digest.update(B.toByteArray());
        digest.update(serverSecretKey.toByteArray());

        BigInteger verifyKey = new BigInteger(1,digest.digest());
        return verifyKey.equals(M1);
    }
}
