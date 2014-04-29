package com.tapjoy.reach.paramslist;

import java.util.List;

public class ParamModel implements Comparable<ParamModel> {
	
	private Integer id;
	private String name;
	private String properName;
	private List<DependentModel> dependents;
	
	public ParamModel(Integer id, String name, String properName,
			List<DependentModel> dependents) {
		super();
		this.id = id;
		this.name = name;
		this.properName = properName;
		this.dependents = dependents;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperName() {
		return properName;
	}

	public void setProperName(String properName) {
		this.properName = properName;
	}

	public List<DependentModel> getDependents() {
		return dependents;
	}

	public void setDependents(List<DependentModel> dependents) {
		this.dependents = dependents;
	}

	@Override
	public int compareTo(ParamModel o) {
		return this.id.compareTo(o.id);
	}
	
	

}
