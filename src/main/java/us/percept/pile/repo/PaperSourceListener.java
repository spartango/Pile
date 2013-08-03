package us.percept.pile.repo;

import us.percept.pile.model.Paper;

import java.util.Collection;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 7:19 PM
 */
public interface PaperSourceListener {
    public void onPaperReceived(Paper paper);
    public void onResultsReceived(Collection<Paper> papers);

    public void onLookupFailure(String paper, Throwable cause);
    public void onSearchFailure(String query, Throwable cause);
}
