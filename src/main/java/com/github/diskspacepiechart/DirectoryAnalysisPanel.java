package com.github.diskspacepiechart;

import org.jfree.chart.*;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DirectoryAnalysisPanel extends SimpleGridBagJPanel {

    private static final String PLEASE_WAIT_TEXT = "Please wait, while directories are analysed...";

    private final DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

    private File selectedDirectory;

    private final JProgressBar jProgressBar;
    private final JFreeChart chart;

    private AnalyseDirectoriesInBackgroundWorker backgroundWorker;


    private static final class AnalysisResult {

        private final String directoryName;
        private final double size;

        private AnalysisResult(String directoryName, double size) {
            this.directoryName = directoryName;
            this.size = size;
        }
    }

    DirectoryAnalysisPanel() {

        jProgressBar = new JProgressBar();
        jProgressBar.setIndeterminate(true);
        add(jProgressBar, 0, 0, 1, 1, GridBagConstraints.BOTH);

        chart = ChartFactory.createPieChart(
                "Directory sizes",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setLegendLabelGenerator(new
                StandardPieSectionLabelGenerator("{0}: {2}"));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(300, 300));
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity entity = event.getEntity();
                String drillDownFileName = null;
                if (entity instanceof PieSectionEntity) {
                    drillDownFileName = (String)((PieSectionEntity) entity).getSectionKey();
                } else if (entity instanceof LegendItemEntity) {
                    drillDownFileName = (String)((LegendItemEntity) entity).getSeriesKey();
                }

                if (drillDownFileName != null) {
                    onDrillDownFileNameSelected(drillDownFileName);
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

            }
        });

        add(chartPanel, 0, 1, 1, 1, GridBagConstraints.BOTH);
    }

    private void onDrillDownFileNameSelected(String drillDownFileName) {

        File drillDownFile = new File(selectedDirectory, drillDownFileName);

        setSelectedDirectory(drillDownFile);
    }


    public void setSelectedDirectory(File selectedDirectory) {

        if (backgroundWorker != null) {
            backgroundWorker.cancel(true);
        }

        this.dataset.clear();

        this.selectedDirectory = selectedDirectory;

        startBackgroundWork();

        backgroundWorker = new AnalyseDirectoriesInBackgroundWorker(selectedDirectory);
        backgroundWorker.execute();
    }

    private void startBackgroundWork() {
        this.jProgressBar.setString(PLEASE_WAIT_TEXT);
        this.jProgressBar.setStringPainted(true);
        this.jProgressBar.setIndeterminate(true);
    }

    private void stopBackgroundWork() {
        this.chart.setTitle(selectedDirectory.getAbsolutePath());
        this.jProgressBar.setIndeterminate(false);
        this.jProgressBar.setMaximum(100);
        this.jProgressBar.setValue(100);
        this.jProgressBar.setString("");
    }

    private void onDirectoryAnalysed(AnalysisResult ar) {
        this.dataset.setValue(ar.directoryName, ar.size);

    }


    private class AnalyseDirectoriesInBackgroundWorker extends SwingWorker<Void, AnalysisResult> {

        private final File directoryToAnalyse;

        private AnalyseDirectoriesInBackgroundWorker(File directoryToAnalyse) {
            this.directoryToAnalyse = directoryToAnalyse;
        }

        @Override
        protected Void doInBackground() throws Exception {

            File[] files = directoryToAnalyse.listFiles(File::isDirectory);
            if (files != null) {
                if (files.length == 0) {
                    files = directoryToAnalyse.listFiles(File::isFile);
                }

                if (files != null) {
                    for (File theDir : files) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }

                        long directorySize = getDirectorySize(theDir);

                        AnalysisResult result = new AnalysisResult(theDir.getName(), directorySize);

                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }

                        publish(result);
                    }
                }
            }

            return null;
        }

        private long getDirectorySize(File file) {
            try {
                AtomicLong size = new AtomicLong(0);

                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes fileAttributes) {
                        // sum size of all visit file
                        size.addAndGet(fileAttributes.size());

                        FileVisitResult fileVisitResult;

                        if (!Thread.currentThread().isInterrupted()) {
                            fileVisitResult = FileVisitResult.CONTINUE;
                        } else {
                            size.set(-1);
                            fileVisitResult = FileVisitResult.TERMINATE;
                        }
                        return fileVisitResult;
                    }
                });

                return size.get();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void process(List<AnalysisResult> results) {
            results.forEach(DirectoryAnalysisPanel.this::onDirectoryAnalysed);
        }

        @Override
        protected void done() {
            if(!isCancelled()) {
                stopBackgroundWork();
            }
        }
    }
}
