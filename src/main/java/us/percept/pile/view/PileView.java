package us.percept.pile.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Author: spartango
 * Date: 8/4/13
 * Time: 10:04 PM.
 */
public class PileView extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(PileView.class);

    private JTextField  searchField;
    private JScrollPane listScrollPane;
    private JList       paperList;

    private void createUIComponents() {
        searchField = new JTextField();
        listScrollPane = new JScrollPane();
        paperList = new JList();
        paperList.setCellRenderer(new PaperView());

        paperList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Paper paper = (Paper) paperList.getSelectedValue();
                    try {
                        Desktop.getDesktop().browse(new URL(paper.getFileLocation()).toURI());
                    } catch (URISyntaxException | IOException e1) {
                        logger.error("Bad URL ", e1);
                    }
                }
            }
        });
    }

    public void setListData(Object[] listData) {
        paperList.setListData(listData);
    }

    public void addSearchListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }

    public void removeSearchListener(ActionListener listener) {
        searchField.removeActionListener(listener);
    }

    {
        // GUI Initializer
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        createUIComponents();
        this.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        searchField = new JTextField();
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
        listScrollPane = new JScrollPane();
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
