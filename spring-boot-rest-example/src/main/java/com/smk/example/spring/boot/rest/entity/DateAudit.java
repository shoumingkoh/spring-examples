package com.smk.example.spring.boot.rest.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class DateAudit {
	
	@NotNull
    @PastOrPresent
	@Setter(value = AccessLevel.NONE)
	@CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;


	@NotNull
    @PastOrPresent
	@Setter(value = AccessLevel.NONE)
    @LastModifiedDate
    private LocalDateTime updatedDate;
	
}
