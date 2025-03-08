package com.pul.demo.repo;

import com.pul.demo.po.FF14SubscribeCfgPO;
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
public interface FF14SubscribeCfgRepo extends JpaRepository<FF14SubscribeCfgPO, Long>, JpaSpecificationExecutor<FF14SubscribeCfgPO> {


	Optional<FF14SubscribeCfgPO> findByUser(FF14UserPO user);


}
