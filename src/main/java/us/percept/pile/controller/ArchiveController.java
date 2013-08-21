package us.percept.pile.controller;

import us.percept.pile.model.Paper;
import us.percept.pile.store.PaperIndex;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;

import java.util.Arrays;
import java.util.List;

/**
 * Author: spartango
 * Date: 8/8/13
 * Time: 1:25 PM.
 */
public class ArchiveController extends PileViewController {

    private static final String PLACEHOLDER_TEXT = "The archives contain papers that you've finished reading. \n"
                                                   + "To find an old paper, you can search for it using the box above. \n"
                                                   + "You can look for specific titles, authors, or keywords. \n"
                                                   + "All these papers are still available for you to peruse offline. \n";
    private PaperIndex  index;
    private List<Paper> lastResults;

    public ArchiveController(PileView pileView, PaperStorage paperStorage, PaperIndex index) {
        super(pileView, paperStorage);
        this.index = index;
        lastResults = null;
    }

    @Override public void onLoad() {
        super.onLoad();

        // Set the search action
        pileView.setSearchAction("Search");

        // No action
        pileView.setPaperAction("");

        // Clear the pileview
        pileView.clearPapers();

        if (lastResults != null && !lastResults.isEmpty()) {
            pileView.addPapers(lastResults);
        } else {
            // Show a placeholder card
            showPlaceholder();
        }
    }

    private void showPlaceholder() {
        Paper placeholder = new Paper();
        placeholder.setTitle("Archives");

        int archivedCount = storage.getArchived().size();
        placeholder.setAuthors(Arrays.asList((archivedCount == 0 ? "No" : archivedCount)
                                             + " Paper"
                                             + (archivedCount != 1 ? "s" : "")));

        placeholder.setSummary(PLACEHOLDER_TEXT);

        pileView.addPaper(placeholder);
    }

    @Override public void onSearchRequested(String query) {
        pileView.clearPapers();

        // Search the index
        lastResults = index.search(query);

        pileView.addPapers(lastResults);
    }

    @Override public void onPaperArchived(Paper paper) {
        // Ignore
    }
}
