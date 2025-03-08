package com.pul.demo.repo;

import com.pul.demo.po.FF14ItemPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:57
 */
public interface FF14ItemRepo extends JpaRepository<FF14ItemPO, Long>, JpaSpecificationExecutor<FF14ItemPO> {

	List<FF14ItemPO> findByName(String name);

	List<FF14ItemPO> findByNameIn(List<String> names);

	List<FF14ItemPO> findByNameContaining(String name);

}
