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
 * @date 2024/12/20 11:16
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14ItemSubPO.TABLE_NAME)
public class FF14ItemSubPO {
	public static final String TABLE_NAME = "ff14_subscribe_item";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private FF14ItemPO item;

	@Comment("是否只订阅hq")
	private Boolean hq;

	@Comment("通知价格阈值：低于该阈值通知，可以为空，为空则都会通知")
	private Long notifyThreshold;

}
