package com.athaydes.pathtrie;

import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

public class ResultsLineChart extends Chart {

    @Override
    public Node createChartPane() {
        List<String> args = getParameters().getUnnamed();
        boolean showParameterized = args.contains("p");
        Map<String, long[]> results;
        StackPane chartPane;

        if (showParameterized) {
            results = PerformanceTest.run(PerformanceTest.PARAMETERIZED);
            chartPane = new StackPane(create98PercentChart(
                    "Parameterized Performance", results));
            chartPane.setPrefSize(700, 550);
        } else {
            results = PerformanceTest.run(PerformanceTest.NO_PARAMETERS);
            chartPane = new StackPane(create98PercentChart(
                    "Non-parameterized Performance", results));
            chartPane.setPrefSize(700, 550);
        }

        return chartPane;
    }

    private LineChart<Number, Number> create98PercentChart(String title, Map<String, long[]> results) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        xAxis.setLabel("Path");
        yAxis.setLabel("Time (ns)");

        List<XYChart.Series<Number, Number>> allSeries = results.entrySet().stream().map((entry) -> {
            String path = entry.getKey();
            long[] data = entry.getValue();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(path);
            PrimitiveIterator.OfLong dataIter = PerformanceTest.take98Percentile(data).iterator();
            int i = 0;
            long[] smoothData = new long[100];
            while (dataIter.hasNext()) {
                smoothData[i % 100] = dataIter.nextLong();
                if (i > 0 && i % 100 == 99) {
                    double dataPoint = LongStream.of(smoothData).average().getAsDouble();
                    series.getData().add(new XYChart.Data<>(i, dataPoint));
                }
                i++;
            }
            return series;
        }).collect(Collectors.toList());

        lineChart.getData().addAll(allSeries);
        return lineChart;
    }

}
