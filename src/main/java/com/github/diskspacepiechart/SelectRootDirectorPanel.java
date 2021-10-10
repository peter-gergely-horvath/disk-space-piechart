package com.github.diskspacepiechart;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SelectRootDirectorPanel extends SimpleGridBagJPanel {

    interface DirectorySelectedCallback {
        void onDirectorySelected(File directory);
    }

    private static final String DEFAULT_PATH = System.getProperty("user.home");

    private static final String SELECT_ANOTHER_LOCATION_BUTTON_TEXT = "Select another location";
    private static final String START_ANALYSIS_HERE_BUTTON_TEXT = "Start analysis here";

    private final JLabel pathSelectionJLabel;

    private final DirectorySelectedCallback directorySelectedCallback;

    SelectRootDirectorPanel(DirectorySelectedCallback directorySelectedCallback) {

        this.directorySelectedCallback = directorySelectedCallback;

        JTextPane jTextPane = new JTextPane();
        String descriptionText = String.format(
                "The application quickly shows you directory sizes as a Pie chart.\n" +
                        "Click on '%s' button to start immediately in the \n" +
                        "selected directory or click on '%s' to specify a custom location.",
                START_ANALYSIS_HERE_BUTTON_TEXT, SELECT_ANOTHER_LOCATION_BUTTON_TEXT);

        jTextPane.setText(descriptionText);
        jTextPane.setBackground(getBackground());
        jTextPane.setEditable(false);

        add(jTextPane, 0, 0, 4, 4, GridBagConstraints.BOTH);
        add(Box.createRigidArea(new Dimension(10, 10)), 0, 4, 4, 1, GridBagConstraints.BOTH);


        add(new JLabel("Currently selected location:"), 0, 5, 1, 1, GridBagConstraints.VERTICAL);
        pathSelectionJLabel = new JLabel(DEFAULT_PATH);

        add(pathSelectionJLabel, 1, 5, 1, 1, GridBagConstraints.VERTICAL);

        add(Box.createRigidArea(new Dimension(10, 10)), 0, 6, 4, 1, GridBagConstraints.BOTH);

        Button selectAnotherLocation = new Button(SELECT_ANOTHER_LOCATION_BUTTON_TEXT);
        selectAnotherLocation.addActionListener(e -> onSelectAnotherLocationClicked());
        add(selectAnotherLocation, 0, 7, 1, 1, GridBagConstraints.NONE);


        Button startAnalysisHere = new Button(START_ANALYSIS_HERE_BUTTON_TEXT);
        startAnalysisHere.addActionListener(e -> onStartAnalysisHereClicked());
        add(startAnalysisHere, 1, 7, 1, 1, GridBagConstraints.NONE);
    }

    private void onStartAnalysisHereClicked() {

        String filePath = pathSelectionJLabel.getText();

        File targetDirectory = new File(filePath);

        if (!(targetDirectory.exists() && targetDirectory.isDirectory())) {
            JOptionPane.showMessageDialog(this,
                    "Please specify a valid directory to analyse", "Invalid path to analyse",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            directorySelectedCallback.onDirectorySelected(targetDirectory);
        }
    }

    private void onSelectAnotherLocationClicked() {
        String currentPathSelection = pathSelectionJLabel.getText();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select directory to analyse");

        fileChooser.setCurrentDirectory(new File(currentPathSelection));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            pathSelectionJLabel.setText(selectedFile.getAbsolutePath());
        }
    }


}
