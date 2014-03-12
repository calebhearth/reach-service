package com.tapjoy.reach.counts;

public class CountsModel {
	private int udids_count;
	private int impressions_count;
	
	public CountsModel(int udidsCount, int offerwallImpCount){
		this.udids_count = udidsCount;
		this.impressions_count = offerwallImpCount;
	}

	public int getUdids_count() {
		return udids_count;
	}

	public void setUdids_count(int udids_count) {
		this.udids_count = udids_count;
	}

	public int getImpressions_count() {
		return impressions_count;
	}

	public void setImpressions_count(int impressions_count) {
		this.impressions_count = impressions_count;
	}
	
	
}
