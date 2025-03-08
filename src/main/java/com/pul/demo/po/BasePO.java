package com.pul.demo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldNameConstants
public abstract class BasePO<I extends Serializable> implements Serializable {

	private I id;

	/**
	 * 空接口方便代码生成器拼接
	 */
	public interface BaseEmptyPO {
	}

	public interface BaseCreateIdPO {
		String getCreateId();

		void setCreateId(String value);
	}

	public interface BaseCreateTimePO {
		LocalDateTime getCreateTime();

		void setCreateTime(LocalDateTime value);
	}

	public interface BaseUpdateIdPO {
		String getUpdateId();

		void setUpdateId(String value);
	}

	public interface BaseUpdateTimePO {
		LocalDateTime getUpdateTime();

		void setUpdateTime(LocalDateTime value);
	}

	public interface BaseIsDeletedPO {
		Boolean getIsDeleted();

		void setIsDeleted(Boolean value);
	}

	/**
	 * 常用的po类，有id、createId、updateId、createTime、updateTime
	 */
	@Getter
	@Setter
	@MappedSuperclass
	@FieldNameConstants
	public abstract static class CommonPO<I extends Serializable> extends BasePO<I>
			implements BaseCreateIdPO, BaseUpdateIdPO, BaseCreateTimePO, BaseUpdateTimePO {

		@Column(nullable = false)
		@CreatedBy
		@Comment("创建人主键id")
		private String createId;

		@Column(nullable = false)
		@LastModifiedBy
		@Comment("更新人主键id")
		private String updateId;

		@Column(nullable = false)
		@CreatedDate
		@Comment("创建时间")
		// 特殊情况需要序列化po对象，加一个注解兼容
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createTime;

		@Column(nullable = false)
		@LastModifiedDate
		@Comment("更新时间")
		// 特殊情况需要序列化po对象，加一个注解兼容
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime updateTime;
	}
}
