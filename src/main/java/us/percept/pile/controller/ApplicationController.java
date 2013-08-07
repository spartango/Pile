package us.percept.pile.controller;

import us.percept.pile.repo.ArxivSource;
import us.percept.pile.repo.PaperSource;
import us.percept.pile.store.PaperFetcher;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.ModeView;
import us.percept.pile.view.ModeViewListener;
import us.percept.pile.view.PileView;

import javax.swing.*;
import java.awt.*;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:05 PM.
 */
public class ApplicationController implements Controller, ModeViewListener {
    // Controllers
    private QueueController queueController;

    private Controller activeController;

    // Views
    private PileView pileView;
    private ModeView modeView;
    private JFrame   frame;

    // Model facilities
    private PaperSource  paperSource;
    private PaperFetcher paperFetcher;
    private PaperStorage paperStorage;

    public void onLoad() {
        // Setup the model facilities
        paperSource = new ArxivSource();
        paperFetcher = new PaperFetcher("/tmp/papers");
        paperStorage = new PaperStorage();

        // Setup the views
        pileView = new PileView();
        modeView = new ModeView();

        // Setup the controllers
        queueController = new QueueController(pileView, paperStorage, paperSource, paperFetcher);

        // Listen to mode transitions
        modeView.addListener(this);

        // Setup the frame
        JFrame frame = new JFrame("Pile");

        // TODO add the mode view
        pileView.add(modeView, new com.intellij.uiDesigner.core.GridConstraints(2,
                                                                                0,
                                                                                1,
                                                                                2,
                                                                                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                                                                                com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                                                                                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                                null,
                                                                                new Dimension(150, -1),
                                                                                null,
                                                                                0,
                                                                                false));

        frame.add(pileView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setVisible(true);

        // Default controller
        activeController = queueController;
        queueController.onLoad();
    }

    @Override public void onUnload() {
        //TODO implement ApplicationController.onUnload
    }

    @Override public void onExploreMode() {
        // Unload the current Controller
        activeController.onUnload();

        // TODO Load the explore controller
        activeController = queueController;
        queueController.onLoad();
    }

    @Override public void onQueueMode() {
        // Unload the active controller
        activeController.onUnload();

        // Load the queue controller
        activeController = queueController;
        queueController.onLoad();
    }

    @Override public void onArchiveMode() {
        activeController.onUnload();

        // TODO Load the archive controller
        activeController = queueController;
        queueController.onLoad();
    }

}
