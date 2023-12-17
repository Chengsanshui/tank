package com.wjy_chy.tank.ui;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.texture;

/**
 * Main menu radio button
 */
public class MainMenuButton extends RadioButton {

    public MainMenuButton(String text, Runnable action) {
        Texture texture = texture("ui/icon.png");
        texture.setRotate(180);
        texture.setVisible(false);
        setGraphic(texture);
        setGraphicTextGap(30);
        getStyleClass().add("main-menu-btn");
        setText(text);
        //If selected, the previous tank picture will be displayed.
        selectedProperty().addListener((ob, ov, nv) -> texture.setVisible(nv));
        //Press Enter to execute the method corresponding to this button
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                play("select.wav");
                action.run();
            }
        });
        //Click the button to execute the method corresponding to the button
        setOnMouseClicked(event -> {
            play("select.wav");
            action.run();
        });
        //Move the mouse into it, select it, and play the sound
        setOnMouseEntered(e -> {
                    play("mainMenuHover.wav");
                    setSelected(true);
                }
        );
        //When it gets focus, select it and play the sound.
        focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                play("mainMenuHover.wav");
                setSelected(true);
            }
        });
    }
}
