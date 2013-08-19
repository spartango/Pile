package us.percept.pile.repo;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:28 PM
 */
public interface PaperSource {
    // Asynchronous requests
    public void requestPaper(String identifier);

    public void requestSearch(String query);

    public void addListener(PaperSourceListener listener);

    public void removeListener(PaperSourceListener listener);
}
