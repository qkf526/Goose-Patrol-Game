
package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

//Class that draws the arrows that point to the menu the user has currently selected.
public class MenuArrow {

    public static Texture create() {

        Pixmap pixmap = new Pixmap(
            24, 24, Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        // Black outline.
        pixmap.setColor(Color.BLACK);
        pixmap.fillTriangle(
            2, 1,
            2, 22,
            22, 12
        );

        // Gold interior.
        pixmap.setColor(
            new Color(1f, 0.78f, 0.16f, 1f)
        );
        pixmap.fillTriangle(
            5, 5,
            5, 18,
            17, 12
        );

        Texture texture = new Texture(pixmap);

        texture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        pixmap.dispose();

        return texture;
    }
}

