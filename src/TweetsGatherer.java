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

        Future<?> f = service.submit(new GathererTask(tweets));
        try {
            f.get(1,TimeUnit.MINUTES); // Give specific time to the GathererTask
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            f.cancel(true); // Stop the Gatherer
            System.out.println("***********************************************************************************");
            System.out.println("1");
            System.out.println("***********************************************************************************");
        }


        try {
            service.submit(new FileTask(tweets)).get(); // Wait til FileTask completes
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        service.shutdownNow();

       try {
            service.awaitTermination(7, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
