package org.activityinfo.geoadmin.model;

import java.util.List;

public class AdminLevel {
	private int id;
	private Integer parentId;
	private String name;
	private List<AdminUnit> entities;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public boolean isRoot() {
		return parentId == null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<AdminUnit> getEntities() {
		return entities;
	}

	public void setEntities(List<AdminUnit> entities) {
		this.entities = entities;
	}

	@Override
	public String toString() {
		return name;
	}

}
