package top.mjava.srp;

import top.mjava.entity.SRPGroupEntity;
import top.mjava.util.SRPCommonUtils;

import java.math.BigDecimal;
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

    public BigInteger calculateSecretKey(MessageDigest digest,
                                         SRPGroupEntity groups,
                                         BigInteger A,
                                         BigInteger B,
                                         BigInteger v){
        BigInteger u = SRPCommonUtils.calculate_u(digest, A, B, groups);
        BigInteger N = groups.getN();
        return A.multiply(v.modPow(u,N)).mod(N).modPow(b,N);
    }

    public BigInteger calculateM2(MessageDigest digest,
                                  BigInteger A,
                                  BigInteger B,
                                  BigInteger M1){
        digest.update(A.toByteArray());
        digest.update(B.toByteArray());
        digest.update(M1.toByteArray());
        return new BigInteger(1,digest.digest());
    }

    public BigInteger calculate_v(MessageDigest digest, SRPGroupEntity groups,
                                  String userName, String password, String slat){
        BigInteger x = SRPCommonUtils.calculate_x(digest,userName,password,slat);
        return groups.getG().modPow(x,groups.getN());
    }
}
