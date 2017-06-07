/**
 * 
 */
package com.interview.domain;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.interview.main.twitter.TwitterMain;
import org.junit.Before;
import org.junit.Test;

import com.interview.domain.twitter.Message;
import com.interview.domain.twitter.UserDetails;

/**
 * @author Arvind
 *
 */
public class TwitterMainTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * Test method for grouping the messages by used id
	 */
	@Test
	public void testGroupByUserId() {

		/** Check Messages are grouped by the user id */
		Map<Long, List<Message>> twitterMessage = new TreeMap<Long, List<Message>>();

		/** Initialize the value */
		int count = 0;

		UserDetails userdetailsOne = new UserDetails();

		Message messageOne = new Message();
		userdetailsOne.setUserID(1234);
		messageOne.setAuthor(userdetailsOne);
		messageOne.setText("User id is 1234 : This is first Messages");
		messageOne.setMessageID(100);

		TwitterMain.populateUserMap(twitterMessage, messageOne, count);

		Message messageTwo = new Message();
		userdetailsOne.setUserID(1234);
		messageTwo.setAuthor(userdetailsOne);
		messageTwo.setText("User id is 1234 : This is second messages");
		messageTwo.setMessageID(200);

		TwitterMain.populateUserMap(twitterMessage, messageTwo, count);

		/**
		 * Two messages were send to the method but since the user id is same
		 * for both the messages. Only one key will be available in the Map
		 */
		assertTrue(twitterMessage.size() == 1);

		/**
		 * Two messages were send to the method so value count of messages
		 * should be 2 in the value List.
		 */

		int CountOfList = 0;
		for (Long userId : twitterMessage.keySet()) {

			/** Display the details of the user */
			List<Message> messageList = twitterMessage.get(userId);

			/** Display the first id for the User details */
			Message messageDetail = messageList.get(0);
			UserDetails userDetails = messageDetail.getAuthor();

			assert (userDetails.getUserID() == 1234);
			for (Message message : messageList) {
				CountOfList++;
			}

		}

		assertTrue(CountOfList == 2);

	}

	/**
	 * Test for ignoring the messages having same messages id.
	 */
	@Test
	public void testIgnoreDuplicateMessageId() {
		/** Check Messages are grouped by the user id */
		Map<Long, List<Message>> twitterMessage = new TreeMap<Long, List<Message>>();

		/** Initialize the value */
		int count = 0;

		UserDetails userdetailsOne = new UserDetails();

		Message messageOne = new Message();
		userdetailsOne.setUserID(5678);
		messageOne.setAuthor(userdetailsOne);
		messageOne.setText("User id is 5678 : This is first Messages");
		messageOne.setMessageID(100);

		TwitterMain.populateUserMap(twitterMessage, messageOne, count);

		UserDetails userdetailsTwo = new UserDetails();

		Message messageTwo = new Message();
		userdetailsTwo.setUserID(5678);
		messageTwo.setAuthor(userdetailsTwo);
		messageTwo.setText("User id is 5678 : This is second messages");
		messageTwo.setMessageID(100);

		TwitterMain.populateUserMap(twitterMessage, messageTwo, count);

		/**
		 * The user id and message id for both the messages are same. So
		 * duplicate message should be ignored.
		 */

		int CountOfList = 0;
		for (Long userId : twitterMessage.keySet()) {

			/** Display the details of the user */
			List<Message> messageList = twitterMessage.get(userId);

			for (Message message : messageList) {

				CountOfList++;
			}
		}

		assertTrue(CountOfList == 1);
	}

}
