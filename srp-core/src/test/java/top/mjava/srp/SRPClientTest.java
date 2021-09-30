package top.mjava.srp;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import top.mjava.util.SRPCommonUtils;

import java.math.BigInteger;
import java.security.MessageDigest;


/**
 * 直接运行整个测试类
 * */
public class SRPClientTest {
    private static final MessageDigest messageDigest = DigestUtils.getSha1Digest();
    private static final BigInteger random_a = SRPCommonUtils.random_a();
    private static BigInteger A = null;

    /**
     * 用户信息
     * */
    private static final String userName = "test";
    private static final String password = "test111";
    private static final String salt = "e371da7a-f6c0-4746-91d3-1b3b25606c09";
    /**
     * 服务端公钥
     * */
    private static final String B = "109918270454468017112125644781430031638735321491499193167806373752992948280901784784134327892376086418970201548914202128047654563165694408562502112820410544943895420329439104856977518511512939252181872693818648926043000539856334431832882426087115018499130219772217818534709246582403817936668925576511285712499";


    @Test
    public void calculateA(){
        A = SRPClient.calculateA(
                SRPGroups.rfc5054_1024,
                random_a
        );
        System.out.printf("客户端公钥 A：%d%n",A);
    }

    @Test
    public void test(){
        BigInteger clientSecret = SRPClient.calculateSecretKey(
                messageDigest,
                SRPGroups.rfc5054_1024,
                A,
                new BigInteger(B),
                random_a,
                userName,
                password,
                salt
        );
        BigInteger M1 = SRPClient.calculatedM1(
                messageDigest,
                A,
                new BigInteger(B),
                clientSecret
        );
        System.out.printf("客户端 M1：%d%n",M1);
    }
}
