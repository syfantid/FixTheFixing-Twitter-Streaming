import twitter4j.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

/**
 * Main class that combines the gatherer and the writer class
 */
public class GathererTask implements Runnable {
    private final BlockingQueue<Tweet> tweets;
    // The keywords used to retrieve tweets
    private static ArrayList<String> keywords;
    private TwitterStream twitterStream;

    static {
        // Initialize keywords
        Path path = Paths.get("input//keywords.txt");
        keywords = new ArrayList<>();
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(keyword -> {
                if(!keyword.equals("")) {
                    keywords.add(keyword);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GathererTask(BlockingQueue<Tweet> tweets) {
        this.tweets = tweets;
        // Initialize Twitter Stream
        twitterStream =  new TwitterStreamFactory().getInstance();
    }

    @Override
    public void run() {
        StatusListener listener = new StatusListener(){
            public void onStatus(Status status) {
                try {
                    tweets.put(new Tweet(status.getText(),status.getCreatedAt(),status.getUser().getName(),status.getHashtagEntities()));
                } catch (InterruptedException e) {
                    //Thread.currentThread().interrupt();
                    System.out.println("GathererTask Interrupted");
                    e.printStackTrace();
                }
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        FilterQuery tweetFilterQuery = new FilterQuery(); // Create a filter
        String[] keywordsFilter = keywords.toArray(new String[keywords.size()]);
        tweetFilterQuery.track(keywordsFilter); // Define the filter (match fixing related keywords)
        tweetFilterQuery.language(new String[]{"en"}); // Keep only tweets in English
        twitterStream.filter(tweetFilterQuery); // Apply filter
    }
}
