package us.percept.pile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.PaperSource;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;
import us.percept.pile.view.PileViewListener;

import java.util.Collection;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:06 PM.
 */
public class SearchController implements Controller, PileViewListener, PaperSourceListener {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private PileView pileView;

    private PaperStorage storage;
    private PaperSource  source;

    public SearchController(PileView pileView,
                            PaperStorage storage,
                            PaperSource source) {
        this.pileView = pileView;
        this.storage = storage;
        this.source = source;
    }

    @Override public void onLoad() {
        pileView.addListener(this);
        source.addListener(this);

        // Set the search action
        pileView.setSearchAction("Search");
        pileView.setPaperAction("Save");

        // Clear the pileview
        pileView.clearPapers();
    }

    @Override public void onUnload() {
        pileView.removeListener(this);
        source.removeListener(this);
    }

    @Override public void onSearchRequested(String query) {
        source.requestSearch(query);
    }

    @Override public void onPaperOpened(Paper paper) {
        logger.info("Paper opened " + paper.getIdentifier());
    }

    @Override public void onPaperArchived(Paper paper) {
        logger.info("Paper archived " + paper.getIdentifier());
    }

    @Override public void onPaperReceived(Paper paper) {
        // Ignore
    }

    @Override public void onResultsReceived(Collection<Paper> papers) {
        pileView.addPapers(papers);
    }

    @Override public void onLookupFailure(String paper, Throwable cause) {
        // Ignore
    }

    @Override public void onSearchFailure(String query, Throwable cause) {
        logger.error("Search for "+query+" failed", cause);
    }
}
