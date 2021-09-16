package top.mjava.util;

import top.mjava.entity.SRPGroupEntity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Base on SRP-6a
 * See RFC-5054
 *
 * 此类用于计算公共字段
 * 例如：
 *  随机数 a、b
 *  u
 *  k
 *  PAD() 运算
 *  s 盐值获取
 *
 * */
public class SRPCommonUtils {

    /**
     * 生成安全随机数
     * */
    private static BigInteger genSecureRandom(){
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            sr = new SecureRandom();
        }
        byte[] buffer = new byte[64];
        sr.nextBytes(buffer);
        return new BigInteger(1,buffer);
    }

    public static BigInteger random_a(){
        return genSecureRandom();
    }

    public static BigInteger random_b(){
        return genSecureRandom();
    }


    /**
     * SRP-6a 中的隐式转换
     * PAD() 操作
     * - 先将src整数转换为 byte[]
     * - 如果结果 byte[] 的最高位为 0（也就是byte[0]==0），将最高位的0删除
     * - 然后与 N 的转换后的byte[] 长度对比（可以通过 ceil((N.bitLength() + 1)/8) 快速算出 N 的长度），
     * - 如果 src 的字节长度小于 N 的字节长度，则将 src 的字节数组**左边补0**，直到等于 N 的字节长度。
     * - 如果 src 的字节长度大于等于 N 的字节长度，则不需要补 0 操作
     * */
    public static byte[] PAD(BigInteger src, SRPGroupEntity groups){
        byte[] srcBytes = implicitlyConvert(src);
        int NLen = (groups.getN().bitLength() + 1)/8;
        if (srcBytes.length < NLen){
            byte[] tmp = new byte[NLen];
            System.arraycopy(srcBytes,0,tmp,NLen - srcBytes.length,srcBytes.length);
            return tmp;
        }
        return srcBytes;
    }
    /**
     * 隐式转换
     * */
    private static byte[] implicitlyConvert(BigInteger src){
        byte[] bytes = src.toByteArray();
        if (bytes[0] == 0 && bytes.length!=1){
            byte[] tmp = new byte[bytes.length-1];
            System.arraycopy(bytes,1,tmp,0,tmp.length);
            return tmp;
        }
        return bytes;
    }

    /**
     * 计算 SRP-6a 中的 u
     * u = SHA1(PAD(A) | PAD(B))
     * @param digest 消息加密抽象类
     *               可以用 Apache 的工具包 org.apache.commons.codec.digest 来使用
     *               传入选择的加密方法
     * @param A 客户端公钥
     * @param B 服务端公钥
     * @param groups SRP Group
     * */
    public static BigInteger calculate_u(MessageDigest digest,
                                         BigInteger A,
                                         BigInteger B,
                                         SRPGroupEntity groups){
        byte[] padA = PAD(A,groups);
        byte[] padB = PAD(B,groups);
        digest.update(padA);
        digest.update(padB);
        return new BigInteger(1,digest.digest());
    }

    /**
     * 计算 SRP-6a 中的 k
     * k = SHA1(N | PAD(g))
     * */
    public static BigInteger calculate_k(MessageDigest digest,SRPGroupEntity groups){
        byte[] pad_g = PAD(groups.getG(), groups);

        digest.update(groups.getN().toByteArray());
        digest.update(pad_g);
        return new BigInteger(1,digest.digest());
    }

    /**
     * 计算 x
     * @param digest 加密器
     * @param slat 盐值
     * */
    public static BigInteger calculate_x(MessageDigest digest, String userName, String password, String slat){
        byte[] userNameBytes = userName.getBytes();
        byte[] passwordBytes = password.getBytes();
        digest.update(userNameBytes);
        digest.update(":".getBytes());
        digest.update(passwordBytes);
        byte[] userInfoBytes = digest.digest();

        digest.reset();
        digest.update(slat.getBytes());
        digest.update(userInfoBytes);

        return new BigInteger(1,digest.digest());
    }


}
