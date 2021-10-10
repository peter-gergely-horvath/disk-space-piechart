package com.github.diskspacepiechart;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ApplicationWindow extends JFrame {

    private final SelectRootDirectorPanel selectRootDirectorPanel;
    private final DirectoryAnalysisPanel directoryAnalysisPanel;

    ApplicationWindow() {

        setTitle("Disk Space Pie Chart");
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();


        selectRootDirectorPanel = new SelectRootDirectorPanel(this::onRootDirectorySelected);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        layout.setConstraints(selectRootDirectorPanel, constraints);
        add(selectRootDirectorPanel);


        directoryAnalysisPanel = new DirectoryAnalysisPanel();
        directoryAnalysisPanel.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(directoryAnalysisPanel, constraints);
        add(directoryAnalysisPanel);

    }

    private void onRootDirectorySelected(File selectedDirectory) {
        selectRootDirectorPanel.setVisible(false);
        directoryAnalysisPanel.setSelectedDirectory(selectedDirectory);
        directoryAnalysisPanel.setVisible(true);
    }

}
