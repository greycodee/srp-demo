package top.mjava.entity;

import java.math.BigInteger;

/**
 * 用于传输 SRP-6a 中的 N 和 g
 * */
public class SRPGroupEntity {

    private BigInteger N;
    private BigInteger g;

    public SRPGroupEntity(BigInteger n, BigInteger g) {
        N = n;
        this.g = g;
    }

    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }
}
