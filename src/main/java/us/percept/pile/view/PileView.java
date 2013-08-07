package us.percept.pile.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Author: spartango
 * Date: 8/4/13
 * Time: 10:04 PM.
 */
public class PileView extends JPanel implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(PileView.class);

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            Font robotoRegular = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Roboto-Light.ttf"));
            Font robotoLight = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Roboto-Regular.ttf"));
            ge.registerFont(robotoLight);
            ge.registerFont(robotoRegular);
            logger.info("Fonts added: " + robotoLight.getName() + " & " + robotoRegular.getName());
        } catch (FontFormatException | IOException e) {
            logger.warn("Failed to load Roboto font");
        }
    }

    private JTextField  searchField;
    private JScrollPane listScrollPane;
    private JPanel      paperList;

    private List<Paper>            papers    = new LinkedList<>();
    private List<PileViewListener> listeners = new ArrayList<>(1);

    private void createUIComponents() {
        searchField = new JTextField();
        paperList = new JPanel(new GridBagLayout());
        listScrollPane = new JScrollPane();
    }

    public void addListener(PileViewListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PileViewListener listener) {
        listeners.remove(listener);
    }

    public void addPaper(Paper paper) {
        // Build a view for it
        PaperView view = new PaperView();
        view.setPaper(paper);
        view.addListeners(listeners);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = paperList.getComponentCount();

        paperList.add(view, constraints);
        Component spacer = addSpacer();
        Component[] added = {view, spacer};
        papers.add(paper);

        this.repaint();
    }

    public void removePaper(Paper paper) {
        papers.remove(paper);

        // Remove everything
        clearPapers();

        // Readd everything
        addPapers(papers);

        this.repaint();
    }

    private Component addSpacer() {
        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.gridx = 0;
        spacerConstraints.gridy = paperList.getComponentCount();

        Component spacer = Box.createVerticalStrut(18);
        paperList.add(spacer, spacerConstraints);

        return spacer;
    }

    public void addPapers(Collection<Paper> papers) {
        for (Paper p : papers) {
            addPaper(p);
        }
    }

    public void clearPapers() {
        paperList.removeAll();
        addSpacer();
    }

    public void notifySearchRequested(String query) {
        for (PileViewListener listener : listeners) {
            listener.onSearchRequested(query);
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        notifySearchRequested(e.getActionCommand());
    }

    {
        // GUI Initializer
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        createUIComponents();

        this.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        this.setBackground(new Color(238, 238, 238));
        searchField.setFont(new Font("Roboto Regular", Font.PLAIN, 13));
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.addActionListener(this);

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
        listScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        addSpacer();
    }


}
