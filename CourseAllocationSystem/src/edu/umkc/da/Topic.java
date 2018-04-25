package edu.umkc.da;

public class Topic {
	private int topicID;
	private int topicName;
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public int getTopicName() {
		return topicName;
	}
	public void setTopicName(int topicName) {
		this.topicName = topicName;
	}
	
	@Override
	public int hashCode() {
		return this.topicID+this.topicName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(((Topic)obj).getTopicID() == this.topicID){
			return true;
		}
		return false;
	}
}
