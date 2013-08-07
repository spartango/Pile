package us.percept.pile.view;

import us.percept.pile.model.Paper;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 11:35 PM.
 */
public interface PaperViewListener {
    public void onPaperOpened(Paper paper);

    public void onPaperArchived(Paper paper);
}
