package top.mjava.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "srp_user")
public class SRPUser {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_v")
    private String userV;

    @Column(name = "salt")
    private String salt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserV() {
        return userV;
    }

    public void setUserV(String userV) {
        this.userV = userV;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
