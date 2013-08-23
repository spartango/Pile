package us.percept.pile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.PaperSource;
import us.percept.pile.repo.PaperSourceListener;
import us.percept.pile.store.PaperFetcher;
import us.percept.pile.store.PaperFetcherListener;
import us.percept.pile.store.PaperIndex;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:06 PM.
 */
public class QueueController extends PileViewController implements PaperSourceListener, PaperFetcherListener {
    private static final Logger logger         = LoggerFactory.getLogger(QueueController.class);
    private static final String PLACEHOLDER_ID = "PLACEHOLDER";

    private static final String PLACEHOLDER_TEXT =
            "Your inbox holds on to papers you'd like to read or are currently reading. \n"
            + "These papers are downloaded for you, so you can read them offline. \n"
            + "You can add a paper to the inbox by putting a link to its page in the box above. \n"
            + "Alternatively, you can use the Explore tab to search for new papers. \n"
            + "When you're done reading a paper, click Archive to move it to The Archives.  \n";

    private static final String ARXIV_URL_PREFIX  = "http://arxiv.org/abs/";
    private static final String PUBMED_URL_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

    private PaperSource  source;
    private PaperFetcher fetcher;
    private PaperIndex   index;

    public QueueController(PileView pileView,
                           PaperStorage storage,
                           PaperSource source,
                           PaperFetcher fetcher, PaperIndex index) {
        super(pileView, storage);
        this.source = source;
        this.fetcher = fetcher;
        this.index = index;
    }

    // App Delegate
    @Override public void onLoad() {
        super.onLoad();
        source.addListener(this);
        fetcher.addListener(this);

        // Set the search action
        pileView.setSearchAction("Import");
        pileView.setPaperAction("Archive");
        pileView.setPaperPopup("Attach...");

        // Clear the pileview
        pileView.clearPapers();

        // Get the current queue papers from storage
        Collection<Paper> papers = storage.getQueue();
        if (!papers.isEmpty()) {
            logger.info("Loaded " + papers.size() + " papers from file");
            pileView.addPapers(papers);
        } else {
            // Show a placeholder card
            showPlaceholder();
        }
    }

    private void showPlaceholder() {
        Paper placeholder = new Paper();
        placeholder.setTitle("Inbox");

        placeholder.setAuthors(Arrays.asList("Your inbox is empty"));

        placeholder.setSummary(PLACEHOLDER_TEXT);

        placeholder.setIdentifier(PLACEHOLDER_ID);

        pileView.addPaper(placeholder);
    }

    @Override public void onUnload() {
        super.onUnload();
        source.removeListener(this);
        fetcher.removeListener(this);
    }

    // PileView Delegate
    @Override public void onSearchRequested(String query) {
        String identifier = query;

        // Check if this is an arxiv URL
        if (query.startsWith(ARXIV_URL_PREFIX)) {
            // Strip off the URL pieces
            identifier = query.replaceFirst(Matcher.quoteReplacement(ARXIV_URL_PREFIX), "");
        } else if (query.startsWith(PUBMED_URL_PREFIX)) {
            identifier = query.replaceFirst(Matcher.quoteReplacement(PUBMED_URL_PREFIX), "");
        }

        // Get the Paper metadata
        source.requestPaper(identifier);
    }

    @Override public void onPaperArchived(Paper paper) {
        if (paper.getIdentifier().equals(PLACEHOLDER_ID)) {
            return;
        }

        // Add the paper to the index
        index.addPaper(paper);
        storage.archivePaper(paper);

        // Remove it from the view
        storage.dequeuePaper(paper.getIdentifier());
        pileView.removePaper(paper);

        if (storage.getQueue().isEmpty()) {
            showPlaceholder();
        }
    }

    // PaperSource Delegate
    @Override public void onPaperReceived(Paper paper) {
        // Store the paper
        storage.enqueuePaper(paper);

        // Add this paper to the pileview
        pileView.addPaper(paper);

        logger.info("Paper " + paper.getIdentifier() + " is queued");

        fetcher.fetch(paper);
    }

    @Override public void onLookupFailure(String paper, Throwable cause) {
        logger.error("Failed to find paper " + paper, cause);
    }

    @Override public void onResultsReceived(String query, Collection<Paper> papers) {
        // Will not be used
    }

    @Override public void onSearchFailure(String query, Throwable cause) {
        // Will not be used
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
