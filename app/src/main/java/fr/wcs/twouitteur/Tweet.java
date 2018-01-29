package fr.wcs.twouitteur;

/**
 * Created by apprenti on 11/6/17.
 */

public class Tweet {

    private String tweetText;
    private String userName;
    private String userTweetId;
    private String tweetDate;

    public Tweet() {};

    public Tweet(String tweetText, String userName, String userTweetId, String tweetDate) {
        this.tweetText = tweetText;
        this.userName = userName;
        this.userTweetId = userTweetId;
        this.tweetDate = tweetDate;
    }

    public String getTweetText() {
        return tweetText;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserTweetId() {
        return userTweetId;
    }
    public String getTweetDate() {
        return tweetDate;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserTweetId(String userTweetId) {
        this.userTweetId = userTweetId;
    }
    public void setTweetDate(String tweetDate) {
        this.tweetDate = tweetDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (tweetText != null ? !tweetText.equals(tweet.tweetText) : tweet.tweetText != null)
            return false;
        if (userName != null ? !userName.equals(tweet.userName) : tweet.userName != null)
            return false;
        if (userTweetId != null ? !userTweetId.equals(tweet.userTweetId) : tweet.userTweetId != null)
            return false;
        return tweetDate != null ? tweetDate.equals(tweet.tweetDate) : tweet.tweetDate == null;
    }

    @Override
    public int hashCode() {
        int result = tweetText != null ? tweetText.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userTweetId != null ? userTweetId.hashCode() : 0);
        result = 31 * result + (tweetDate != null ? tweetDate.hashCode() : 0);
        return result;
    }
}
