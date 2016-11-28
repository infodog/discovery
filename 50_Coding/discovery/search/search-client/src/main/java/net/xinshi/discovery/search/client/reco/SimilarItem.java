package net.xinshi.discovery.search.client.reco;

public class SimilarItem {
	private String name;
	private float score;
	private int searches;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public int getSearches() {
		return searches;
	}
	public void setSearches(int searches) {
		this.searches = searches;
	}
}
