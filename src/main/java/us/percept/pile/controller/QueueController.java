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
public class QueueController implements PaperSourceListener, PaperFetcherListener{
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    private PileView pileView;

    private PaperStorage storage;
    private PaperSource  source;
    private PaperFetcher fetcher;

    public void onLoad() {
        // Clear the pileview
        pileView.clearPapers();

        // Get the current queue papers from storage
        Collection<Paper> papers = storage.getQueue();
        pileView.addPapers(papers);
    }

    public void onImportRequest() {
        // Show the import dialog
        // TODO
    }

    public void onIdImported(String identifier) {
        // Get the Paper metadata
        source.requestPaper(identifier);
    }


    public void onPaperArchived(Paper paper) {
        storage.archivePaper(paper);
        storage.dequeuePaper(paper.getIdentifier());
    }

    public void onPaperOpened(Paper paper) {
        try {
            Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
        } catch (URISyntaxException | IOException e1) {
            logger.error("Bad URL ", e1);
        }
    }

    @Override public void onPaperReceived(Paper paper) {
        // Store the paper
        storage.enqueuePaper(paper);

        // Add this paper to the pileview
        pileView.addPaper(paper);

        logger.info("Paper "+paper.getIdentifier()+" is queued");
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

    @Override public void onPaperFetched(Paper paper) {
        // Update the storage entry
        storage.updatePaper(paper);
        logger.info("Paper "+paper.getIdentifier()+" storage entry updated");
    }

    @Override public void onFetchFailed(Paper paper, Throwable error) {
        logger.error("Failed to download paper "+paper.getIdentifier(), error);
    }
}
