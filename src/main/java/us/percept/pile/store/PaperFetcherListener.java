package us.percept.pile.store;

import us.percept.pile.model.Paper;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 4:40 PM.
 */
public interface PaperFetcherListener {
    public void onPaperFetched(Paper paper);

    public void onFetchFailed(Paper paper, Throwable error);

}
