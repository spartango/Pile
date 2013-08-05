package us.percept.pile.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Author: spartango
 * Date: 8/5/13
 * Time: 2:56 AM.
 */
public class PaperView extends JPanel implements ListCellRenderer {
    private static final Logger logger = LoggerFactory.getLogger(PaperView.class);

    private JLabel    titleField;
    private JLabel    authorsField;
    private JTextArea summaryArea;
    private JLabel    dateField;
    private JLabel    pdfLabel;

    @Override public Component getListCellRendererComponent(JList list,
                                                            Object value,
                                                            int index,
                                                            boolean isSelected,
                                                            boolean cellHasFocus) {
        // Value should be a paper
        if (value instanceof Paper) {
            Paper paper = (Paper) value;
            titleField.setText(paper.getTitle());

            StringBuilder authorString = new StringBuilder();
            java.util.List<String> authors = paper.getAuthors();

            authorString.append("<html>");
            // Get the last author and guard against empty authors
            String lastAuthor = authors.size() > 0 ? authors.get(authors.size() - 1) : authors.get(0);
            for (String author : authors) {
                authorString.append(author);
                if (author != lastAuthor) {
                    authorString.append(", ");
                }
            }
            authorString.append("</html>");

            authorsField.setText(authorString.toString());
            summaryArea.setText(paper.getSummary());

            DateFormat df = new SimpleDateFormat("MMM yyy");
            String date = df.format(paper.getDate());
            dateField.setText(date);
            pdfLabel.setToolTipText(paper.getFileLocation());
        }

        Border divider = BorderFactory.createMatteBorder(0, 1, 20, 1, SystemColor.lightGray);
        if (isSelected) {
            this.setBorder(BorderFactory.createCompoundBorder(divider, BorderFactory.createMatteBorder(1,
                                                                                                       10,
                                                                                                       1,
                                                                                                       1,
                                                                                                       SystemColor.textHighlight)
            ));
        } else {
            this.setBorder(divider);
        }

        // Populate the fields from the paper
        return this;
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        this.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.setBackground(new Color(-1));
        titleField = new JLabel();
        titleField.setFont(new Font(titleField.getFont().getName(), Font.BOLD, 16));
        titleField.setText("Title");
        titleField.setVerticalAlignment(0);
        this.add(titleField,
                 new com.intellij.uiDesigner.core.GridConstraints(0,
                                                                  0,
                                                                  1,
                                                                  1,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                  null,
                                                                  new Dimension(430, 20),
                                                                  null,
                                                                  1,
                                                                  false));
        authorsField = new JLabel();
        authorsField.setBackground(new Color(-1));
        authorsField.setText("Authors");
        this.add(authorsField,
                 new com.intellij.uiDesigner.core.GridConstraints(1,
                                                                  0,
                                                                  1,
                                                                  1,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                  1,
                                                                  null,
                                                                  new Dimension(430, 16),
                                                                  null,
                                                                  1,
                                                                  false));
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setText("This is a summary of the article presented here.");
        dateField = new JLabel();
        dateField.setEnabled(false);
        dateField.setFont(new Font(dateField.getFont().getName(), Font.ITALIC, dateField.getFont().getSize()));
        dateField.setText("Jan 2013");
        this.add(dateField,
                 new com.intellij.uiDesigner.core.GridConstraints(0,
                                                                  1,
                                                                  1,
                                                                  1,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                                                                  1,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  1,
                                                                  false));
        pdfLabel = new JLabel();
        pdfLabel.setForeground(UIManager.getColor("controlHighlight"));
        pdfLabel.setText("PDF");
        this.add(pdfLabel,
                 new com.intellij.uiDesigner.core.GridConstraints(1,
                                                                  1,
                                                                  1,
                                                                  1,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  1,
                                                                  false));
        final JSeparator separator1 = new JSeparator();
        this.add(separator1,
                 new com.intellij.uiDesigner.core.GridConstraints(2,
                                                                  0,
                                                                  1,
                                                                  2,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  0,
                                                                  false));
        summaryArea = new JTextArea();
        this.add(summaryArea,
                 new com.intellij.uiDesigner.core.GridConstraints(3,
                                                                  0,
                                                                  1,
                                                                  2,
                                                                  com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                                                                  com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                  com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  1,
                                                                  false));
    }
}
