package com.pul.demo.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Comment;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 11:05
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14SubscribeCfgPO.TABLE_NAME)
@Table(indexes = {
		@Index(name = "cfg_user_id_idx", columnList = "user_id")
})
public class FF14SubscribeCfgPO {
	public static final String TABLE_NAME = "ff14_subscribe_cfg";

	private static final String SUBSCRIBE_GROUP_ITEMS = "ff14_subscribe_group_items";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private FF14UserPO user;

	@Comment("是否开启推送通知")
	private Boolean notify = false;
}
