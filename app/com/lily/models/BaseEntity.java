package com.lily.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class BaseEntity {

	public Integer id;

	@JsonIgnore
	@Column(name = "created")
	public Date createdAt;

	@JsonIgnore
	@Column(name = "last_modified")
	public Date lastModified;
}
