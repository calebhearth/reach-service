package com.tapjoy.reach.counts;

public class CountsModel {
	private int udids_count;
	private int impressions_count;
	private int unique_viewers_count;
	
	public CountsModel(int udidsCount, int offerwallImpCount, int uvCount){
		this.udids_count = udidsCount;
		this.impressions_count = offerwallImpCount;
		this.unique_viewers_count = uvCount;
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
	
	public int getUnique_viewers_count() {
		return unique_viewers_count;
	}

	public void setUnique_viewers_count(int unique_viewers_count) {
		this.unique_viewers_count = unique_viewers_count;
	}

	
	
}
