package us.percept.pile.controller;

import us.percept.pile.repo.ArxivSource;
import us.percept.pile.repo.PaperSource;
import us.percept.pile.store.PaperFetcher;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;

import javax.swing.*;

/**
 * Author: spartango
 * Date: 8/6/13
 * Time: 10:05 PM.
 */
public class ApplicationController {
    // Controllers
    private QueueController queueController;

    // Views
    private PileView pileView;
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

        // Setup the controllers
        queueController = new QueueController(pileView, paperStorage, paperSource, paperFetcher);

        // Setup the frame
        JFrame frame = new JFrame("Pile");
        frame.add(pileView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setVisible(true);

        queueController.onLoad();
    }
}
