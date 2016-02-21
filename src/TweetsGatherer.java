import java.io.File;
import java.util.concurrent.*;

/**
 * Class that gathers tweets using Tweeter Streaming API
 */
public class TweetsGatherer {

    public static void main(String[] args) {

        // Delete CSV file if it exists already
        File file = new File("out.csv");
        file.delete();

        final int threadCount = 2;

        // BlockingQueue with a capacity of 200
        BlockingQueue<Tweet> tweets = new ArrayBlockingQueue<>(200);

        // create thread pool with given size
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        try {
            service.submit(new GathererTask(tweets)).get(2,TimeUnit.MINUTES);
        } catch (Exception e) {}

        // Wait til GathererTask completes
        try {
            service.submit(new FileTask(tweets)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        service.shutdownNow();

       try {
            service.awaitTermination(365, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
