/**
 * 
 */
package com.interview.domain.twitter;


/**
 * This class contains messages level details for the twitter messages.
 * 
 * @author Arvind
 * 
 */
public class Message {

	private long messageID;
	private String creationDate;
	private String text;
	private UserDetails author;

	public long getMessageID() {
		return messageID;
	}

	public void setMessageID(long messageID) {
		this.messageID = messageID;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserDetails getAuthor() {
		return author;
	}

	public void setAuthor(UserDetails author) {
		this.author = author;
	}

}
