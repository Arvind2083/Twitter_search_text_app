/**
 * 
 */
package com.interview.domain.twitter;

/**
 * This class contains User level details for the twitter messages.
 * 
 * @author Arvind
 * 
 */
public class UserDetails {

	private long userID;

	private String creationDate;

	private String userName;

	private String screenName;

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

}
