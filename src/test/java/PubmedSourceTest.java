import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.repo.pubmed.PubmedSource;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * Author: spartango
 * Date: 8/21/13
 * Time: 1:12 AM.
 */
public class PubmedSourceTest {
    private static final Logger logger = LoggerFactory.getLogger(PubmedSourceTest.class);

    public static void main(String[] args) {
        PubmedSource source = new PubmedSource();

        final CountDownLatch counter = new CountDownLatch(1);

        source.addListener(new PaperSourceListener() {
            @Override public void onPaperReceived(Paper paper) {
                logger.info("Received paper: " + paper);
                counter.countDown();
            }

            @Override public void onResultsReceived(String query, Collection<Paper> papers) {
                logger.info("Received results: " + papers);
                counter.countDown();
            }

            @Override public void onLookupFailure(String paper, Throwable cause) {
                logger.error("Failed to lookup paper, " + paper, cause);
                counter.countDown();
            }

            @Override public void onSearchFailure(String query, Throwable cause) {
                logger.error("Failed to search for " + query, cause);
                counter.countDown();
            }
        });

        source.requestPaper("11748933");

        try {
            counter.await();
        } catch (InterruptedException e) {
            logger.error("Wait interrupted by ", e);
        }

    }

}
