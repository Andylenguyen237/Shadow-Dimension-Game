import bagel.*;
import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;


public class Demon {

    // LOAD STATIC IMAGE FOR DEMON
    private static final String DEMON_FIRE ="res/demon/demonFire.png";
    private static final String DEMON_RIGHT = "res/demon/demonRight.png";
    private static final String DEMON_LEFT = "res/demon/demonLeft.png";
    private static final String DEMON_INVINCIBLE_LEFT  = "res/demon/demonInvincibleLeft.PNG";
    private static final String DEMON_INVINCIBLE_RIGHT = "res/demon/demonInvincibleRight.PNG";
    private static final int MAX_HEALTH_POINTS = 40;
    private final static int DAMAGE_POINT = 10;
    private static final int DEMON_ATTACK_RANGE = 150;
    private final static Image FIRE_IMAGE = new Image(DEMON_FIRE);

    private static final double MAX_SPEED = 0.7;
    private static final double MIN_SPEED = 0.2;
    private static double RANDOM_SPEED = (MAX_SPEED - MIN_SPEED) * Math.random() + MIN_SPEED;

    protected final static int ORANGE_BOUNDARY = 65;
    protected final static int RED_BOUNDARY = 35;
    protected final static int FONT_SIZE = 15;
    protected final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    protected final static DrawOptions COLOUR = new DrawOptions();
    protected final static Colour GREEN = new Colour(0, 0.8, 0.2);
    protected final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    protected final static Colour RED = new Colour(1, 0, 0);

    private static final double REFRESH_RATE = 60;
    private static final double MILLISECONDS = 1000;
    private static final double ROTATE_90 = 1.57;
    private static final double ROTATE_180 = 3.14;
    private static final double ROTATE_270 = 4.71;
    private static final int TOP_LEFT = 1;
    private static final int BOTTOM_LEFT = 2;
    private static final int TOP_RIGHT = 3;
    private static final int BOTTOM_RIGHT = 4;

    private static final int AGGRESSIVE_DEMON = 0;
    private static final int LEFT_FACING_DEMON = 5;
    private static final int INVINCIBLE_STATE = 3000;
    private static final int MAX_TIMESCALE = 3;
    private static final int MIN_TIMESCALE = -3;
    private final static double ORIGINAL_SPEED = RANDOM_SPEED;

    protected Point position;
    protected Point prevPosition;
    protected int healthPoints;
    protected Image currentImage;
    protected boolean invincible;
    protected boolean isMovingUp;
    protected int invincible_time;
    protected boolean hasBecomeInvincible;
    protected   Rectangle fireBox;
    protected int timeScale;


    public Demon(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(DEMON_RIGHT);
        this.isMovingUp = true;
        this.invincible = false;
        this.hasBecomeInvincible = false;
        this.invincible_time = 0;
        this.timeScale = 0;
        COLOUR.setBlendColour(GREEN);
    }

    public void update(Input input, ShadowDimension gameObject, int index) {

        setPrevPosition();

        if (index == AGGRESSIVE_DEMON) {

            if (gameObject.checkEnemiesObjectBound(this)) {
                moveBack();

                isMovingUp = !isMovingUp;

                if (isMovingUp){
                    move(0, -RANDOM_SPEED);
                }else{
                    move(0, RANDOM_SPEED);
                }


            } else {

                if (isMovingUp){
                    move(0, -RANDOM_SPEED);
                }else{
                    move(0, RANDOM_SPEED);
                }

            }

            timeScale(input);

        }

        if (hasBecomeInvincible){
            invincible_time++;
            if (refreshRateCal(invincible_time) == INVINCIBLE_STATE){
                hasBecomeInvincible = false;
                invincible_time = 0;
                invincible = false;
            }
        }

        if (!invincible && this.getHealthPoints() > 0) {
            if (index == LEFT_FACING_DEMON){
                currentImage = new Image(DEMON_LEFT);
            }else {
                currentImage = new Image(DEMON_RIGHT);
            }
            currentImage.drawFromTopLeft(this.position.x, this.position.y);
            renderHealthPoints();
        }

        if (invincible && this.getHealthPoints() > 0){
            hasBecomeInvincible = true;
            if (index == LEFT_FACING_DEMON){
                currentImage = new Image(DEMON_INVINCIBLE_LEFT);
            }else{
                currentImage = new Image(DEMON_INVINCIBLE_RIGHT);
            }
            currentImage.drawFromTopLeft(this.position.x, this.position.y);
            renderHealthPoints();
        }

        gameObject.enemiesAttackRange();
        gameObject.playerAttackOnEnemies();

    }

