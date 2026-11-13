package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;



public class Button {
    float x;
    float y;
    float width;
    float height;
    String label;

    public Button(String label, float x, float y, float width, float height)
    {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @param pointx x co-ordinate of point
     * @param pointy y co-ordinate of point
     * @return true if point x,y is inside buttons rectangle
     */
    public boolean contains(float pointx, float pointy)
    {
        return pointx >= x && pointx <= x + width && pointy >= y && pointy <= y + height;
    }

    public void drawShape(ShapeRenderer shape, boolean hover)
    {
        if (hover)
        {
            shape.setColor(Color.LIGHT_GRAY);
        }
        else {
            shape.setColor(Color.DARK_GRAY);
        }

        shape.rect(x,y,width,height);
    }

    public void drawText(SpriteBatch batch, BitmapFont font)
    {
        GlyphLayout layout = new GlyphLayout(font, label); // measures how big text will be - allows centring
        font.draw(batch, label, x + (width - layout.width) / 2, y + (height = layout.height) / 2);
    }
}
