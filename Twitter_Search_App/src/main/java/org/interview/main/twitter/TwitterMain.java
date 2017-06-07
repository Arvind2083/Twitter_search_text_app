package org.interview.main.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.interview.oauth.twitter.TwitterAuthenticationException;
import org.interview.oauth.twitter.TwitterAuthenticator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.interview.domain.twitter.Message;
import com.interview.domain.twitter.UserDetails;

/**
 * @author Arvind
 *
 */
public class TwitterMain {

	private static final String CONSUMER_KEY = "RLSrphihyR4G2UxvA0XBkLAdl";
	private static final String CONSUMER_SECRET = "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4";

	/** Static field for the Twitter details */
	private static final String USERID = "id";
	private static final String CREATED_AT = "created_at";
	private static final String NAME = "name";
	private static final String SCREEN_NAME = "screen_name";
	private static final String MESSAGE_CREATED_AT = "created_at";
	private static final String MESSAGEID = "id";
	private static final String TEXT = "text";
	private static final String USER = "user";

	/** Url for searching the word bieber */
	private static final String BASE_URL = "https://stream.twitter.com/1.1/statuses/filter.json?delimited=length&track=bieber";
	final static Logger loglogger = Logger.getLogger("tweetsLog");
	final static Logger statlogger = Logger.getLogger("statFile");

	/** Counter for Timer */
	static int counter = 1;

	public static void main(String[] args) {

		TwitterAuthenticator twitterAuthenticator = null;
		HttpRequestFactory httpRequestFactory = null;
		GenericUrl genericUrl = null;
		HttpResponse resp = null;
		BufferedReader reader = null;

		/**
		 * TreeMap maintains the sorting of messages based upon the key i.e
		 * Message UserId
		 */
		Map<Long, List<Message>> twitterMessage = new TreeMap<Long, List<Message>>();

		try {

			twitterAuthenticator = new TwitterAuthenticator(System.out,
					CONSUMER_KEY, CONSUMER_SECRET);

			httpRequestFactory = twitterAuthenticator
					.getAuthorizedHttpRequestFactory();

			genericUrl = new GenericUrl(BASE_URL);
			resp = httpRequestFactory.buildGetRequest(genericUrl).execute();

			reader = new BufferedReader(
					new InputStreamReader(resp.getContent()));

			/** Timer to check the statistics per minute */
			Timer timer = statisticsTimer(twitterMessage);

			/** check 30 sec duration */
			long endTime = System.currentTimeMillis() + 30000;

			/** check the count of 100 message. */
			int count = 1;
			while (reader.readLine() != null
					&& System.currentTimeMillis() < endTime) {

				/** Exit the loop if map contains distinct 100 messages */
				if (count > 100) {
					loglogger
							.info("Count is greater than 100. Program will exits "
									+ count);
					break;
				}
				/**
				 * Breaking the twitter stream if line returns more than 1
				 * twitter response in one line
				 */
				String[] output = reader.readLine().split("\r\n");
				for (String str : output) {

					/** Parse the messages details */
					JSONObject jsonObject = (JSONObject) new JSONParser()
							.parse(str);

					/** Get the user specific details */
					JSONObject userJsonObject = (JSONObject) jsonObject
							.get(USER);

					/** Populate the user details */
					UserDetails user = new UserDetails();
					user.setUserID(Long.valueOf(userJsonObject.get(USERID)
							.toString()));
					user.setCreationDate(userJsonObject.get(CREATED_AT)
							.toString());
					user.setUserName(userJsonObject.get(NAME).toString());
					user.setScreenName(userJsonObject.get(SCREEN_NAME)
							.toString());

					/** Populate the message details */
					Message message = new Message();
					message.setAuthor(user);
					message.setCreationDate(jsonObject.get(MESSAGE_CREATED_AT)
							.toString());
					message.setMessageID(Long.valueOf(jsonObject.get(MESSAGEID)
							.toString()));
					message.setText(jsonObject.get(TEXT).toString());

					twitterMessage = populateUserMap(twitterMessage, message,
							count);
				}
			}
			printMessagesGroupedbyUsers(twitterMessage);

			/** stop the timer after the main thread is stopped */
			timer.cancel();

		} catch (ParseException e) {
			loglogger.error("ParseException: " + e.getMessage(), e);
		} catch (TwitterAuthenticationException e) {
			loglogger.error(
					"TwitterAuthenticationException: " + e.getMessage(), e);
		} catch (IOException e) {
			loglogger.error("IOException: " + e.getMessage(), e);
		}
	}

