package us.percept.pile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.PaperSource;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.store.PaperFetcher;
import us.percept.pile.store.PaperFetcherListener;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;
import us.percept.pile.view.PileViewListener;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:06 PM.
 */
public class SearchController implements Controller, PileViewListener, PaperSourceListener, PaperFetcherListener {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private PileView pileView;

    private PaperStorage storage;
    private PaperSource  source;
    private PaperFetcher fetcher;

    public SearchController(PileView pileView,
                            PaperStorage storage,
                            PaperSource source,
                            PaperFetcher fetcher) {
        this.pileView = pileView;
        this.storage = storage;
        this.source = source;
        this.fetcher = fetcher;
    }

    @Override public void onLoad() {
        pileView.addListener(this);
        source.addListener(this);
        fetcher.addListener(this);

        // Set the search action
        pileView.setSearchAction("Search");
        pileView.setPaperAction("Save");

        // Clear the pileview
        pileView.clearPapers();
    }

    @Override public void onUnload() {
        pileView.removeListener(this);
        source.removeListener(this);
        fetcher.removeListener(this);
    }

    @Override public void onSearchRequested(String query) {
        source.requestSearch(query);
    }

    @Override public void onPaperOpened(Paper paper) {
        try {
            Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
        } catch (URISyntaxException | IOException e1) {
            logger.error("Bad URL ", e1);
        }
    }

    @Override public void onPaperArchived(Paper paper) {
        logger.info("Paper enqueued " + paper.getIdentifier());
        // Save this paper to the queue
        storage.enqueuePaper(paper);

        // Fetch the paper
        fetcher.fetch(paper);
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
        logger.error("Search for " + query + " failed", cause);
    }

    // PaperFetcher Delegate
    @Override public void onPaperFetched(Paper paper) {
        // Update the storage entry
        storage.updatePaper(paper);
        logger.info("Paper " + paper.getIdentifier() + " storage entry updated");
    }

    @Override public void onFetchFailed(Paper paper, Throwable error) {
        logger.error("Failed to download paper " + paper.getFileLocation(), error);
    }
}
