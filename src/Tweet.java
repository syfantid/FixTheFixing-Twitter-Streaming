import twitter4j.HashtagEntity;

import java.util.Date;

/**
 * Class that represents a Tweet
 */
public class Tweet {

    private String tweetString;
    private Date timestamp;
    private String poster;
    private HashtagEntity[] hashtags;

    public Tweet(String tweetString, Date timestamp, String poster, HashtagEntity[] hashtags) {

        this.tweetString = tweetString;
        this.timestamp = timestamp;
        this.poster = poster;
        this.hashtags = hashtags;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPoster() {
        return poster;
    }

    public String getHashtags() {
        StringBuilder tags = new StringBuilder();
        for(HashtagEntity h : hashtags) {
            tags.append(h.getText());
            tags.append(" ");
        }
        return tags.toString();
    }

    public String getTweetString() {

        return tweetString;
    }
}
