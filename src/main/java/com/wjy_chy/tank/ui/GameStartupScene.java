package com.wjy_chy.tank.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Game startup scene
 */
public class GameStartupScene extends StartupScene {
    public GameStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);
        StackPane pane = new StackPane(new ImageView(getClass().getResource("/assets/textures/ui/fxgl_logo.png").toExternalForm()));
        pane.setPrefSize(appWidth, appHeight);
        pane.setStyle("-fx-background-color: black");
        getContentRoot().getChildren().addAll(pane);
    }
}