    /**
     * Method used for timescale control that adjust Demon's speed
     * using user's input
     */
    protected void timeScale(Input input){

        int tmp;

        double step = ORIGINAL_SPEED * (50.0/100.0);

        if (input.wasPressed(Keys.L)){
            tmp = timeScale + 1;
            if (MIN_TIMESCALE <= tmp && tmp <= MAX_TIMESCALE) {
                timeScale++;
                RANDOM_SPEED += step;
            }
        }

        if (input.wasPressed(Keys.K)){
            tmp = timeScale - 1;
            if (MIN_TIMESCALE <= tmp && tmp <= MAX_TIMESCALE) {
                timeScale--;
                RANDOM_SPEED -= step;
            }
        }

    }

    /**
     * Method that moves Fae back to previous position
     */
    protected void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    protected void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    protected void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        } else{
            COLOUR.setBlendColour(GREEN);
        }
        FONT.drawString(Math.round(percentageHP) + "%", position.x, position.y - 6, COLOUR);
    }

    /**
     * Method that calculate refresh rate of demon's invincible state
     */
    protected double refreshRateCal(int time){
        return time/(REFRESH_RATE/MILLISECONDS);
    }

    /**
     * Method that checks if Fae's health has depleted
     */
    protected boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Methods that render fire shooting behaviour for demon
     */
    protected void drawFire(int num, Rectangle demonBox){

        DrawOptions rotate = new DrawOptions();

        // Top-left
        if (num == TOP_LEFT) {
            FIRE_IMAGE.drawFromTopLeft(demonBox.left() - FIRE_IMAGE.getWidth(), demonBox.top() - FIRE_IMAGE.getHeight());
            this.fireBox = new Rectangle(demonBox.left(), demonBox.top(), FIRE_IMAGE.getWidth(), FIRE_IMAGE.getHeight());
        }

        // Bottom-left
        if (num == BOTTOM_LEFT){
            FIRE_IMAGE.drawFromTopLeft(demonBox.left() - FIRE_IMAGE.getWidth(), demonBox.bottom(), rotate.setRotation(ROTATE_270));
            this.fireBox = new Rectangle(demonBox.left(), demonBox.bottom(), FIRE_IMAGE.getWidth(), FIRE_IMAGE.getHeight());
        }

        // Top-right
        if (num == TOP_RIGHT) {
            FIRE_IMAGE.drawFromTopLeft(demonBox.right(), demonBox.top() - FIRE_IMAGE.getHeight(), rotate.setRotation(ROTATE_90));
            this.fireBox = new Rectangle(demonBox.right(), demonBox.top(), FIRE_IMAGE.getWidth(), FIRE_IMAGE.getHeight());
        }

        // Bottom-right
        if (num == BOTTOM_RIGHT) {
            FIRE_IMAGE.drawFromTopLeft(demonBox.right(), demonBox.bottom(), rotate.setRotation(ROTATE_180));
            this.fireBox = new Rectangle(demonBox.right(), demonBox.bottom(), FIRE_IMAGE.getWidth(), FIRE_IMAGE.getHeight());
        }

    }

    protected void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    protected Point getPosition() {
        return position;
    }

    protected int getHealthPoints() {
        return healthPoints;
    }

    protected void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    protected Rectangle getBoundBox(){ return new Rectangle(position,
            currentImage.getWidth(), currentImage.getHeight());
    }

    protected Point getCenter(){
        return new Point(position.x + currentImage.getWidth()/2, position.y + currentImage.getHeight()/2);
    }

    protected Rectangle getFireBox(){
        return fireBox;
    }

    protected void setVisibility(boolean invincible){
        this.invincible = invincible;
    }

    protected boolean getVisibility(){
        return invincible;
    }

    protected int getDamagePoints(){
        return DAMAGE_POINT;
    }

    protected int getAttackRange(){
        return DEMON_ATTACK_RANGE;
    }

}
