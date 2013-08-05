import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.ArxivSource;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.view.PileView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            }

            @Override public void onResultsReceived(Collection<Paper> papers) {
                pileView.setListData(papers.toArray());
            }

            @Override public void onLookupFailure(String paper, Throwable cause) {
            }

            @Override public void onSearchFailure(String query, Throwable cause) {
            }
        });

        pileView.addSearchListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                logger.info("Searching for "+e.getActionCommand());
                source.requestSearch(e.getActionCommand());
            }
        });

        Thread.sleep(60000);
    }
}
