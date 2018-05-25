package com.athaydes.pathtrie;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import static com.athaydes.pathtrie.PerformanceTest.NO_PARAMETERS;
import static com.athaydes.pathtrie.PerformanceTest.PARAMETERIZED;

public class StatisticsBarChart extends Application {

    @Override
    public void start(Stage stage) {
        List<String> args = getParameters().getUnnamed();
        boolean showParameterized = args.contains("p");

        Map<String, LongSummaryStatistics> stats;

        if (showParameterized) {
            stats = PerformanceTest.collectStats(PerformanceTest.run(PARAMETERIZED));
        } else {
            stats = PerformanceTest.collectStats(PerformanceTest.run(NO_PARAMETERS));
        }

        stage.setTitle("PathTrie Performance Test");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Performance Summary " +
                (showParameterized ? "(Parameterized)" : "(Non-parameterized)"));
        xAxis.setLabel("Path");
        yAxis.setLabel("Time (ns)");

        XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        minSeries.setName("Min");
        stats.forEach((path, stat) -> {
            minSeries.getData().add(new XYChart.Data<>(path, stat.getMin()));
        });

        XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("Max");
        stats.forEach((path, stat) -> {
            maxSeries.getData().add(new XYChart.Data<>(path, stat.getMax()));
        });

        XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();
        avgSeries.setName("Avg");
        stats.forEach((path, stat) -> {
            avgSeries.getData().add(new XYChart.Data<>(path, stat.getAverage()));
        });

        Scene scene = new Scene(barChart, 800, 600);
        barChart.getData().addAll(minSeries, maxSeries, avgSeries);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
