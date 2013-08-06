package us.percept.pile.view.util;

import javax.swing.plaf.basic.BasicListUI;

/**
 * Author: spartango
 * Date: 8/5/13
 * Time: 3:49 PM.
 */
public class ListView extends BasicListUI {
    public void refreshLayout() {
        updateLayoutStateNeeded = 1;
    }

}
