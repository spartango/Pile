package us.percept.pile.controller;

import us.percept.pile.model.Paper;
import us.percept.pile.store.PaperIndex;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;

import java.util.List;

/**
 * Author: spartango
 * Date: 8/8/13
 * Time: 1:25 PM.
 */
public class ArchiveController extends PileViewController {

    private PaperIndex        index;
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

        if(lastResults != null && !lastResults.isEmpty()) {
            pileView.addPapers(lastResults);
        }
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
