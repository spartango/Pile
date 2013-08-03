package us.percept.pile.repo;

import us.percept.pile.model.Paper;

import java.util.Collection;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:28 PM
 */
public interface PaperSource {
    public Paper getPaper(String identifier);
    public Collection<Paper> findPapers(String query);

    // Asynchronous requests
    public void requestPaper(String identifier);
    public void requestSearch(String query);

    public void addListener(PaperSourceListener listener);
    public void removeListener(PaperSourceListener listener);
}
