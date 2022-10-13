import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

public class Player {
    private final static String FAE_LEFT = "res/fae/faeLeft.png";
    private final static String FAE_RIGHT = "res/fae/faeRight.png";
    private final static String FAE_ATTACK_RIGHT = "res/fae/faeAttackRight.png";
    private final static String FAE_ATTACK_LEFT = "res/fae/faeAttackLeft.png";
    private final static int MAX_HEALTH_POINTS = 100;
    private final static int DAMAGE_POINTS = 20;
    private final static double MOVE_SIZE = 2;
    private final static int WIN_X = 950;
    private final static int WIN_Y = 670;

    private final static int HEALTH_X = 20;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private final static double REFRESH_RATE = 60.0;
    private final static double MILLISECONDS = 1000.0;
    private final static int ATTACK_STATE_TIME = 1000;
    private final static int COOLDOWN_TIME = 2000;
    private final static int INVINCIBLE_STATE_TIME = 3000;
    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private boolean facingRight;
    private boolean isAttack;
    private boolean isInvincible;
    private int invincible_time;
    private boolean hasAttack;
    private int attackTime;
    private int cooldownTime;
    private boolean startCoolTime;


    public Player(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(FAE_RIGHT);
        this.facingRight = true;
        this.isInvincible = false;
        this.hasAttack = false;
        this.startCoolTime = false;
        this.attackTime = 0;
        this.cooldownTime = 0;
        this.invincible_time = 0;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ShadowDimension gameObject){

        if (input.isDown(Keys.UP)){
            setPrevPosition();
            move(0, -MOVE_SIZE);

        }
        else if (input.isDown(Keys.DOWN)){
            setPrevPosition();
            move(0, MOVE_SIZE);

        }
        else if (input.isDown(Keys.LEFT)){

            setPrevPosition();
            move(-MOVE_SIZE,0);

            if (facingRight) {
                if (!hasAttack) {
                    this.currentImage = new Image(FAE_LEFT);
                }else { this.currentImage = new Image(FAE_ATTACK_LEFT);}
                facingRight = !facingRight;
            }

        }
        else if (input.isDown(Keys.RIGHT)){
            setPrevPosition();
            move(MOVE_SIZE,0);

            if (!facingRight) {
                if (!hasAttack) {
                    this.currentImage = new Image(FAE_RIGHT);
                }else{this.currentImage = new Image(FAE_ATTACK_RIGHT);}
                facingRight = !facingRight;
            }

        }

        if (input.isDown(Keys.A) && !hasAttack && !startCoolTime){

            hasAttack = true;
            if (refreshRateCal(attackTime) <= ATTACK_STATE_TIME){
                if (facingRight){this.currentImage = new Image(FAE_ATTACK_RIGHT);}
                else{this.currentImage = new Image(FAE_ATTACK_LEFT);}
                isAttack = true;
            }
        }else{isAttack = false;}

        if (hasAttack){
            attackTime++;
            if (refreshRateCal(attackTime) == ATTACK_STATE_TIME){
                startCoolTime = true;
                hasAttack = false;
                attackTime = 0;
                isAttack = false;
                if (facingRight){this.currentImage = new Image(FAE_RIGHT);}
                else{this.currentImage = new Image(FAE_LEFT);}
            }
        }

        if (startCoolTime){
            cooldownTime++;
            if (refreshRateCal(cooldownTime) == COOLDOWN_TIME){
                hasAttack = false;
                cooldownTime = 0;
                startCoolTime = false;
            }
        }

        if (isInvincible){
            invincible_time++;
            if (refreshRateCal(invincible_time) == INVINCIBLE_STATE_TIME){
                invincible_time = 0;
                isInvincible = false;
            }
        }

        this.currentImage.drawFromTopLeft(position.x, position.y);
        gameObject.checkCollisions(this);
        renderHealthPoints();
        gameObject.checkOutOfBounds(this);
    }

    /**
     * Method that stores Fae's previous position
     */
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves Fae back to previous position
     */
    public void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if Fae's health has depleted
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Method that checks if Fae has found the gate
     */
    public boolean reachedGate(){
        return (this.position.x >= WIN_X) && (this.position.y >= WIN_Y);
    }

    public Point getPosition() {
        return position;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public static int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    public int getDamagePoints(){
        return DAMAGE_POINTS;
    }

    public boolean playerIsAttacking(){
        return this.isAttack;
    }

    private double refreshRateCal(int time){
        return time/(REFRESH_RATE/MILLISECONDS);
    }

    public Point getCenter(){
        return new Point(position.x + currentImage.getWidth()/2, position.y + currentImage.getHeight()/2);
    }

    public boolean getVisibility(){
        return isInvincible;
    }

    public void setVisibility(boolean isInvincible){
        this.isInvincible = isInvincible;
    }

}