import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.repo.arxiv.ArxivSource;
import us.percept.pile.view.PileView;
import us.percept.pile.view.PileViewListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

/**
 * Author: spartango
 * Date: 8/5/13
 * Time: 3:08 AM.
 */
public class PileViewTest {
    private static final Logger logger = LoggerFactory.getLogger(PileViewTest.class);

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Pile");
        final PileView pileView = new PileView();
        frame.add(pileView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setVisible(true);

        final ArxivSource source = new ArxivSource();
        source.addListener(new PaperSourceListener() {
            @Override public void onPaperReceived(Paper paper) {
                pileView.addPaper(paper);
            }

            @Override public void onResultsReceived(String query, Collection<Paper> papers) {
                pileView.clearPapers();
                pileView.addPapers(papers);
            }

            @Override public void onLookupFailure(String paper, Throwable cause) {
            }

            @Override public void onSearchFailure(String query, Throwable cause) {
            }
        });

        pileView.addListener(new PileViewListener() {
            @Override public void onSearchRequested(String query) {
                logger.info("Query: " + query);
                source.requestSearch(query);
            }

            @Override public void onPaperOpened(Paper paper) {
                logger.info("Paper open requested");
                try {
                    Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
                } catch (URISyntaxException | IOException e1) {
                    logger.error("Bad URL ", e1);
                }
            }

            @Override public void onPaperArchived(Paper paper) {
                logger.info("Paper archive requested");
            }
        });

        Thread.sleep(60000);
    }
}
