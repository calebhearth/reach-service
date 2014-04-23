package com.tapjoy.reach.counts;

public class CountsModel {
	private long udids_count;
	private long impressions_count;
	private long unique_viewers_count;
	
	public CountsModel(long udidsCount, long offerwallImpCount, long uvCount){
		this.udids_count = udidsCount;
		this.impressions_count = offerwallImpCount;
		this.unique_viewers_count = uvCount;
	}

	public long getUdids_count() {
		return udids_count;
	}

	public void setUdids_count(long udids_count) {
		this.udids_count = udids_count;
	}

	public long getImpressions_count() {
		return impressions_count;
	}

	public void setImpressions_count(long impressions_count) {
		this.impressions_count = impressions_count;
	}
	
	public long getUnique_viewers_count() {
		return unique_viewers_count;
	}

	public void setUnique_viewers_count(long unique_viewers_count) {
		this.unique_viewers_count = unique_viewers_count;
	}

	
	
}
