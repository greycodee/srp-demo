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

        String slat = "jdskljlkasd";

        SRPGroupEntity groups = SRPGroups.rfc5054_1024;
        MessageDigest digest = DigestUtils.getSha1Digest();

        SRPClient client = new SRPClient(userName,password,SRPCommonUtils.random_a());
        SRPServer server = new SRPServer(SRPCommonUtils.random_b());
        BigInteger v = server.calculate_v(digest,groups,userName,password,slat);

        BigInteger A = client.calculateA(groups);

        BigInteger B = server.calculateB(digest,groups,v);

        BigInteger client_secret = client.calculateSecretKey(digest,groups,A,B,slat);
        BigInteger server_secret = server.calculateSecretKey(digest,groups,A,B,v);

        System.out.println(client_secret);
        System.out.println(server_secret);


    }
}
