package us.percept.pile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;
import us.percept.pile.store.PaperStorage;
import us.percept.pile.view.PileView;
import us.percept.pile.view.PileViewListener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Author: spartango
 * Date: 8/8/13
 * Time: 3:16 PM.
 */
public abstract class PileViewController implements Controller, PileViewListener {
    private static final Logger logger = LoggerFactory.getLogger(PileViewController.class);

    protected PileView     pileView;
    protected PaperStorage storage;

    protected PileViewController(PileView pileView, PaperStorage paperStorage) {
        this.pileView = pileView;
        this.storage = paperStorage;
    }

    public void onLoad() {
        pileView.addListener(this);
    }

    public void onUnload() {
        pileView.removeListener(this);
    }

    public void onPaperOpened(Paper paper) {
        try {
            Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
        } catch (URISyntaxException | IOException e1) {
            logger.error("Bad URL ", e1);
        }
    }


    @Override public void onPaperMenu(Paper paper) {
        // Open dialog to attach the paper
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(pileView);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            logger.info("Attaching: " + file.getAbsolutePath() + " to " + paper);
            paper.setFileLocation("file:///" + file.getAbsolutePath());
        } else {
            logger.warn("Attach cancelled");
        }
    }
}
