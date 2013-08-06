package us.percept.pile.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * Author: spartango
 * Date: 8/4/13
 * Time: 10:04 PM.
 */
public class PileView extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(PileView.class);

    private JTextField  searchField;
    private JScrollPane listScrollPane;
    private JPanel       paperList;

    private void createUIComponents() {
        searchField = new JTextField();
        listScrollPane = new JScrollPane();
        paperList = new JPanel(new GridLayout(0,1));
    }
    public void addSearchListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }

    public void removeSearchListener(ActionListener listener) {
        searchField.removeActionListener(listener);
    }

    public void addPaper(Paper paper) {
        // Build a view for it
        PaperView view = new PaperView();
        view.setPaper(paper);
        paperList.add(view);
    }

    public void addPapers(Collection<Paper> papers) {
        for(Paper p : papers) {
            addPaper(p);
        }
    }

    public void clearPapers() {
        paperList.removeAll();
    }


    {
        // GUI Initializer
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        createUIComponents();
        this.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        this.add(searchField,
                   new com.intellij.uiDesigner.core.GridConstraints(0,
                                                                    0,
                                                                    1,
                                                                    1,
                                                                    com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                                                                    com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                                                                    com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                    com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                    null,
                                                                    new Dimension(150, -1),
                                                                    null,
                                                                    0,
                                                                    false));
        this.add(listScrollPane,
                   new com.intellij.uiDesigner.core.GridConstraints(1,
                                                                    0,
                                                                    1,
                                                                    1,
                                                                    com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                                                                    com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                                                                    com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                                                    | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                    com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                                                    | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    0,
                                                                    false));
        listScrollPane.setViewportView(paperList);
    }
}
