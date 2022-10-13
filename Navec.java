import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Navec extends Demon{

    private static final String NAVEC_LEFT = "res/navec/navecLeft.png";
    private static final String NAVEC_RIGHT = "res/navec/navecRight.png";
    private static final String NAVEC_INVINCIBLE_LEFT = "res/navec/navecInvincibleLeft.PNG";
    private static final String NAVEC_INVINCIBLE_RIGHT = "res/navec/navecInvincibleRight.PNG";
    private static final String NAVEC_FIRE = "res/navec/navecFire.png";

    private final static int MAX_HEALTH_POINTS = 100;
    private static double MOVE_SIZE = 2;
    private final static int NAVEC_ATTACK_RANGE = 200;
    private final static int DAMAGE_POINT = 20;


    private static final double ROTATE_90 = 1.57;
    private static final double ROTATE_180 = 3.14;
    private static final double ROTATE_270 = 4.71;
    private static final int TOP_LEFT = 1;
    private static final int BOTTOM_LEFT = 2;
    private static final int TOP_RIGHT = 3;
    private static final int BOTTOM_RIGHT = 4;
    private static final int INVINCIBLE_STATE = 3000;
    private static final Image FIRE_IMAGE = new Image(NAVEC_FIRE);

    private final static int MAX_TIMESCALE = 3;
    private final static int MIN_TIMESCALE = -3;
    private final static double ORIGINAL_SPEED = MOVE_SIZE;


    public Navec(int startX, int startY){
        super(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(NAVEC_RIGHT);
    }

    public void update(Input input, ShadowDimension gameObject) {

        setPrevPosition();
        timeScale(input);

        if (gameObject.checkEnemiesObjectBound(this)) {
            moveBack();

            isMovingUp = !isMovingUp;

            if (isMovingUp){
                move(0, -MOVE_SIZE);
            }else{
                move(0, MOVE_SIZE);
            }


        } else {

            if (isMovingUp){
                move(0, -MOVE_SIZE);
            }else{
                move(0, MOVE_SIZE);
            }

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
            currentImage = new Image(NAVEC_RIGHT);
            currentImage.drawFromTopLeft(this.position.x, this.position.y);
            renderHealthPoints();
        }

        if (invincible && this.getHealthPoints() > 0){
            hasBecomeInvincible = true;
            currentImage = new Image(NAVEC_INVINCIBLE_RIGHT);
            currentImage.drawFromTopLeft(this.position.x, this.position.y);
            renderHealthPoints();
        }

        gameObject.enemiesAttackRange();
        gameObject.playerAttackOnEnemies();

    }

    /**
     * Method used of timescale control that adjust Navec's speed
     * using user's input
     */
    @Override
    protected void timeScale(Input input){

        int tmp;
        double step = ORIGINAL_SPEED * (50.0/100.0);

        if (input.wasPressed(Keys.L)){
            tmp = timeScale + 1;
            if (MIN_TIMESCALE <= tmp && tmp <= MAX_TIMESCALE) {
                timeScale++;
                MOVE_SIZE += step;
            }
        }

        if (input.wasPressed(Keys.K)){
            tmp = timeScale - 1;
            if (MIN_TIMESCALE <= tmp && tmp <= MAX_TIMESCALE) {
                timeScale--;
                MOVE_SIZE -= step;
            }
        }

    }

    /**
     * Method that renders the current health as percentage on screen
     */
    @Override
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
     * Methods that render fire shooting behaviour for Navec
     */
    @Override
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

    @Override
    protected Rectangle getBoundBox(){ return new Rectangle(position,
            currentImage.getWidth(), currentImage.getHeight());
    }

    @Override
    protected Point getCenter(){
        return new Point(position.x + currentImage.getWidth()/2, position.y + currentImage.getHeight()/2);
    }

    @Override
    protected int getDamagePoints(){
        return DAMAGE_POINT;
    }

    @Override
    protected int getAttackRange(){
        return NAVEC_ATTACK_RANGE;
    }

}