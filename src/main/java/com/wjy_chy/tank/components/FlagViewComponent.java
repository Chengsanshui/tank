package com.wjy_chy.tank.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

/**
 * If the flag is hit, then switch to the failed flag image
 */
public class FlagViewComponent extends Component {
    public void hitFlag() {
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(FXGL.texture("map/flag_failed.png"));
    }
}
