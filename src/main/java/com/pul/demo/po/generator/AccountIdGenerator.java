package com.pul.demo.po.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RadixUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class AccountIdGenerator implements IdentifierGenerator {
	private static final Snowflake snowflake = new Snowflake();

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return RadixUtil.encode(RadixUtil.RADIXS_SHUFFLE_34, snowflake.nextId());
	}
}
