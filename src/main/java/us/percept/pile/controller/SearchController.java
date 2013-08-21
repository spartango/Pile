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

import java.util.Arrays;
import java.util.Collection;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:06 PM.
 */
public class SearchController extends PileViewController implements PaperSourceListener, PaperFetcherListener {
    private static final Logger logger                 = LoggerFactory.getLogger(SearchController.class);
    private static final String PLACEHOLDER_ID         = "PLACEHOLDER";
    private static final String ARXIV_PLACEHOLDER_TEXT =
            "Find new papers to read by searching arXiv using the box above. \n"
            + "ArXiv contains over 800,000 manuscripts in Physics, Mathematics, \n"
            + "Computer Science, Quantitative Biology, Quantitative Finance and Statistics. \n"
            + "You can look for papers containing specific titles, authors, or keywords. \n"
            + "When you find a paper you'd like to read, click Save to put it in your inbox. \n"
            + "You saved papers can be read offline, anytime. \n";
    private static final String ARXIV_SUBTITLE_TEXT    = "Search arXiv for publications";

    private PaperSource  source;
    private PaperFetcher fetcher;

    private Collection<Paper> lastResults;

    public SearchController(PileView pileView,
                            PaperStorage storage,
                            PaperSource source,
                            PaperFetcher fetcher) {
        super(pileView, storage);
        this.storage = storage;
        this.source = source;
        this.fetcher = fetcher;

        lastResults = null;
    }

    @Override public void onLoad() {
        super.onLoad();
        source.addListener(this);
        fetcher.addListener(this);

        // Set the search action
        pileView.setSearchAction("Search");
        pileView.setPaperAction("Save");

        // Clear the pileview
        pileView.clearPapers();

        if (lastResults != null && !lastResults.isEmpty()) {
            pileView.addPapers(lastResults);
        } else {
            // Show a placeholder card
            showPlaceholder();
        }
    }


    @Override public void onUnload() {
        super.onUnload();
        source.removeListener(this);
        fetcher.removeListener(this);
    }

    @Override public void onSearchRequested(String query) {
        pileView.clearPapers();
        source.requestSearch(query);
    }

    private void showPlaceholder() {
        Paper placeholder = new Paper();
        placeholder.setTitle("Explore");

        placeholder.setAuthors(Arrays.asList(ARXIV_SUBTITLE_TEXT));

        placeholder.setSummary(ARXIV_PLACEHOLDER_TEXT);
        placeholder.setIdentifier(PLACEHOLDER_ID);
        pileView.addPaper(placeholder);
    }

    @Override public void onPaperArchived(Paper paper) {
        if (paper.getIdentifier().equals(PLACEHOLDER_ID)) {
            return;
        }

        logger.info("Paper enqueued " + paper.getIdentifier());
        // Save this paper to the queue
        storage.enqueuePaper(paper);

        // Fetch the paper
        fetcher.fetch(paper);

        // Remove this paper from the search
        lastResults.remove(paper);
        pileView.removePaper(paper);
    }

    @Override public void onPaperReceived(Paper paper) {
        // Ignore
    }

    @Override public void onResultsReceived(String query, Collection<Paper> papers) {
        lastResults = papers;
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
