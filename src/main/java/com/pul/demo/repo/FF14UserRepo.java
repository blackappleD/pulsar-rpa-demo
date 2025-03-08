package com.pul.demo.repo;

import com.pul.demo.po.FF14UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:57
 */
public interface FF14UserRepo extends JpaRepository<FF14UserPO, String>, JpaSpecificationExecutor<FF14UserPO> {

	Optional<FF14UserPO> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<FF14UserPO> findByUserAccount(String userAccount);

}
