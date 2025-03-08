package com.pul.demo.po;

import cn.hutool.core.collection.ListUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 11:05
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14SubscribeGroupPO.TABLE_NAME)
@Table(indexes = {
		@Index(name = "user_world_name_idx", columnList = "wold_id,user_id", unique = true)
})
public class FF14SubscribeGroupPO {
	public static final String TABLE_NAME = "ff14_subscribe_group";

	private static final String SUBSCRIBE_GROUP_ITEMS = "ff14_subscribe_group_items";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private FF14UserPO user;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private FF14WorldPO world;

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	@JoinTable(name = SUBSCRIBE_GROUP_ITEMS,
			joinColumns = @JoinColumn(name = "user_subscribe_id"),
			inverseJoinColumns = @JoinColumn(name = "item_sub_id"),
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
			inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private List<FF14ItemSubPO> items = ListUtil.list(false);


}
