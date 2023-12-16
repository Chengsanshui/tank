package com.leewyatt.github.tank.effects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.leewyatt.github.tank.GameConfig;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 * 玩家获得无敌道具的效果
 */
public class HelmetEffect extends Effect {
    private final AnimatedTexture helmetAnimTexture;

    public HelmetEffect() {
        super(GameConfig.HELMET_TIME);
        helmetAnimTexture = new AnimatedTexture(new AnimationChannel(FXGL.image("item/armed_helmet.png"), Duration.seconds(.3), 4)).loop();
    }

    @Override
    public void onStart( Entity entity) {
        helmetAnimTexture.setTranslateX(entity.getWidth()/2.0-helmetAnimTexture.getFitWidth()/2);
        helmetAnimTexture.setTranslateY(entity.getHeight()/2.0-helmetAnimTexture.getFitHeight()/2);
        entity.getViewComponent().addChild(helmetAnimTexture);
    }

    @Override
    public void onEnd( Entity entity) {
        entity.getViewComponent().removeChild(helmetAnimTexture);
    }

}
