package com.athaydes.pathtrie;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public abstract class Chart extends Application {

    abstract Node createChartPane();

    @Override
    public void start(Stage stage) {
        stage.setTitle("PathTrie Performance Test");

        Node chartPane = createChartPane();

        Parent charts = new ScrollPane(chartPane);

        Button saveAsPic = new Button("Save as Picture");
        saveAsPic.setOnMouseClicked(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select a directory");
            File dir = chooser.showDialog(stage);
            if (dir != null) {
                File file = new File(dir, "results-line-chart.png");
                chartPane.snapshot(snapshot -> {
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot.getImage(), null), "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }, null, null);
            }
        });

        VBox topBox = new VBox(10, charts, saveAsPic);

        Scene scene = new Scene(topBox, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
