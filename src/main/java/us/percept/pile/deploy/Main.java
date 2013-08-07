package us.percept.pile.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.controller.ApplicationController;

/**
 * Author: spartango
 * Date: 8/7/13
 * Time: 12:19 AM.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static ApplicationController application;

    public static void main(String[] args) {
        application = new ApplicationController();
        application.onLoad();

        try {
            synchronized (application) {
                application.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
