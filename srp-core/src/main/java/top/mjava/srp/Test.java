package top.mjava.srp;

import top.mjava.entity.SRPGroupEntity;
import org.apache.commons.codec.digest.DigestUtils;
import top.mjava.util.SRPCommonUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Test {

    public static void main(String[] args) {
        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        BigInteger random_a = SRPCommonUtils.random_a();
        BigInteger x = SRPCommonUtils.calculate_x(
                messageDigest,
                "kang1",
                "kangkang123123",
                "22623317-4c8c-4c02-8acb-d44f73a0ca72"
        );

        BigInteger A = SRPClient.calculateA(
                SRPGroups.rfc5054_1024,
                random_a
        );
        System.out.println(A);

        BigInteger clientSecret = SRPClient.calculateSecretKey(
                messageDigest,
                SRPGroups.rfc5054_1024,
                A,
                new BigInteger("145077548320342492028380058301805914083145437951618418683057910230434223219173991207569760427426145286543888306272809482094825048293467307913027844992445213005533932696046243497890358527124085583572378385068736640237723381116197008306947257230635543815838189138786601838012645782595246312455029195478400397398"),
                random_a,
                "kang1",
                "kang123123",
                "22623317-4c8c-4c02-8acb-d44f73a0ca72"
        );
        BigInteger M1 = SRPClient.calculatedM1(
                messageDigest,
                A,
                new BigInteger("145077548320342492028380058301805914083145437951618418683057910230434223219173991207569760427426145286543888306272809482094825048293467307913027844992445213005533932696046243497890358527124085583572378385068736640237723381116197008306947257230635543815838189138786601838012645782595246312455029195478400397398"),
                clientSecret
                );
        System.out.println(M1);
    }
}
