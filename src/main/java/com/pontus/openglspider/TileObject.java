package com.pontus.openglspider;

public class TileObject {

    public int index;
    public float x;
    public float y;
    public float[] color;
    private float width;
    private boolean isTouched = false;


    public TileObject(int index, float x, float y) {
        if (index < 0) {
            index = 0;
        }

        this.index = index;
        this.x = x;
        this.y = y;
        this.color = new float[]{1f, 1f, 1f, 1.0f};
        isTouched = false;
    }


    public TileObject(int index, float x, float y, float width) {
        if (index < 0) {
            index = 0;
        }

        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.color = new float[]{1f, 1f, 1f, 1.0f};
        isTouched = false;

    }


    public void handleActionDown(float eventX, float eventY) {


        float x1 = x + 5;
        float x2 = x + width;
        float y1 = y + 5;
        float y2 = y + width;


        if (eventX >= x1 && eventX <= x2) {
            if (eventY >= y1 && eventY <= y2) {
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }
    }

    public void setNotTouched() {
        isTouched = false;
    }

    private void setTouched(boolean touch) {
        isTouched = touch;
    }

    public boolean isTouched() {
        return isTouched;
    }


    public void moveTo(float x, float y) {
        // Update our location.
        this.x = x;
        this.y = y;
    }

    public void move(float deltax, float deltay) {
        this.x += deltax;
        this.y += deltay;
    }

}