	/**
	 * This is the timer checking Users and count of messages per second. Output
	 * of this message will be written in the file statFile.
	 */

	private static Timer statisticsTimer(
			final Map<Long, List<Message>> twitterMessage) {
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				statlogger.info("Twitter response Statistics in " + counter
						+ " sec : \n");
				if (twitterMessage.isEmpty()) {
					statlogger.info(" Count of Messages :" + 0);
				}
				for (Long userId : twitterMessage.keySet()) {

					statlogger.info("User Id :" + userId
							+ " Count of Messages Retrived:"
							+ twitterMessage.get(userId).size());
				}

				statlogger.info("End of Twitter Statistics in " + counter
						+ " sec : \n");
				counter++;
			}
		};

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(timerTask, 1, 1000);
		return timer;
	}

	/**
	 * This method creates Map will User id as key and list of corresponding
	 * messages as value This method will also ignore any duplicate messages
	 * from the twitter stream.
	 */
	public static Map<Long, List<Message>> populateUserMap(
			Map<Long, List<Message>> twitterMessage, Message message, int count) {
		/**
		 * Add the message details if map is empty.Flow will enter only first
		 * time.
		 */
		if (twitterMessage.isEmpty()) {
			List<Message> messageList = new ArrayList<Message>();
			messageList.add(message);
			twitterMessage.put(message.getAuthor().getUserID(), messageList);
			count++;
		} else {
			/** Create a new list if user Id doesn't exists */
			if (!(twitterMessage.containsKey(message.getAuthor().getUserID()))) {
				List<Message> messageList = new ArrayList<Message>();
				messageList.add(message);
				twitterMessage
						.put(message.getAuthor().getUserID(), messageList);
				count++;
			} else {
				/**
				 * If that particular User id exists then get the list from the
				 * map and add new object of message in the list
				 */
				List<Message> messageList = twitterMessage.get(message
						.getAuthor().getUserID());
				/**
				 * Check if this message id already exists in the map
				 */
				boolean checkDuplicate = false;
				for (Message checkMessage : messageList) {

					if (checkMessage.getMessageID() == message.getMessageID()) {
						checkDuplicate = true;
					}
				}
				/**
				 * Ignore the duplicate message from the streaming.
				 */
				if (!checkDuplicate) {
					messageList.add(message);
					twitterMessage.put(message.getAuthor().getUserID(),
							messageList);
					count++;
				}

			}
		}
		return twitterMessage;
	}

	/**
	 * This method is responsible to iterate through the User details and it's
	 * related messages. Output of the written to the log file tweetsLog.
	 */
	private static void printMessagesGroupedbyUsers(
			Map<Long, List<Message>> twitterMessage) {
		loglogger
				.info("====================Start of the User detail =======================================");
		for (Long userId : twitterMessage.keySet()) {

			/** Display the details of the user */
			List<Message> messageList = twitterMessage.get(userId);

			/** Display the first id for the User details */
			Message messageDetail = messageList.get(0);
			UserDetails userDetails = messageDetail.getAuthor();

			loglogger.info("\n\n UserName :" + userDetails.getUserName()
					+ "  UserID: " + userDetails.getUserID()
					+ " CreationDate: " + userDetails.getCreationDate()
					+ "  ScreenName: " + userDetails.getScreenName());
			loglogger
					.info("====================================================================================");
			for (Message message : messageList) {
				loglogger.info("MessageID :" + message.getMessageID()
						+ " CreationDate: " + message.getCreationDate()
						+ " Text:  " + message.getText());
			}

		}
		loglogger
				.info("=========================End of the User detail=======================================");
	}
}
