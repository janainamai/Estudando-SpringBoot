package br.com.springboot.repository;

import br.com.springboot.domain.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    SystemUser findByUsername(String name);

}
