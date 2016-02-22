import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Class that writes info to csv file
 */
public class FileTask implements Runnable {
    private final BlockingQueue<Tweet> tweets;
    private static ArrayList<String> basicWords;
    StringBuilder sb;

    static {
        // Initialize basic words
        Path path = Paths.get("input//basicWords.txt");
        basicWords = new ArrayList<>();
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(basicWord -> {
                if(!basicWord.equals("")) {
                    basicWords.add(basicWord);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileTask(BlockingQueue<Tweet> tweets) {
        this.tweets = tweets;
        sb = new StringBuilder();
        // Appending headers
        sb.append("text");
        sb.append(",");
        sb.append("timestamp");
        sb.append(",");
        sb.append("user");
        sb.append(",");
        sb.append("hashtags");
        sb.append("\n");
    }

    public boolean containsBasicWord(String tweetText) {
        for(String word : basicWords) {
            String pattern = "\\b"+word+"\\b"; // \b Matches a word boundary where a word character is [a-zA-Z0-9_].
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(tweetText); // Checks if pattern/word appears in tweet text (exact much, no substring)
            if(m.find()) {
                return true;
            }
        }
        return false;
    }

   /* private void processTweet(Tweet tweet) {
        if(containsBasicWord(tweet.getTweetString())) {
            System.out.println();
            System.out.println("Tweet: " + tweet.getTweetString());
            System.out.println();
            sb.append(tweet.getTweetString());
            sb.append(',');
            sb.append(tweet.getTimestamp().toString());
            sb.append(',');
            sb.append(tweet.getPoster());
            sb.append(',');
            sb.append(tweet.getHashtags());
            sb.append('\n');
        }
    }*/

    private String processTweetString(String tweetText) {
        String processedTweet = tweetText.toLowerCase();
        /* Remove URLs */
        String urlRegex = "https?://\\S+\\s?";
        processedTweet = processedTweet.replaceAll(urlRegex, " ");
        /* Remove mentions */
        String mentionRegex = "^@\\w+|\\s@\\w+";
        processedTweet= processedTweet.replaceAll(mentionRegex, " ");
        /* Remove numbers and punctuation */
        String punctuationRegex = "[^a-zA-Z ]";
        processedTweet = processedTweet.replaceAll(punctuationRegex," ");
        /* Remove multiple whitespaces */
        processedTweet = processedTweet.replaceAll("\\s+", " ");
        return processedTweet;
    }

    private void processTweet(Tweet tweet,PrintWriter out) {
        if(containsBasicWord(tweet.getTweetString())) {
            System.out.println();
            System.out.println("Tweet: " + tweet.getTweetString());
            System.out.println();
            String tweetString = processTweetString(tweet.getTweetString());
            out.print(tweetString);
            out.print(',');
            out.print(tweet.getTimestamp().toString());
            out.print(',');
            out.print(tweet.getPoster());
            out.print(',');
            out.print(tweet.getHashtags());
            out.print('\n');
        }
    }

    /*@Override
    public void run() {
        Tweet tweet;

        while(true) {
            try {
                // block if the queue is empty
                tweet = tweets.take();
                processTweet(tweet);

            } catch (InterruptedException ex) {
                break; // GathererTask has completed
            }
        }
        // poll() returns null if the queue is empty
        while((tweet = tweets.poll()) != null) {
            processTweet(tweet);
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("test.csv"));
            pw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
        System.out.println("Done!");
    }*/

    public void run() {
        Tweet tweet;
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.csv", true)))) {
            while(true) {
                try {
                    // block if the queue is empty
                    tweet = tweets.take();
                    processTweet(tweet,out);

                } catch (InterruptedException ex) {
                    System.out.println("************************************************************");
                    System.out.println("FileTask Interrupted");
                    System.out.println("************************************************************");
                    break; // GathererTask has completed
                }
            }
            // poll() returns null if the queue is empty
            while((tweet = tweets.poll()) != null) {
                processTweet(tweet,out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
