import us.percept.pile.model.Paper;
import us.percept.pile.view.PileView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Author: spartango
 * Date: 8/5/13
 * Time: 3:08 AM.
 */
public class PileViewTest {
    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Pile");
        PileView pileView = new PileView();
        frame.add(pileView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setVisible(true);

        // Test adding some data
        Paper first = new Paper("1",
                                "Title of a First Paper",
                                Arrays.asList("Major Author"),
                                "This Paper was written by a major author a long time ago",
                                new Date(),
                                "none");
        Paper second = new Paper("2",
                                 "Title of a Second Paper",
                                 Arrays.asList("Minor Author"),
                                 "This paper was written by a fool who could hardly spell",
                                 new Date(),
                                 "none");

        ArrayList<Paper> papers = new ArrayList<>();
        for(int i=0; i<100; i++){
            papers.add(i % 2 == 0? first : second);
        }

        pileView.setListData(papers.toArray());

        Thread.sleep(60000);
    }
}
