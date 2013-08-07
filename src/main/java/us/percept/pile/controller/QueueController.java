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
public class QueueController implements PaperSourceListener, PaperFetcherListener, PileViewListener {
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    private PileView pileView;

    private PaperStorage storage;
    private PaperSource  source;
    private PaperFetcher fetcher;

    public QueueController(PileView pileView,
                           PaperStorage storage,
                           PaperSource source,
                           PaperFetcher fetcher) {
        this.pileView = pileView;
        this.storage = storage;
        this.source = source;
        this.fetcher = fetcher;

        pileView.addListener(this);
        source.addListener(this);
        fetcher.addListener(this);
    }

    // App Delegate
    public void onLoad() {
        // Clear the pileview
        pileView.clearPapers();

        // Get the current queue papers from storage
        Collection<Paper> papers = storage.getQueue();
        logger.info("Loaded "+papers.size()+" papers from file");
        pileView.addPapers(papers);
    }

    // PileView Delegate
    public void onSearchRequested(String identifier) {
        // Get the Paper metadata
        source.requestPaper(identifier);
    }

    public void onPaperArchived(Paper paper) {
        storage.archivePaper(paper);
        storage.dequeuePaper(paper.getIdentifier());

        pileView.removePaper(paper);
    }

    public void onPaperOpened(Paper paper) {
        try {
            Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
        } catch (URISyntaxException | IOException e1) {
            logger.error("Bad URL ", e1);
        }
    }

    // PaperSource Delegate
    @Override public void onPaperReceived(Paper paper) {
        // Store the paper
        storage.enqueuePaper(paper);

        // Add this paper to the pileview
        pileView.addPaper(paper);

        logger.info("Paper "+paper.getIdentifier()+" is queued");

        fetcher.fetch(paper);
    }

    @Override public void onLookupFailure(String paper, Throwable cause) {
        logger.error("Failed to find paper "+paper, cause);
    }

    @Override public void onResultsReceived(Collection<Paper> papers) {
        // Will not be used
    }

    @Override public void onSearchFailure(String query, Throwable cause) {
        // Will not be used
    }

    // PaperFetcher Delegate
    @Override public void onPaperFetched(Paper paper) {
        // Update the storage entry
        storage.updatePaper(paper);
        logger.info("Paper "+paper.getIdentifier()+" storage entry updated");
    }

    @Override public void onFetchFailed(Paper paper, Throwable error) {
        logger.error("Failed to download paper "+paper.getFileLocation(), error);
    }
}
