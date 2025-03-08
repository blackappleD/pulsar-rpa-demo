package com.pul.demo.repo;

import com.pul.demo.po.FF14WorldPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:57
 */
public interface FF14WorldRepo extends JpaRepository<FF14WorldPO, Long>, JpaSpecificationExecutor<FF14WorldPO> {

	Optional<FF14WorldPO> findByName(String name);

	List<FF14WorldPO> findByNameContaining(String name);

}
