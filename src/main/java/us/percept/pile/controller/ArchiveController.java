package us.percept.pile.controller;

import us.percept.pile.model.Paper;
import us.percept.pile.view.PileViewListener;

/**
 * Author: spartango
 * Date: 8/8/13
 * Time: 1:25 PM.
 */
public class ArchiveController implements Controller, PileViewListener {
    @Override public void onLoad() {
        //TODO implement ArchiveController.onLoad
    }

    @Override public void onUnload() {
        //TODO implement ArchiveController.onUnload
    }

    @Override public void onSearchRequested(String query) {
        //TODO implement ArchiveController.onSearchRequested
    }

    @Override public void onPaperOpened(Paper paper) {
        //TODO implement ArchiveController.onPaperOpened
    }

    @Override public void onPaperArchived(Paper paper) {
        //TODO implement ArchiveController.onPaperArchived
    }
}
