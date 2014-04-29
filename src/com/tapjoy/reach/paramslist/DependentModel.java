package com.tapjoy.reach.paramslist;

public class DependentModel implements Comparable<DependentModel> {
	
	private String dependentType;
	private Integer dependentId;
	
	public DependentModel(String dependentType, Integer dependentId) {
		super();
		this.dependentType = dependentType;
		this.dependentId = dependentId;
	}

	public String getDependentType() {
		return dependentType;
	}
	
	public void setDependentType(String dependentType) {
		this.dependentType = dependentType;
	}
	
	public Integer getDependentId() {
		return dependentId;
	}
	
	public void setDependentId(Integer dependentId) {
		this.dependentId = dependentId;
	}

	@Override
	public int compareTo(DependentModel o) {
		return dependentId.compareTo(o.dependentId);
	}
	
	

}
