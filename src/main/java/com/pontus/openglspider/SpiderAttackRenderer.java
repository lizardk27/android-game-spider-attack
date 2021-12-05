package com.pontus.openglspider;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SpiderAttackRenderer implements Renderer {

    // VARIABLE DEFINITION START
    //--------------------------------------------------------------------------------

    // Our screenresolution
    final static float optimalScreenHeight = 1920;
    // Our view, projection and p&v matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];
    // textures array - the size is defined by
    // GLES20.GL_TEXTURE31 -- predefined texture position
    // which is the highest number of texture predefined pos
    // CHANGED -- TESTING 99 as limit
    // int[] texturenames = new int[32];
    int[] texturenames = new int[30];
    long mLastTime;
    long msLastTimeBugsCreated = 0;
    int bug_speed = 10;
    int bug_creation_counter = 5000;
    //ARRAY LISTS TO HOLD DIFFRENT SPRITES
    private ArrayList<Sprite> permanent_images = new ArrayList<Sprite>();
    private ArrayList<Sprite> bugs = new ArrayList<Sprite>();
    private ArrayList<Sprite> menu_images = new ArrayList<Sprite>();
    private float currScreenHeight = 1920;
    private float currScreenWidth = 1080;
    private boolean shooting;
    private float scale;
    private PointF center_stage;
    private long last_bullets_checked = 0;
    private long last_bugs_created = 0;
    private long last_bugs_updated = 0;
    private int bugCounter = 0;
    private int activeBug = 0;


    //Sprites

    //THE SPIDER
    private Sprite spider_sprite;


    // Menu Sprites
    private Sprite about_sprite;
    private Sprite menu_back_sprite;
    private Sprite soundVol_sprite;
    private Sprite musicVol_sprite;
    private Sprite play_sprite;
    private Sprite exit_sprite;
    private Sprite musicControl_sprite;
    private Sprite soundControl_sprite;
    private Sprite pause_button;
    private Sprite connect_button_sprite;


    private Sprite confirm_text_sprite;
    private Sprite confirm_yes_sprite;
    private Sprite confirm_no_sprite;


    private Sprite background_sprite;
    private Sprite moveButton_sprite;
    private Sprite shootButton_sprite;
    private Sprite bullet_sprite;
    private Sprite bug1_sprite;
    private Sprite bug2_sprite;
    private Sprite bug3_sprite;
    private Sprite bug4_sprite;
    private Sprite bug5_sprite;
    private Sprite bug6_sprite;
    private Sprite bug7_sprite;
    private Sprite bug8_sprite;

    private Sprite dead_bug_sprite_1;


    //BANNERS

    private TileObject level_up_sprite;
    private TileObject extra_shots_sprite;
    private TileObject life_lost_sprite;
    private TileObject start_sprite;
    private TileObject lost_sprite;
    private TileObject won_sprite;

    //Banners timers
    private int sign_lost_timer = -2;
    private int sign_won_timer = -2;
    private int sign_level_timer = -2;
    private int sign_shots_timer = -2;
    private int sign_start_timer = -2;
    private int sign_lifelost_timer = -2;

    // STONES
    private TileObject stone_xxxa0_sprite;
    private TileObject stone_xxxa1_sprite;
    private TileObject stone_xxxa2_sprite;
    private TileObject stone_xxxb0_sprite;
    private TileObject stone_xxxb1_sprite;
    private TileObject stone_xxxb2_sprite;
    private TileObject stone_xxl0_sprite;
    private TileObject stone_xxl1_sprite;
    private TileObject stone_xxl2_sprite;
    private TileObject stone_xxl3_sprite;
    private TileObject stone_xxr0_sprite;
    private TileObject stone_xxr1_sprite;
    private TileObject stone_xxr2_sprite;
    private TileObject stone_xxr3_sprite;

    private TileObject dead_spider_x1_sprite;
    private TileObject dead_spider_x2_sprite;
    private TileObject dead_spider_x3_sprite;
    private TileObject dead_spider_x4_sprite;
    private TileObject dead_spider_x5_sprite;
    private TileObject bug_counter1_sprite;
    private TileObject bug_counter2_sprite;
    private TileObject bug_counter3_sprite;
    private TileObject bug_counter4_sprite;
    private TileObject bug_counter5_sprite;
    private TileObject bug_counter6_sprite;


    // Misc
    private Context mContext;
    private boolean shootingButtonActive;
    private float minXpos;
    private float maxXpos;
    private float middleXpos;
    private float angle;
    private RectF base;
    private int talking_timer = 0;
    private int bullet_speed = 20;
    private int bullet_prize_counter = 0;
    private float minXVCpos;
    private float maxXVCpos;
    private float middleXVCpos;
    private int number_lives_left = 5;
    private int number_bugs_reached_spider = 0;

    /*
    For testing purposes change the next three lines
    TO ORIGINAL VALUES>

    private int score = 0;
    private int level = 0;
    private int level_change_score = 30;

     */

    private int score = 0;
    private int level = 0;
    private int level_change_score = 30;


    private boolean isPaused = true;
    private boolean isSurfaceCreated = false;
    private boolean levelChangeLocked = true;
    private boolean isFirstRun = true;
    private boolean gameIsWon = false;
    private boolean gameIsLost = false;
    private boolean showPlayButton = true;
    private boolean returningFromPause = false;


    private TextManager tm;

    private RocksTileManager lives_manager;
    private RocksTileManager stones_manager;
    private SignsTileManager banners_manager;

    private TextObject score_text;
    private TextObject level_text;

    private SoundManager spiderSound;
    //--------------------------------------------------------------------------------
    // VARIABLE DEFINITION END

    // Constructor
    //    SpiderAttackRenderer(Context c)
    public SpiderAttackRenderer(Context c) {


        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Rect rectSize = new Rect();
        Point realSizePoint = new Point();

        currScreenHeight = metrics.heightPixels;
        currScreenWidth = metrics.widthPixels;

        //System.out.println("METRICS (TRANSFORMED-CURRENT):");
        //System.out.println("Width = " + currScreenWidth + " - Height = " + currScreenHeight);
        //System.out.println("----------------------------------------------------------------------------------");

        // SET BASE SQUARE FOR OUR IMAGE, INITIAL POSITION (translation)
        base = new RectF(
                -(((currScreenHeight * 1.25f) / 2) / 1.48148148148148148148148148f),   // left,
                ((currScreenHeight * 1.25f) / 2),                                    // top,
                (((currScreenHeight * 1.25f) / 2) / 1.48148148148148148148148148f),    // right,
                -((currScreenHeight * 1.25f) / 2));                                  // bottom

        center_stage = new PointF(currScreenWidth / 2, currScreenHeight / 2);


        scale = 1f;
        angle = 0f;

        // ADDING BACKGROUND
        background_sprite = new Sprite(c, scale, angle, center_stage, base);
        permanent_images.add(background_sprite);

        // calculate scale factor for all images
        // 1920 x 1080 is the "normal" resolution
        // so, since all images are set to that resolution
        // we change the scale in accordance
        // to the difference between the optimalScreenHeight and currScreenHeight

        scale = currScreenHeight / optimalScreenHeight;
        ////System.out.println("SCALE=" + scale);

        // ADDING controlCase
        // SIZE: 200x200      left,    top,     right,     bottom
        base = new RectF(-100f, 100f, 100f, -100f);

        // Position from center
        float xpos = center_stage.x - 220 * scale;
        float ypos = center_stage.y - 800 * scale;

        PointF translation = new PointF(xpos, ypos);

        moveButton_sprite = new Sprite(c, scale, angle, translation, base);
        permanent_images.add(moveButton_sprite);

        // same size (base)
        // Position from center (same y)
        xpos = center_stage.x + 320 * scale;

        translation = new PointF(xpos, ypos);

        shootButton_sprite = new Sprite(c, scale, angle, translation, base);
        permanent_images.add(shootButton_sprite);


        // SIZE: 50x100      left,    top,     right,     bottom
        base = new RectF(-25f, 50f, 25f, -50f);

        // Position from center

        xpos = -100 * scale;
        ypos = center_stage.y + 900 * scale;

        translation = new PointF(xpos, ypos);

        bullet_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -450 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug1_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -400 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug2_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -550 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug3_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -600 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug4_sprite = new Sprite(c, scale, angle, translation, base);


        xpos = -650 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug5_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -700 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug6_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -750 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug7_sprite = new Sprite(c, scale, angle, translation, base);

        xpos = -800 * scale;
        ypos = center_stage.y + 9000 * scale;

        translation = new PointF(xpos, ypos);
        bug8_sprite = new Sprite(c, scale, angle, translation, base);


        // SIZE: 120x100      left,    top,     right,     bottom
        base = new RectF(-60f, 50f, 60f, -50f);

        // Position from center
        xpos = center_stage.x - 385 * scale;
        ypos = center_stage.y + 755 * scale;

        spiderSound = new SoundManager(mContext);


        // SIZE: 120x100      left,    top,     right,     bottom
        base = new RectF(-60f, 50f, 60f, -50f);
        // Position from center
        xpos = -222 * scale;
        ypos = -100 * scale;
        translation = new PointF(xpos, ypos);
        dead_bug_sprite_1 = new Sprite(c, scale, angle, translation, base);
        //permanent_images.add(dead_bug_sprite_1);


        // SIZE: 400x200      left,    top,     right,     bottom
        base = new RectF(-200f, 100f, 200f, -100f);

        // Position from center
        xpos = center_stage.x * scale;
        ypos = center_stage.y - 600 * scale;

        translation = new PointF(xpos, ypos);
        spider_sprite = new Sprite(c, scale, angle, translation, base);
        //permanent_images.add(spider_sprite);

        // SIZE: 112x112      left,    top,     right,     bottom
        base = new RectF(-56f, 56f, 56f, -56f);
        // Position from center
        xpos = center_stage.x + 460 * scale;
        ypos = center_stage.y + 890 * scale;
        translation = new PointF(xpos, ypos);
        pause_button = new Sprite(c, scale, angle, translation, base);


        //PAUSE MENU AND START MENU OVER ALL RENDERED LAST !!!!

        // SIZE: 430x650      left,    top,     right,     bottom
        base = new RectF(-215f, 325f, 215f, -325f);
        // Position from center
        xpos = center_stage.x + 10 * scale;
        ypos = center_stage.y + 10 * scale;
        translation = new PointF(xpos, ypos);

        menu_back_sprite = new Sprite(c, scale, angle, translation, base);
        menu_back_sprite.setScale(1.5f * scale);
        menu_images.add(menu_back_sprite);


        // SIZE: 300x150      left,    top,     right,     bottom
        base = new RectF(-150f, 75f, 150f, -75f);
        // Position from center
        ypos = center_stage.y + 170 * scale;
        translation = new PointF(xpos, ypos);
        soundVol_sprite = new Sprite(c, scale, angle, translation, base);
        soundVol_sprite.setScale(scale * 1.2f);
        menu_images.add(soundVol_sprite);
        base = new RectF(-30f, 30f, 30f, -30f);
        ypos = center_stage.y + 140 * scale;
        translation = new PointF(xpos, ypos);
        soundControl_sprite = new Sprite(c, scale, angle, translation, base);
        soundControl_sprite.setScale(scale * 1.2f);
        menu_images.add(soundControl_sprite);


        // SIZE: 300x150      left,    top,     right,     bottom
        base = new RectF(-150f, 75f, 150f, -75f);
        // Position from center
        ypos = center_stage.y + 10 * scale;
        translation = new PointF(xpos, ypos);
        musicVol_sprite = new Sprite(c, scale, angle, translation, base);
        musicVol_sprite.setScale(scale * 1.2f);
        menu_images.add(musicVol_sprite);


        base = new RectF(-30f, 30f, 30f, -30f);
        ypos = center_stage.y - 30 * scale;
        translation = new PointF(xpos, ypos);
        musicControl_sprite = new Sprite(c, scale, angle, translation, base);
        musicControl_sprite.setScale(scale * 1.3f);
        menu_images.add(musicControl_sprite);


        // SIZE: 420x112       left,    top,     right,     bottom
        base = new RectF(-210f, 66f, 210f, -66f);
        // Position from center
        ypos = center_stage.y + 370 * scale;
        translation = new PointF(xpos, ypos);
        about_sprite = new Sprite(c, scale, angle, translation, base);
        about_sprite.setScale(scale * 1.2f);
        menu_images.add(about_sprite);


        // SIZE: 250x60      left,    top,     right,     bottom
        base = new RectF(-125f, 30f, 125f, -30f);
        // Position from center
        ypos = center_stage.y - 250 * scale;
        translation = new PointF(xpos, ypos);
        play_sprite = new Sprite(c, scale, angle, translation, base);
        play_sprite.setScale(scale * 1.5f);
        menu_images.add(play_sprite);

        // SIZE: 428x117      left,    top,     right,     bottom
        base = new RectF(-214, 58.5f, 214f, -58.5f);
        // Position from center
        ypos = center_stage.y - 250 * scale;
        translation = new PointF(xpos, ypos);
        connect_button_sprite = new Sprite(c, scale, angle, translation, base);
        connect_button_sprite.setScale(scale * 0f);
        menu_images.add(connect_button_sprite);


        // SIZE: 220x50      left,    top,     right,     bottom
        base = new RectF(-110f, 25f, 110f, -25f);
        // Position from center
        ypos = center_stage.y - 400 * scale;
        translation = new PointF(xpos, ypos);
        exit_sprite = new Sprite(c, scale, angle, translation, base);
        exit_sprite.setScale(scale * 1.5f);
        menu_images.add(exit_sprite);


        // SIZE: 364x34     left,    top,     right,     bottom
        base = new RectF(-182f, 17f, 182f, -17f);
        // Position from center
        ypos = center_stage.y - 250 * scale;
        translation = new PointF(xpos, ypos);
        confirm_text_sprite = new Sprite(c, scale, angle, translation, base);
        confirm_text_sprite.setScale(scale * 0f);
        menu_images.add(confirm_text_sprite);

        // SIZE: 103x48     left,    top,     right,     bottom
        base = new RectF(-51.5f, 24f, 51.5f, -24f);
        // Position from center
        ypos = center_stage.y - 400 * scale;
        xpos = center_stage.x - 100 * scale;
        translation = new PointF(xpos, ypos);
        confirm_yes_sprite = new Sprite(c, scale, angle, translation, base);
        confirm_yes_sprite.setScale(scale * 0f);
        menu_images.add(confirm_yes_sprite);


        // SIZE: 103x48     left,    top,     right,     bottom
        base = new RectF(-51.5f, 24f, 51.5f, -24f);
        // Position from center
        ypos = center_stage.y - 400 * scale;
        xpos = center_stage.x + 100 * scale;
        translation = new PointF(xpos, ypos);
        confirm_no_sprite = new Sprite(c, scale, angle, translation, base);
        confirm_no_sprite.setScale(scale * 0f);
        menu_images.add(confirm_no_sprite);


        // GAME ANNOUNCE SIGNS / BANNERS


        ypos = center_stage.y + 10 * scale;
        xpos = center_stage.x + 10 * scale;
        //LISTO
        start_sprite = new TileObject(0, xpos - 500 * scale, ypos - 500 * scale);
        extra_shots_sprite = new TileObject(1, xpos - 500 * scale, ypos - 500 * scale);
        won_sprite = new TileObject(2, xpos - 470 * scale, ypos - 500 * scale);
        lost_sprite = new TileObject(3, xpos - 500 * scale, ypos - 500 * scale);
        life_lost_sprite = new TileObject(4, xpos - 500 * scale, ypos - 500 * scale);
        level_up_sprite = new TileObject(5, xpos - 500 * scale, ypos - 500 * scale);


        System.out.println("Sign_ level_timer pos(x,y):(" + level_up_sprite.x + "," + level_up_sprite.y + ")");


        // LIVES

        // Position from center
        xpos = center_stage.x + 430 * scale;
        ypos = center_stage.y - 190 * scale;
        dead_spider_x1_sprite = new TileObject(6, xpos, ypos);

        xpos = center_stage.x + 430 * scale;
        ypos = center_stage.y - 115 * scale;
        dead_spider_x2_sprite = new TileObject(6, xpos, ypos);

        xpos = center_stage.x + 430 * scale;
        ypos = center_stage.y - 40 * scale;
        dead_spider_x3_sprite = new TileObject(6, xpos, ypos);

        xpos = center_stage.x + 430 * scale;
        ypos = center_stage.y + 35 * scale;
        dead_spider_x4_sprite = new TileObject(6, xpos, ypos);

        xpos = center_stage.x + 430 * scale;
        ypos = center_stage.y + 110 * scale;
        dead_spider_x5_sprite = new TileObject(6, xpos, ypos);


        // BUGS

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y - 120 * scale;
        bug_counter1_sprite = new TileObject(7, xpos, ypos);

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y - 70 * scale;
        bug_counter2_sprite = new TileObject(7, xpos, ypos);

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y - 20 * scale;
        bug_counter3_sprite = new TileObject(7, xpos, ypos);

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y + 30 * scale;
        bug_counter4_sprite = new TileObject(7, xpos, ypos);

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y + 80 * scale;
        bug_counter5_sprite = new TileObject(7, xpos, ypos);

        xpos = center_stage.x - 500 * scale;
        ypos = center_stage.y + 130 * scale;
        bug_counter6_sprite = new TileObject(7, xpos, ypos);


        //STONES...

        xpos = center_stage.x - 240 * scale;
        ypos = center_stage.y + 252 * scale;
        stone_xxxa0_sprite = new TileObject(0, xpos, ypos, 170f);

        xpos = center_stage.x - 143 * scale;
        ypos = center_stage.y - 217 * scale;
        stone_xxxa1_sprite = new TileObject(0, xpos, ypos, 170f);

        xpos = center_stage.x - 82 * scale;
        ypos = center_stage.y + 17 * scale;
        stone_xxxa2_sprite = new TileObject(0, xpos, ypos, 170f);

        xpos = center_stage.x * scale;
        ypos = center_stage.y + 390 * scale;
        stone_xxxb0_sprite = new TileObject(1, xpos, ypos, 180f);

        xpos = center_stage.x + 100 * scale;
        ypos = center_stage.y + 135 * scale;
        stone_xxxb1_sprite = new TileObject(1, xpos, ypos, 180f);

        xpos = center_stage.x - 250 * scale;
        ypos = center_stage.y - 150 * scale;
        stone_xxxb2_sprite = new TileObject(1, xpos, ypos, 180f);

        xpos = center_stage.x - 80 * scale;
        ypos = center_stage.y + 500 * scale;
        stone_xxl0_sprite = new TileObject(3, xpos, ypos, 180f);

        xpos = center_stage.x + 125 * scale;
        ypos = center_stage.y - 246 * scale;
        stone_xxl1_sprite = new TileObject(3, xpos, ypos, 130f);

        xpos = center_stage.x * scale;
        ypos = center_stage.y - 0 * scale;
        stone_xxl2_sprite = new TileObject(3, xpos, ypos, 130f);

        xpos = center_stage.x - 295 * scale;
        ypos = center_stage.y + 108 * scale;
        stone_xxl3_sprite = new TileObject(3, xpos, ypos, 130f);

        xpos = center_stage.x - 380 * scale;
        ypos = center_stage.y + 390 * scale;
        stone_xxr0_sprite = new TileObject(4, xpos, ypos, 130f);

        xpos = center_stage.x + 150 * scale;
        ypos = center_stage.y + 320 * scale;
        stone_xxr1_sprite = new TileObject(4, xpos, ypos, 130f);

        xpos = center_stage.x + 270 * scale;
        ypos = center_stage.y - 91 * scale;
        stone_xxr2_sprite = new TileObject(4, xpos, ypos, 130f);

        xpos = center_stage.x + 87 * scale;
        ypos = center_stage.y - 91 * scale;
        stone_xxr3_sprite = new TileObject(4, xpos, ypos, 130f);

    }

    public void onDestroy() {
        ((MainActivity) mContext).onDestroy();
        boolean success = CacheFileHandler.writeAllCachedText(mContext, "spiderCacheFile.txt", "-1-0-0-5-");
        System.exit(0);
    }


    public void onPause() {
        if (gameIsWon | gameIsLost) {
            gameIsLost = false;
            gameIsWon = false;
        }
        returningFromPause = true;
        ((MainActivity) mContext).onPause();
        if (!showPlayButton) {
            play_sprite.setScale(scale * 0f);
            connect_button_sprite.setScale(scale * 1f);
        } else {
            play_sprite.setScale(scale * 1.5f);
            connect_button_sprite.setScale(scale * 0f);
        }
        play_sprite.UpdateSprite();
        connect_button_sprite.UpdateSprite();
    }

    public void showPlayButton(boolean state) {
        showPlayButton = state;
    }


    private void setLevel(int level_value) throws IOException {
        if (level_value < 0 || level_value > 11) {
            throw new IOException("level value should be between 0 and 11");
        }

        // Position from center
        float ypos = center_stage.y + 725 * scale;
        float xpos = center_stage.x + 325 * scale;

        // Create our new textobject
        String tmp = String.valueOf(level);
        while (tmp.length() < 2) {
            tmp = "0" + tmp;
        }
        // Prepare the text for rendering
        tm.removeText(level_text);
        level_text = new TextObject(tmp, xpos, ypos);
        tm.addText(level_text);
        tm.PrepareDraw();

        if (level == 11) {
            playWonAnimation();
        }


    }


    private void turnOnStones(int stones_number) {

        stones_manager.removeAll();

        int stone = 0;
        int[] stones_turned_on = new int[14];

        for (int tmp : stones_turned_on) {
            tmp = 0;
        }

        for (stone = 0; stone < stones_number; stone++) {
            int stone_pos = (int) Math.floor(Math.random() * 14);
            if (stones_turned_on[stone_pos] != 0) {
                //stone used
                stone--;
            } else {
                stones_turned_on[stone_pos] = 1;
                switch (stone_pos) {
                    case 0:
                        stones_manager.addTileObject(stone_xxxa0_sprite);
                        break;
                    case 1:
                        stones_manager.addTileObject(stone_xxxa1_sprite);
                        break;
                    case 2:
                        stones_manager.addTileObject(stone_xxxa2_sprite);
                        break;
                    case 3:
                        stones_manager.addTileObject(stone_xxxb0_sprite);
                        break;
                    case 4:
                        stones_manager.addTileObject(stone_xxxb1_sprite);
                        break;
                    case 5:
                        stones_manager.addTileObject(stone_xxxb2_sprite);
                        break;
                    case 6:
                        stones_manager.addTileObject(stone_xxl0_sprite);
                        break;
                    case 7:
                        stones_manager.addTileObject(stone_xxl1_sprite);
                        break;
                    case 8:
                        stones_manager.addTileObject(stone_xxl2_sprite);
                        break;
                    case 9:
                        stones_manager.addTileObject(stone_xxl3_sprite);
                        break;
                    case 10:
                        stones_manager.addTileObject(stone_xxr0_sprite);
                        break;
                    case 11:
                        stones_manager.addTileObject(stone_xxr1_sprite);
                        break;
                    case 12:
                        stones_manager.addTileObject(stone_xxr2_sprite);
                        break;
                    case 13:
                        stones_manager.addTileObject(stone_xxr3_sprite);
                        break;
                }// end Switch
            }//end else
        }// end for
        stones_manager.PrepareDraw();
    }

    public void onResume() {
        //System.out.println("SPIDER ATTACK-RENDERER IS RESUMING...");
        //System.out.println("CACHE FILE EXISTS???: " + CacheFileHandler.fileExists(mContext, "spiderCacheFile.txt"));

        if (!isPaused) {
//            ((MainActivity) (mContext)).hideBanner();
            ((MainActivity) (mContext)).closeContextMenu();
        }


        /*
        ((MainActivity) (mContext)).hideBanner();
        ((MainActivity) (mContext)).closeContextMenu();
        ((Ma binActivity) (mContext)).closeOptionsMenu();
        /*

        if (CacheFileHandler.fileExists(mContext, "spiderCacheFile.txt")) {
            String readText = CacheFileHandler.readAllCachedText(mContext, "spiderCacheFile.txt");
            System.out.println("READ FROM FILE: " + readText);
            String[] vals = readText.split("-");
            score = Integer.valueOf(vals[0]);
            level = Integer.valueOf(vals[1]);
            number_bugs_reached_spider = Integer.valueOf(vals[2]);
            number_lives_left = Integer.valueOf(vals[3]);
        }
*/
    }

    @Override
    public synchronized void onDrawFrame(GL10 unused) {


        isSurfaceCreated = true;
        // Get the current time
        long now = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (mLastTime > now) return;


        // BLOCK  RUNS IF NOT PAUSED
        if (!isPaused) {
            try {

                setScore(score);

                if (talking_timer > 0) {
                    talking_timer--;
                }

                if (talking_timer == 0 && score > 0 && score % 9 == 0) {

                    int pos = (int) Math.floor(Math.random() * 4);
                    switch (pos) {
                        case 0:
                            spiderSound.playSound(SoundManager.GREAT_JOB);
                            talking_timer = 2000;
                            break;
                        case 1:
                            spiderSound.playSound(SoundManager.EXCELLENT);
                            talking_timer = 2000;
                            break;
                        case 2:
                            spiderSound.playSound(SoundManager.EXPERT);
                            talking_timer = 2000;
                            break;
                        case 3:
                            spiderSound.playSound(SoundManager.BETTER);
                            talking_timer = 2000;
                            break;
                    }
                }


                if (returningFromPause || isFirstRun || ((score > 0) && (score % level_change_score == 0) && !levelChangeLocked)) {


                    levelChangeLocked = true;

                    if (!returningFromPause) {

                        if (isFirstRun) {
                            level++;
                            sign_start_timer = 500;
                        } else if (level < 11) {
                            spiderSound.playSound(SoundManager.LEVEL_UP);
                            sign_level_timer = 500;
                            level++;
                        }
                    }
                    isFirstRun = false;
                    returningFromPause = false;


                    setLevel(level);

                    switch (level) {
                        case 1:
                            bug_creation_counter = 3000;
                            bug_speed = 14;
                            turnOnStones(2);
                            break;
                        case 2:
                            bug_creation_counter = 2600;
                            bug_speed = 14;
                            turnOnStones(3);
                            break;
                        case 3:
                            bug_creation_counter = 2200;
                            bug_speed = 14;
                            turnOnStones(4);
                            break;
                        case 4:
                            bug_creation_counter = 1800;
                            bug_speed = 16;
                            turnOnStones(5);
                            break;
                        case 5:
                            bug_creation_counter = 1600;
                            bug_speed = 16;
                            turnOnStones(7);
                            break;
                        case 6:
                            bug_creation_counter = 1200;
                            bug_speed = 18;
                            turnOnStones(9);
                            break;
                        case 7:
                            bug_creation_counter = 1000;
                            bug_speed = 18;
                            turnOnStones(10);
                            break;
                        case 8:
                            bug_creation_counter = 800;
                            bug_speed = 20;
                            turnOnStones(11);
                            break;
                        case 9:
                            bug_creation_counter = 800;
                            bug_speed = 22;
                            turnOnStones(12);
                            break;
                        case 10:
                            bug_creation_counter = 600;
                            bug_speed = 24;
                            turnOnStones(13);
                            break;


                    }//end switch

                }
                // END OF TRY IN NOT PAUSED LOOP
            } catch (IOException e) {
                e.printStackTrace();
            }

            // STILL IN NOT PAUSED

            pause_button.setScale(scale * 1f);
            pause_button.UpdateSprite();
            pause_button.setNotTouched();


            //BULLETS
            if (last_bullets_checked == 0 || (now - last_bullets_checked) > bullet_speed) {

                if (bullet_speed < 20) {
                    bullet_prize_counter++;
                    if (bullet_prize_counter > 4500) {
                        bullet_prize_counter = 0;
                        bullet_speed = 20;

                    }
                }
                if (shooting) {
                    bullet_sprite.move(0, 25);
                    bullet_sprite.UpdateSprite();
                    if (bullet_sprite.getY() >= currScreenHeight - (350 * scale)) {
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                    }
                    bug1_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug2_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug3_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug4_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug5_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug6_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug7_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    bug8_sprite.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());


                    for (TileObject tmp : stones_manager.tileObjectCollection) {
                        tmp.handleActionDown(bullet_sprite.getX(), bullet_sprite.getY());
                    }

                    // BULLET HIT A STONE
                    if (
                            stone_xxxa0_sprite.isTouched() | stone_xxxa1_sprite.isTouched() |
                                    stone_xxxa2_sprite.isTouched() | stone_xxxb0_sprite.isTouched() |
                                    stone_xxxb1_sprite.isTouched() | stone_xxxb2_sprite.isTouched() |
                                    stone_xxl0_sprite.isTouched() | stone_xxl1_sprite.isTouched() |
                                    stone_xxl2_sprite.isTouched() | stone_xxl3_sprite.isTouched() |
                                    stone_xxr0_sprite.isTouched() | stone_xxr1_sprite.isTouched() |
                                    stone_xxr2_sprite.isTouched() | stone_xxr3_sprite.isTouched()
                            ) {

                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();

                        shooting = false;

                        spiderSound.playSound(SoundManager.SPIDER_STEP);
                    }


                    // THIS BUG IS PRIZE
                    // BULLET SPEED INCREASES FOR 10 SECONDS
                    if (bug8_sprite.isTouched()) {

                        bullet_speed = 1;
                        //System.out.println("SPEED START");
                        bullet_prize_counter = 0;
                        spiderSound.playSound(SoundManager.EXTRA_SHOTS);
                        sign_shots_timer = 500;

                        dead_bug_sprite_1.setX(bug8_sprite.getX());
                        dead_bug_sprite_1.setY(bug8_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug8_sprite.setX(9000f);
                        bug8_sprite.setY(9000f);
                        bug8_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug7_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug7_sprite.getX());
                        dead_bug_sprite_1.setY(bug7_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug7_sprite.setX(9000f);
                        bug7_sprite.setY(9000f);
                        bug7_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug6_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug6_sprite.getX());
                        dead_bug_sprite_1.setY(bug6_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug6_sprite.setX(9000f);
                        bug6_sprite.setY(9000f);
                        bug6_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug5_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug5_sprite.getX());
                        dead_bug_sprite_1.setY(bug5_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug5_sprite.setX(9000f);
                        bug5_sprite.setY(9000f);
                        bug5_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug4_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug4_sprite.getX());
                        dead_bug_sprite_1.setY(bug4_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug4_sprite.setX(9000f);
                        bug4_sprite.setY(9000f);
                        bug4_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug3_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug3_sprite.getX());
                        dead_bug_sprite_1.setY(bug3_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug3_sprite.setX(9000f);
                        bug3_sprite.setY(9000f);
                        bug3_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug2_sprite.isTouched()) {
                        dead_bug_sprite_1.setX(bug2_sprite.getX());
                        dead_bug_sprite_1.setY(bug2_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug2_sprite.setX(9000f);
                        bug2_sprite.setY(9000f);
                        bug2_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                        spiderSound.playSound(SoundManager.DEAD_BUG);
                    }
                    if (bug1_sprite.isTouched()) {

                        spiderSound.playSound(SoundManager.DEAD_BUG);
                        dead_bug_sprite_1.setX(bug1_sprite.getX());
                        dead_bug_sprite_1.setY(bug1_sprite.getY());
                        dead_bug_sprite_1.UpdateSprite();
                        bug1_sprite.setX(9000f);
                        bug1_sprite.setY(9000f);
                        bug1_sprite.UpdateSprite();
                        bullet_sprite.setY(-100F);
                        bullet_sprite.UpdateSprite();
                        shooting = false;
                        score++;
                        levelChangeLocked = false;
                    }


                }
                last_bullets_checked = now;
            }


            //  CREATE BUGS if game not won or lost a
            //  AND
            //  TIME CONDITION: (last_bugs_updated == 0 || (now - last_bugs_updated)

            if ((last_bugs_updated == 0 || (now - last_bugs_updated) > 100)
                    && (!gameIsLost && !gameIsWon)) {

                bug1_sprite.move(0, -bug_speed);
                bug1_sprite.UpdateSprite();
                bug2_sprite.move(0, -bug_speed);
                bug2_sprite.UpdateSprite();
                bug3_sprite.move(0, -bug_speed);
                bug3_sprite.UpdateSprite();
                bug4_sprite.move(0, -bug_speed);
                bug4_sprite.UpdateSprite();
                bug5_sprite.move(0, -bug_speed);
                bug5_sprite.UpdateSprite();
                bug6_sprite.move(0, -bug_speed);
                bug6_sprite.UpdateSprite();
                bug7_sprite.move(0, -bug_speed);
                bug7_sprite.UpdateSprite();
                bug8_sprite.move(0, -bug_speed);
                bug8_sprite.UpdateSprite();

                if (bug1_sprite.getY() < 300 * scale) {
                    //System.out.println("bug1 ARRIVED");
                    bug1_sprite.setY(10000f);
                    bug1_sprite.UpdateSprite();
                    number_bugs_reached_spider++;

                }
                if (bug2_sprite.getY() < 300 * scale) {
                    //System.out.println("bug2 ARRIVED");
                    bug2_sprite.setY(10000f);
                    bug2_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug3_sprite.getY() < 300 * scale) {
                    //System.out.println("bug3 ARRIVED");
                    bug3_sprite.setY(10000f);
                    bug3_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug4_sprite.getY() < 300 * scale) {
                    //System.out.println("bug4 ARRIVED");
                    bug4_sprite.setY(10000f);
                    bug4_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug5_sprite.getY() < 300 * scale) {
                    //System.out.println("bug5 ARRIVED");
                    bug5_sprite.setY(10000f);
                    bug5_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug6_sprite.getY() < 300 * scale) {
                    //System.out.println("bug6 ARRIVED");
                    bug6_sprite.setY(10000f);
                    bug6_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug7_sprite.getY() < 300 * scale) {
                    //System.out.println("bug7 ARRIVED");
                    bug7_sprite.setY(10000f);
                    bug7_sprite.UpdateSprite();
                    number_bugs_reached_spider++;
                }
                if (bug8_sprite.getY() < 300 * scale) {
                    bug8_sprite.setY(10000f);
                    bug8_sprite.UpdateSprite();
                }

                last_bugs_updated = now;

            }
            if (number_bugs_reached_spider > 6) {
                number_bugs_reached_spider = 0;
                number_lives_left--;
                if (number_lives_left < 0) {
                    playDeathAnimation();
                } else {
                    spiderSound.playSound(SoundManager.LIFE_LOST);
                    sign_lifelost_timer = 500;
                }

            }


            lives_manager.removeAll();

            switch (number_lives_left) {
                case 0:
                    lives_manager.addTileObject(dead_spider_x1_sprite);
                    lives_manager.addTileObject(dead_spider_x2_sprite);
                    lives_manager.addTileObject(dead_spider_x3_sprite);
                    lives_manager.addTileObject(dead_spider_x4_sprite);
                    lives_manager.addTileObject(dead_spider_x5_sprite);

                    break;
                case 1:
                    lives_manager.addTileObject(dead_spider_x1_sprite);
                    lives_manager.addTileObject(dead_spider_x2_sprite);
                    lives_manager.addTileObject(dead_spider_x3_sprite);
                    lives_manager.addTileObject(dead_spider_x4_sprite);
                    break;
                case 2:
                    lives_manager.addTileObject(dead_spider_x1_sprite);
                    lives_manager.addTileObject(dead_spider_x2_sprite);
                    lives_manager.addTileObject(dead_spider_x3_sprite);
                    break;
                case 3:
                    lives_manager.addTileObject(dead_spider_x1_sprite);
                    lives_manager.addTileObject(dead_spider_x2_sprite);
                    break;
                case 4:
                    lives_manager.addTileObject(dead_spider_x1_sprite);
                    break;
                case 5:
                    lives_manager.removeAll();
                    break;

            }

            switch (number_bugs_reached_spider) {
                case 0:

                    break;
                case 1:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    break;
                case 2:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    lives_manager.addTileObject(bug_counter2_sprite);
                    break;
                case 3:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    lives_manager.addTileObject(bug_counter2_sprite);
                    lives_manager.addTileObject(bug_counter3_sprite);
                    break;
                case 4:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    lives_manager.addTileObject(bug_counter2_sprite);
                    lives_manager.addTileObject(bug_counter3_sprite);
                    lives_manager.addTileObject(bug_counter4_sprite);
                    break;
                case 5:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    lives_manager.addTileObject(bug_counter2_sprite);
                    lives_manager.addTileObject(bug_counter3_sprite);
                    lives_manager.addTileObject(bug_counter4_sprite);
                    lives_manager.addTileObject(bug_counter5_sprite);

                    break;
                case 6:
                    lives_manager.addTileObject(bug_counter1_sprite);
                    lives_manager.addTileObject(bug_counter2_sprite);
                    lives_manager.addTileObject(bug_counter3_sprite);
                    lives_manager.addTileObject(bug_counter4_sprite);
                    lives_manager.addTileObject(bug_counter5_sprite);
                    lives_manager.addTileObject(bug_counter6_sprite);

                    break;

            }


            lives_manager.PrepareDraw();


            //  CREATE BUGS if game not won or lost a
            //  AND
            //  TIME CONDITION: (now - last_bugs_created) > bug_creation_counter

            if ((last_bugs_created == 0 || (now - last_bugs_created) > bug_creation_counter)
                    && (!gameIsLost && !gameIsWon)) {

                //System.out.println("-------- CREATING bug# " + bugCounter + " -----------");
                //  int type = (int) Math.round(Math.random() * 4);
                float pos = Math.round(Math.random() * 7);
                pos = 150 * scale + (pos * 100) * scale;

                //System.out.println("bug array size:" + bugs.size());
                switch (bugCounter) {
                    case 0:
                        bug1_sprite.setX(pos);
                        bug1_sprite.setY(currScreenHeight - 500 * scale);
                        bug1_sprite.UpdateSprite();
                        bugs.add(bug1_sprite);
                        break;
                    case 1:
                        bug2_sprite.setX(pos);
                        bug2_sprite.setY(currScreenHeight - 500 * scale);
                        bug2_sprite.UpdateSprite();
                        bugs.add(bug2_sprite);
                        break;
                    case 2:
                        bug3_sprite.setX(pos);
                        bug3_sprite.setY(currScreenHeight - 500 * scale);
                        bug3_sprite.UpdateSprite();
                        bugs.add(bug3_sprite);
                        break;
                    case 3:
                        bug4_sprite.setX(pos);
                        bug4_sprite.setY(currScreenHeight - 500 * scale);
                        bug4_sprite.UpdateSprite();
                        bugs.add(bug4_sprite);
                        break;
                    case 4:
                        bug5_sprite.setX(pos);
                        bug5_sprite.setY(currScreenHeight - 500 * scale);
                        bug5_sprite.UpdateSprite();
                        bugs.add(bug5_sprite);
                        break;
                    case 5:
                        bug6_sprite.setX(pos);
                        bug6_sprite.setY(currScreenHeight - 500 * scale);
                        bug6_sprite.UpdateSprite();
                        bugs.add(bug6_sprite);
                        break;
                    case 6:
                        bug7_sprite.setX(pos);
                        bug7_sprite.setY(currScreenHeight - 500 * scale);
                        bug7_sprite.UpdateSprite();
                        bugs.add(bug7_sprite);
                        break;
                    case 7:
                        bug8_sprite.setX(pos);
                        bug8_sprite.setY(currScreenHeight - 500 * scale);
                        bug8_sprite.UpdateSprite();
                        bugs.add(bug8_sprite);
                        break;
                }
                activeBug = bugCounter;
                bugCounter++;
                if (bugCounter > 6) {
                    bugCounter = 0;
                }
                if (bugCounter == 6) {
                    int max_value = (int) Math.abs(Math.floor(8 - level));
                    float red_bug = (int) Math.floor(Math.random() * max_value);
                    if (red_bug == 0) {
                        bugCounter = 7;
                    }
                }

                last_bugs_created = now;
            }

            banners_manager.removeAll();


            // IF THERE IS A SIGN TO SHOW
            if (sign_level_timer > -2 | sign_start_timer > -2 | sign_lifelost_timer > -2 |
                    sign_shots_timer > -2 | sign_won_timer > -2 | sign_lost_timer > -2) {

                banners_manager.removeAll();


                float ypos = center_stage.y + 1 * scale;
                float xpos = center_stage.x + 1 * scale;

                if (sign_level_timer >= 0) {
                    System.out.println("Sign_ level_up_sprite pos(x,y):(" + level_up_sprite.x + "," + level_up_sprite.y + ")");
                    level_up_sprite.move(0, 4 * scale);
                    banners_manager.addTileObject(level_up_sprite);
                    sign_level_timer--;
                }
                if (sign_level_timer == -1) {
                    sign_level_timer = -2;
                    level_up_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(level_up_sprite);

                }


                if (sign_shots_timer >= 0) {
                    extra_shots_sprite.move(0, 4 * scale);
                    banners_manager.addTileObject(extra_shots_sprite);
                    sign_shots_timer--;
                }
                if (sign_shots_timer == -1) {
                    extra_shots_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(extra_shots_sprite);
                    sign_shots_timer = -2;

                }


                if (sign_lifelost_timer >= 0) {
                    life_lost_sprite.move(0, -4 * scale);
                    banners_manager.addTileObject(life_lost_sprite);
                    sign_lifelost_timer--;
                }
                if (sign_lifelost_timer == -1) {
                    life_lost_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(life_lost_sprite);
                    sign_lifelost_timer = -2;

                }


                if (sign_start_timer >= 0) {
                    start_sprite.move(0, -8 * scale);
                    banners_manager.addTileObject(start_sprite);
                    sign_start_timer--;
                }
                if (sign_start_timer == -1) {
                    start_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(start_sprite);
                    sign_start_timer = -2;

                }


                if (sign_won_timer >= 0) {
                    won_sprite.move(0, 3f * scale);
                    banners_manager.addTileObject(won_sprite);
                    sign_won_timer--;
                }

                if (sign_won_timer == -1) {
                    sign_won_timer = -2;
                    won_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(won_sprite);

                    score = 0;
                    level = 1;
                    try {
                        setLevel(level);
                        setScore(score);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    number_bugs_reached_spider = 0;
                    number_lives_left = 5;
                    returnToPauseMenu();
                    isPaused = true;
                    System.out.println("Ended showing WON sign. isPaused = true");
                }

                if (sign_lost_timer >= 0) {
                    lost_sprite.move(0, -2f * scale);
                    banners_manager.addTileObject(lost_sprite);
                    sign_lost_timer--;
                }
                if (sign_lost_timer == -1) {
                    sign_lost_timer = -2;
                    lost_sprite.moveTo(xpos - 500 * scale, ypos - 500 * scale);
                    banners_manager.removeTileObject(lost_sprite);
                    level = 1;
                    score = 0;
                    try {
                        setLevel(level);
                        setScore(score);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    number_bugs_reached_spider = 0;
                    number_lives_left = 5;
                    returnToPauseMenu();
                    isPaused = true;
                    System.out.println("Ended showing LOST sign. isPaused = true");

                }
                banners_manager.PrepareDraw();

            }
        }// END OF BLOCK THAT RUNS WHEN NOT PAUSED


        if (permanent_images != null && permanent_images.size() > 0) {
            // Update our permanent sprites

            for (Sprite tmp : permanent_images) {
                tmp.UpdateSprite();
            }
        }
        if (menu_images != null && menu_images.size() > 0) {
            // Update our permanent sprites
            for (Sprite tmp : menu_images) {
                tmp.UpdateSprite();
            }
        }

        spider_sprite.UpdateSprite();


        // Render all
        Render(mtrxProjectionAndView);
        // Save the current time to see how long it took :).


        // Render the text
        if (tm != null && !isPaused) {
            tm.Draw(mtrxProjectionAndView);
        }

        // Render the stones from image tile
        if (stones_manager != null && !isPaused) {
            stones_manager.Draw(mtrxProjectionAndView);
        }

        // Render the bugs and "x" from image tile
        if (lives_manager != null && !isPaused) {
            lives_manager.Draw(mtrxProjectionAndView);
        }

        // Render the signs when needed from image tile
        if (banners_manager != null && !isPaused) {
            banners_manager.Draw(mtrxProjectionAndView);
        }


        mLastTime = now;
    }

    private synchronized void Render(float[] matrix) {


        // Set our shaderprogram to image shader
        GLES20.glUseProgram(riGraphicTools.sp_Image);


        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // No culling of back faces
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // No depth testing
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_ALWAYS);

        // Enable blending
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        int mTexCoordLoc = 0;

        if (permanent_images != null && permanent_images.size() > 0) {
            renderSpriteArray(permanent_images, mPositionHandle, mTexCoordLoc, matrix);
        }

        renderSprite(dead_bug_sprite_1, mPositionHandle, mTexCoordLoc, matrix);

        if (bugs != null && bugs.size() > 0) {
            renderSpriteArray(bugs, mPositionHandle, mTexCoordLoc, matrix);
        }

        renderSprite(bullet_sprite, mPositionHandle, mTexCoordLoc, matrix);
        renderSprite(spider_sprite, mPositionHandle, mTexCoordLoc, matrix);

        if (isPaused) {
            if (menu_images != null && menu_images.size() > 0) {
                renderSpriteArray(menu_images, mPositionHandle, mTexCoordLoc, matrix);
            }
        }
        renderSprite(pause_button, mPositionHandle, mTexCoordLoc, matrix);


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);

    }

    private synchronized void renderSpriteArray(ArrayList<Sprite> theSpriteArray, int mPositionHandle, int mTexCoordLoc, float[] matrix) {

        for (Iterator<Sprite> sprite_iterator = theSpriteArray.iterator(); sprite_iterator.hasNext(); ) {

            Sprite tmp = sprite_iterator.next();

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, tmp.getVertexBuffer());

            // Get handle to texture coordinates location
            mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");

            // Enable generic vertex attribute array
            GLES20.glEnableVertexAttribArray(mTexCoordLoc);

            // Prepare the texturecoordinates
            GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, tmp.getUvBuffer());

            // Get handle to shape's transformation matrix
            int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, matrix, 0);

            // Get handle to textures locations
            int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");

            // Set the sampler texture unit to sprite.texture_position, where we have saved the texture.
            GLES20.glUniform1i(mSamplerLoc, tmp.getTexturePos());

            // Draw the triangles
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, tmp.getIndices().length, GLES20.GL_UNSIGNED_SHORT, tmp.getDrawListBuffer());

        }
    }

    private synchronized void renderSprite(Sprite theSprite, int mPositionHandle, int mTexCoordLoc, float[] matrix) {


        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, theSprite.getVertexBuffer());

        // Get handle to texture coordinates location
        mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, theSprite.getUvBuffer());

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, matrix, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");

        // Set the sampler texture unit to sprite.texture_position, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, theSprite.getTexturePos());

        // Draw the triangles
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, theSprite.getIndices().length, GLES20.GL_UNSIGNED_SHORT, theSprite.getDrawListBuffer());

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {


        // We need to know the current width and height.
        currScreenWidth = width;
        currScreenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) currScreenWidth, (int) currScreenHeight);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, currScreenWidth, 0.0f, currScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);


    }

    private void goToPontus() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pontusdd.com"));
        ((MainActivity) (mContext)).startActivity(browserIntent);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // Load image as textures
        // texture position marker
        background_sprite.SetupImage("drawable/background", 0, texturenames);

        moveButton_sprite.SetupImage("drawable/control_stick", 1, texturenames);
        shootButton_sprite.SetupImage("drawable/shoot_button", 2, texturenames);

        spider_sprite.SetupImage("drawable/spider", 3, texturenames);

        bullet_sprite.SetupImage("drawable/web_shoot", 4, texturenames);

        bug1_sprite.SetupImage("drawable/bug1", 5, texturenames);
        bug2_sprite.SetupImage("drawable/bug2", 6, texturenames);
        bug3_sprite.SetupImage("drawable/bug1", 7, texturenames);
        bug4_sprite.SetupImage("drawable/bug2", 8, texturenames);
        bug5_sprite.SetupImage("drawable/bug1", 9, texturenames);
        bug6_sprite.SetupImage("drawable/bug2", 10, texturenames);
        bug7_sprite.SetupImage("drawable/bug1", 11, texturenames);
        bug8_sprite.SetupImage("drawable/bug_red1", 12, texturenames);

        dead_bug_sprite_1.SetupImage("drawable/bug_hit2", 13, texturenames);


        menu_back_sprite.SetupImage("drawable/message_board", 14, texturenames);
        musicVol_sprite.SetupImage("drawable/music_vol_control", 15, texturenames);
        musicControl_sprite.SetupImage("drawable/volume_control_stick", 16, texturenames);
        soundVol_sprite.SetupImage("drawable/sound_vol_control", 17, texturenames);
        soundControl_sprite.SetupImage("drawable/volume_control_stick", 18, texturenames);
        play_sprite.SetupImage("drawable/play_button", 19, texturenames);
        exit_sprite.SetupImage("drawable/exit_button", 20, texturenames);

        pause_button.SetupImage("drawable/pause_button", 21, texturenames);


        about_sprite.SetupImage("drawable/about_pontus", 22, texturenames);

        confirm_yes_sprite.SetupImage("drawable/confirm_yes", 23, texturenames);
        confirm_no_sprite.SetupImage("drawable/confirm_no", 24, texturenames);


        confirm_text_sprite.SetupImage("drawable/confirm_text", 25, texturenames);
        connect_button_sprite.SetupImage("drawable/connect_button", 29, texturenames);


        SetupText();


        isSurfaceCreated = true;

        //set button as inactive by default
        shootingButtonActive = false;
        // the middle of the control stick (position 0)
        middleXpos = moveButton_sprite.getX();
        minXpos = middleXpos - 150 * scale;
        maxXpos = middleXpos + 150 * scale;


        // the middle of the control stick of the volume control menus (position 0)
        middleXVCpos = musicVol_sprite.getX();
        minXVCpos = middleXVCpos - 150 * scale;
        maxXVCpos = middleXVCpos + 150 * scale;


        // Set the clear color to black
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1);


        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        // Create the shaders, solid color
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);
        // create empty OpenGL ES Program
        riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();
        // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader);
        // creates OpenGL ES program executables
        GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);


        // Create the shaders, images
        vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);
        // create empty OpenGL ES Program
        riGraphicTools.sp_Image = GLES20.glCreateProgram();
        // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader);
        // creates OpenGL ES program executables
        GLES20.glLinkProgram(riGraphicTools.sp_Image);

        // Text shader
        int vshadert = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Text);
        int fshadert = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Text);

        riGraphicTools.sp_Text = GLES20.glCreateProgram();
        GLES20.glAttachShader(riGraphicTools.sp_Text, vshadert);
        GLES20.glAttachShader(riGraphicTools.sp_Text, fshadert);        // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Text);                  // creates OpenGL ES program executables


        // Set our shader programm
        GLES20.glUseProgram(riGraphicTools.sp_Image);


        try {
            setLevel(level);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public boolean isSurfaceCreated() {
        return isSurfaceCreated;
    }

    public void processTouchEvent(MotionEvent event) {

        if (isPaused) {
            int pointerCount = event.getPointerCount();

            for (int i = 0; i < pointerCount; i++) {
                final float x = event.getX(i);
                final float y = event.getY(i);
                int id = event.getPointerId(i);
                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        exit_sprite.handleActionDown(x, (currScreenHeight - y));
                        if (showPlayButton) {
                            play_sprite.handleActionDown(x, (currScreenHeight - y));
                        } else {
                            connect_button_sprite.handleActionDown(x, (currScreenHeight - y));
                        }
                        musicControl_sprite.handleActionDown(x, (currScreenHeight - y));
                        soundControl_sprite.handleActionDown(x, (currScreenHeight - y));
                        confirm_no_sprite.handleActionDown(x, (currScreenHeight - y));
                        confirm_yes_sprite.handleActionDown(x, (currScreenHeight - y));
                        about_sprite.handleActionDown(x, (currScreenHeight - y));

                        if (about_sprite.isTouched()) {
                            goToPontus();
                        }

                        if (play_sprite.isTouched()) {

                            spiderSound.playSound(SoundManager.DEFEND_NEST);
                            isPaused = false;
                            sign_start_timer = 500;
                            play_sprite.setNotTouched();
 //                           ((MainActivity) mContext).hideBanner();
                        }
                        if (connect_button_sprite.isTouched()) {
                            connect_button_sprite.setNotTouched();
                            ((MainActivity) mContext).checkInternetConnection();
                        }
                        if (exit_sprite.isTouched()) {
                            exit_sprite.setNotTouched();
                            confirmExit();
                        }
                        if (musicControl_sprite.isTouched()) {
                            musicControl_sprite.setScale(scale * 1.3F);
                            musicControl_sprite.UpdateSprite();
                        }
                        if (soundControl_sprite.isTouched()) {
                            soundControl_sprite.setScale(scale * 1.3F);
                            soundControl_sprite.UpdateSprite();
                        }
                        if (confirm_no_sprite.isTouched()) {
                            returnToPauseMenu();
                        }
                        if (confirm_yes_sprite.isTouched()) {
                            onDestroy();
                        }
                        break;


                    case MotionEvent.ACTION_POINTER_DOWN:

                        exit_sprite.handleActionDown(x, (currScreenHeight - y));
                        if (showPlayButton) {
                            play_sprite.handleActionDown(x, (currScreenHeight - y));
                        } else {
                            connect_button_sprite.handleActionDown(x, (currScreenHeight - y));
                        }
                        musicControl_sprite.handleActionDown(x, (currScreenHeight - y));
                        soundControl_sprite.handleActionDown(x, (currScreenHeight - y));
                        confirm_no_sprite.handleActionDown(x, (currScreenHeight - y));
                        confirm_yes_sprite.handleActionDown(x, (currScreenHeight - y));
                        about_sprite.handleActionDown(x, (currScreenHeight - y));

                        if (about_sprite.isTouched()) {
                            goToPontus();
                        }

                        if (play_sprite.isTouched()) {
                            isPaused = false;
                            sign_start_timer = 500;
                            play_sprite.setNotTouched();
                            spiderSound.playSound(SoundManager.DEFEND_NEST);
//                            ((MainActivity) mContext).hideBanner();
                        }
                        if (connect_button_sprite.isTouched()) {
                            connect_button_sprite.setNotTouched();
                            ((MainActivity) mContext).checkInternetConnection();
                        }
                        if (exit_sprite.isTouched()) {
                            exit_sprite.setNotTouched();
                            confirmExit();
                        }
                        if (musicControl_sprite.isTouched()) {
                            musicControl_sprite.setScale(scale * 1.3F);
                            musicControl_sprite.UpdateSprite();
                        }
                        if (soundControl_sprite.isTouched()) {
                            soundControl_sprite.setScale(scale * 1.3F);
                            soundControl_sprite.UpdateSprite();
                        }
                        if (confirm_no_sprite.isTouched()) {
                            returnToPauseMenu();
                        }
                        if (confirm_yes_sprite.isTouched()) {
                            onDestroy();
                        }
                        break;


                    case MotionEvent.ACTION_MOVE:

                        if (musicControl_sprite.isTouched()) {
                            // the stick is being dragged
                            float yPos = musicControl_sprite.getY();
                            float xPos = event.getX();
                            if (xPos >= minXVCpos && xPos <= maxXVCpos) {
                                musicControl_sprite.moveTo(event.getX(), yPos);
                                musicControl_sprite.UpdateSprite();
                                //is control is near center (40 - center to center + 40)
                                if (xPos < (middleXVCpos + 10 * scale) && xPos > (middleXVCpos - 10 * scale)) {
                                    ((MainActivity) mContext).setMusicVolume(0.5f);
                                } else {
                                    if (xPos > middleXVCpos) {
                                        ((MainActivity) mContext).setMusicVolume(0.75f);
                                    } else {
                                        ((MainActivity) mContext).setMusicVolume(0.25f);
                                    }
                                }
                            } else if (xPos > maxXVCpos) {
                                ((MainActivity) mContext).setMusicVolume(1f);
                                musicControl_sprite.moveTo(maxXVCpos, yPos);
                                musicControl_sprite.UpdateSprite();
                            } else if (xPos < minXVCpos) {
                                ((MainActivity) mContext).setMusicVolume(0f);
                                musicControl_sprite.moveTo(minXVCpos, yPos);
                                musicControl_sprite.UpdateSprite();
                            }
                        }

                        if (soundControl_sprite.isTouched()) {
                            // the stick is being dragged
                            float yPos = soundControl_sprite.getY();
                            float xPos = event.getX();
                            if (xPos >= minXVCpos && xPos <= maxXVCpos) {
                                soundControl_sprite.moveTo(event.getX(), yPos);
                                soundControl_sprite.UpdateSprite();
                                //is control is near center (40 - center to center + 40)
                                if (xPos < (middleXVCpos + 10 * scale) && xPos > (middleXVCpos - 10 * scale)) {
                                    spiderSound.setVolumeLevel(50);
                                } else {
                                    if (xPos > middleXVCpos) {
                                        spiderSound.setVolumeLevel(75);
                                    } else {
                                        spiderSound.setVolumeLevel(25);
                                    }
                                }
                            } else if (xPos > maxXVCpos) {
                                soundControl_sprite.moveTo(maxXVCpos, yPos);
                                spiderSound.setVolumeLevel(100);
                                soundControl_sprite.UpdateSprite();
                            } else if (xPos < minXVCpos) {
                                soundControl_sprite.moveTo(minXVCpos, yPos);
                                spiderSound.setVolumeLevel(0);
                                soundControl_sprite.UpdateSprite();
                            }
                        }
                        break;
                    default:
                        System.out.println("SWITCH(" + action + ") has no case. On default (do nothing).");
                        break;

                }//CLOSE SWITCH

                ////System.out.println("ACTION: " + actionString);
            }//CLOSE FOR LOOP
        } // END IF ON PAUSE

        // RUN WHEN NOT PAUSED
        else {


            float orientation = event.getOrientation();
            int pointerCount = event.getPointerCount();

            for (int i = 0; i < pointerCount; i++) {
                final float x = event.getX(i);
                final float y = event.getY(i);
                int id = event.getPointerId(i);
                int action = event.getActionMasked();
                int actionIndex = event.getActionIndex();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        //see if EVENT hits objects
                        ////System.out.println("(x,y) = (" + x + "," + y + ")");
                        moveButton_sprite.handleActionDown(x, (currScreenHeight - y));
                        shootButton_sprite.handleActionDown(x, (currScreenHeight - y));
                        pause_button.handleActionDown(x, (currScreenHeight - y));

                        if (shootButton_sprite.isTouched() && !shootingButtonActive) {
                            shootingButtonActive = true;
                            //   float tmp = shootButton_sprite.getScale() * .8f;
                            shootButton_sprite.setScale(scale * .8f);
                            shoot();
                        }
                        if (pause_button.isTouched() && !isPaused) {
                            pause_button.setScale(scale * .6f);
                            pause_button.UpdateSprite();
                            //onPause();
                            returnToPauseMenu();
                            isPaused = true;
                            ((MainActivity) mContext).onPause();
                        }


                        break;

                    case MotionEvent.ACTION_UP:
                        // TO-DO
                        if (moveButton_sprite.isTouched()) {
                            moveButton_sprite.setNotTouched();
                            moveButton_sprite.setX(middleXpos);
                        }
                        if (shootButton_sprite.isTouched()) {
                            shootButton_sprite.setScale(scale);
                            shootButton_sprite.setNotTouched();
                            shootingButtonActive = false;
                        }
                        break;


                    case MotionEvent.ACTION_POINTER_DOWN:

                        moveButton_sprite.handleActionDown(x, (currScreenHeight - y));
                        shootButton_sprite.handleActionDown(x, (currScreenHeight - y));
                        pause_button.handleActionDown(x, (currScreenHeight - y));

                        if (shootButton_sprite.isTouched() && !shootingButtonActive) {
                            shootingButtonActive = true;
                            // float tmp = shootButton_sprite.getScale() * .8f;
                            shootButton_sprite.setScale(scale * .8f);
                            shoot();
                        }

                        if (pause_button.isTouched() && !isPaused) {
                            pause_button.setScale(scale * .6f);
                            pause_button.UpdateSprite();
                            //onPause();
                            returnToPauseMenu();
                            isPaused = true;
                            ((MainActivity) mContext).onPause();
                        }

                        break;


                    case MotionEvent.ACTION_POINTER_UP:

                        if (shootButton_sprite.isTouched()) {
                            shootButton_sprite.setScale(scale);
                            shootButton_sprite.setNotTouched();
                            shootingButtonActive = false;
                        }
                        moveButton_sprite.UpdateSprite();
                        // do nothing. there is still a
                        // touch event active
                        break;


                    case MotionEvent.ACTION_MOVE:
                        // TO-DO
                        if (moveButton_sprite.isTouched()) {
                            // the stick is being dragged

                            float yPos = moveButton_sprite.getY();
                            float xPos = event.getX();

                            float spider_step = 5;

                            if (xPos >= minXpos && xPos <= maxXpos) {
                                moveButton_sprite.moveTo(event.getX(), yPos);
                            } else if (xPos > maxXpos) {
                                spider_step = 20;
                                moveButton_sprite.moveTo(maxXpos, yPos);
                            } else if (xPos < minXpos) {
                                spider_step = 20;
                                moveButton_sprite.moveTo(minXpos, yPos);
                            }
                            if (moveButton_sprite.getX() > middleXpos) {
                                if (spider_sprite.getX() < (currScreenWidth - 100 * scale)) {
                                    spider_sprite.move(spider_step, 0);
                                    spiderSound.playSound(SoundManager.SPIDER_STEP);
                                }
                            }
                            if (moveButton_sprite.getX() < middleXpos) {
                                if (spider_sprite.getX() > (100 * scale)) {
                                    spider_sprite.move(-spider_step, 0);
                                    spiderSound.playSound(SoundManager.SPIDER_STEP);
                                }
                            }
                        }
                        if (shootButton_sprite.isTouched() && !shootingButtonActive) {
                            shootingButtonActive = true;
                            // float tmp = shootButton_sprite.getScale() * .8f;
                            shootButton_sprite.setScale(scale * .8f);
                            shoot();

                        }
                        break;
                    default:
                        break;

                }//CLOSE SWITCH
            }//CLOSE FOR LOOP
        }// CLOSE ELSE: RUN WHEN NOT PAUSED
    }//CLOSE METHOD

    private void returnToPauseMenu() {
        play_sprite.setScale(scale * 1.5f);
        exit_sprite.setScale(scale * 1.5f);
        confirm_no_sprite.setScale(0);
        confirm_text_sprite.setScale(0);
        confirm_yes_sprite.setScale(0);
        returningFromPause = true;
    }

    private void confirmExit() {
        play_sprite.setScale(0);
        exit_sprite.setScale(0);
        confirm_no_sprite.setScale(scale * 1);
        confirm_text_sprite.setScale(scale * 1.3f);
        confirm_yes_sprite.setScale(scale * 1);
    }

    public void shoot() {
        if (!shooting) {
            shooting = true;
            bullet_sprite.setX(spider_sprite.getX());
            bullet_sprite.setY(spider_sprite.getY());
            spiderSound.playSound(SoundManager.FIRE_SHOT);
            //bugs.add(bullet_sprite);
        }
    }

    private synchronized void setScore(int score_value) throws IOException {


        // Position from center
        float ypos = center_stage.y + 725 * scale;
        float xpos = center_stage.x - 400 * scale;

        // Create our new textobject
        String tmp = String.valueOf(score);
        while (tmp.length() < 3) {
            tmp = "0" + tmp;
        }
        tm.removeText(score_text);
        score_text = new TextObject(tmp, xpos, ypos);
        tm.addText(score_text);
        // Prepare the text for rendering
        tm.PrepareDraw();
    }


    private void playWonAnimation() {

        spiderSound.playSound(SoundManager.SOUND_WON);
        gameIsWon = true;
        sign_won_timer = 500;

    }

    private void playDeathAnimation() {

        gameIsLost = true;
        spiderSound.playSound(SoundManager.GAME_OVER);
        sign_lost_timer = 500;

    }


    public void SetupText() {


        //  for the text texture
        int id = mContext.getResources().getIdentifier("drawable/font", null, mContext.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 26);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[26]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();


        // Create our text manager
        tm = new TextManager();

        // Tell our text manager to use index 35 of textures loaded
        tm.setTextureID(26);

        // Pass the uniform scale
        tm.setUniformscale(scale * 2);

        // Position from center
        float ypos = center_stage.y + 725 * scale;
        float xpos = center_stage.x - 400 * scale;

        // Create our new textobject

        score_text = new TextObject("000", xpos, ypos);

        xpos = center_stage.x + 350 * scale;

        level_text = new TextObject("00", xpos, ypos);

        // Position from center
        ypos = center_stage.y * scale;
        xpos = center_stage.x * scale;


        // Add it to our manager
        tm.addText(score_text);
        tm.addText(level_text);

        // Prepare the text for rendering
        tm.PrepareDraw();


        //-----------------------------------------------------------------


        //  for the images tile texture manager
        int id2 = mContext.getResources().getIdentifier("drawable/image_tile", null, mContext.getPackageName());
        Bitmap bmp2 = BitmapFactory.decodeResource(mContext.getResources(), id2);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 27);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[27]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp2, 0);
        bmp2.recycle();


        // Create our image tile managers
        stones_manager = new RocksTileManager();
        lives_manager = new RocksTileManager();

        // Tell our manager to use index 88 of textures loaded
        stones_manager.setTextureID(27);
        lives_manager.setTextureID(27);

        // Pass the uniform scale
        stones_manager.setUniformscale(scale);
        lives_manager.setUniformscale(scale);


        //-----------------------------------------------------------------


        //  for the BANNERS-SIGNS images tile texture manager
        int id3 = mContext.getResources().getIdentifier("drawable/signs_tile_images", null, mContext.getPackageName());
        Bitmap bmp3 = BitmapFactory.decodeResource(mContext.getResources(), id3);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 28);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[28]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp3, 0);
        bmp2.recycle();


        // Create our image tile managers
        banners_manager = new SignsTileManager();

        // Tell our manager to use index of textures loaded
        banners_manager.setTextureID(28);

        // Pass the uniform scale
        banners_manager.setUniformscale(4 * scale);
    }

    public void showConnectioMessagge() {


        if (!showPlayButton) {
            play_sprite.setScale(0f);
            connect_button_sprite.setScale(scale * 1f);
        } else {
            play_sprite.setScale(scale * 1.5f);
            connect_button_sprite.setScale(0f);
        }
        play_sprite.UpdateSprite();
        connect_button_sprite.UpdateSprite();
    }
}