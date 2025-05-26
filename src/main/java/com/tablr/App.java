package com.tablr;

import com.tablr.controller.AppController;

/**
 * Entry point for the application.
 */
public class App {

    /**
     * The main method that serves as the entry point for the application.
     * It initializes the application by invoking the AppController.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            AppController appController = new AppController();
            appController.initializeApp();
        });
    }
}