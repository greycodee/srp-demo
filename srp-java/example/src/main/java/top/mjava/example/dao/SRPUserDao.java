package top.mjava.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.mjava.example.entity.SRPUser;

@Repository
public interface SRPUserDao extends JpaRepository<SRPUser,Integer> {


}
