import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final Image BACKGROUND_IMAGE0 = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE1 = new Image("res/background1.png");

    private final static int TITLE_FONT_SIZE = 75;
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int TITLE_X_0 = 260;
    private final static int TITLE_Y_0 = 250;
    private final static int TITLE_X_1 = 350;
    private final static int TITLE_Y_1 = 350;
    private final static int INS_X_OFFSET = 90;
    private final static int INS_Y_OFFSET = 190;
    private final Font TITLE_FONT = new Font("res/frostbite.ttf", TITLE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    private final static String INSTRUCTION_MESSAGE = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String END_MESSAGE = "GAME OVER!";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static String LEVEL_COMP_MESSAGE = "LEVEL COMPLETE!";
    private final static String STAGE1_INSTRUCTION_MESSAGE = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    private final static double REFRESH_RATE = 60;
    private final static double MILLISECONDS = 1000;
    private final static int THREE_SECONDS = 3000;
    private final static int WALL_ARRAY_SIZE = 52;
    private final static int S_HOLE_ARRAY_SIZE = 5;
    private final static int TREE_ARRAY_SIZE = 15;
    private final static int DEMON_ARRAY_SIZE = 5;
    private final static Wall[] walls = new Wall[WALL_ARRAY_SIZE];
    private final static Sinkhole[] sinkholes0 = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private final static Sinkhole[] sinkholes1 = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private final static Tree[] trees = new Tree[TREE_ARRAY_SIZE];
    private final static Demon[] demons = new Demon[DEMON_ARRAY_SIZE];
    private Navec navec;

    private Point topLeftStage0;
    private Point topLeftStage1;
    private Point bottomRight;
    private Player playerStage0;
    private Player playerStage1;
    private boolean hasStartedStage0;
    private boolean hasStartedStage1;
    private boolean gameOver;
    private int currentHealth;
    private boolean stage1Begin;
    private boolean playerWinStage0;
    private boolean playerWinStage1;
    private int time;


    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSV();
        readCSV1();
        hasStartedStage0 = false;
        hasStartedStage1 = false;
        gameOver = false;
        playerWinStage0 = false;
        stage1Begin = false;
        playerWinStage1 = false;
        time = 0;
    }

    /**
     * Method used to read file and create objects from level0.csv
     */
    private void readCSV(){
        try (BufferedReader reader = new BufferedReader(new FileReader("res/level0.csv"))){

            String line;
            int currentWallCount = 0;
            int currentSinkholeCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        playerStage0 = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Wall":
                        walls[currentWallCount] = new Wall(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentWallCount++;
                        break;
                    case "Sinkhole":
                        sinkholes0[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
                        break;
                    case "TopLeft":
                        topLeftStage0 = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method used to read file and create objects from level1.csv
     */
    private void readCSV1(){

        try (BufferedReader reader = new BufferedReader(new FileReader("res/level1.csv"))){

            String line;
            int currentTreeCount = 0;
            int currentSinkholeCount = 0;
            int currentDemonCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {

                    case "Fae":
                        playerStage1 = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Tree":
                        trees[currentTreeCount] = new Tree(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentTreeCount++;
                        break;
                    case "Sinkhole":
                        sinkholes1[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
                        break;
                    case "Demon":
                        demons[currentDemonCount] = new Demon(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentDemonCount++;
                        break;
                    case "Navec":
                        navec = new Navec(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "TopLeft":
                        topLeftStage1 = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;

                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // Start stage 0
        if(!hasStartedStage0){
            drawStartScreen();
            if (input.wasPressed(Keys.SPACE)){
                hasStartedStage0 = true;
            }
        }

        // Start stage 1
        if(playerWinStage0 && !hasStartedStage1){
            drawStartScreenTransition();
            if (input.wasPressed(Keys.SPACE)){
                hasStartedStage1 = true;
            }
        }

        // Game win and loose
        if (gameOver){
            drawMessage(END_MESSAGE);
        } else if (playerWinStage0 && playerWinStage1) {
            drawMessage(WIN_MESSAGE);
        }

        // game is running for stage 0
        if (hasStartedStage0 && !gameOver && !playerWinStage0){

            BACKGROUND_IMAGE0.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

            // FOR TESTING ONLY --- Skip To Stage 1
            if (input.wasPressed(Keys.W)){
                playerWinStage0 = true;
            }

            for(Wall current: walls){
                current.update();
            }
            for(Sinkhole current: sinkholes0){
                current.update();
            }

            playerStage0.update(input, this);

            if (playerStage0.isDead()){
                gameOver = true;
            }

            if (playerStage0.reachedGate()){
                playerWinStage0 = true;
            }

            currentHealth = playerStage0.getHealthPoints();
        }

        // game is running for stage 1
        if (hasStartedStage1 && !gameOver && playerWinStage0 && !playerWinStage1){

            BACKGROUND_IMAGE1.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

            if (!stage1Begin) {
                playerStage1.setHealthPoints(currentHealth);
                stage1Begin = true;
            }

            for (Sinkhole current: sinkholes1){
                current.update();
            }

            for (Tree current: trees){
                current.update();
            }

            // aggressive for second demon, stationary for others
            for (int i = 0; i < DEMON_ARRAY_SIZE; i++){
                demons[i].update(input,this, i);
            }
            navec.update(input,this);
            playerStage1.update(input, this);

            if (playerStage1.isDead()){
                gameOver = true;
            }

            if (navec.isDead()){
                playerWinStage1 = true;
            }

        }
    }

    /**
     * Method that checks for collisions between Fae and static objects (wall, tree, sinkhole), and performs
     * corresponding actions.
     */
    public void checkCollisions(Player player){

        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        if (hasStartedStage0 && !hasStartedStage1) {
            for (Wall current : walls) {
                Rectangle wallBox = current.getBoundingBox();
                if (faeBox.intersects(wallBox) && !hasStartedStage1) {
                    player.moveBack();
                }
            }

            for (Sinkhole hole0 : sinkholes0) {
                Rectangle holeBox = hole0.getBoundingBox();
                if (hole0.isActive() && faeBox.intersects(holeBox)) {
                    player.setHealthPoints(Math.max(player.getHealthPoints() - hole0.getDamagePoints(), 0));
                    player.moveBack();
                    hole0.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole0.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }
        }

        if (playerWinStage0 && hasStartedStage1){

            for (Sinkhole hole1 : sinkholes1) {
                Rectangle holeBox = hole1.getBoundingBox();
                if (hole1.isActive() && faeBox.intersects(holeBox)) {
                    player.setHealthPoints(Math.max(player.getHealthPoints() - hole1.getDamagePoints(), 0));
                    player.moveBack();
                    hole1.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole1.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }

            for (Tree tree: trees){
                Rectangle treeBox = tree.getBoundingBox();
                if (faeBox.intersects(treeBox)){
                    player.moveBack();
                }
            }

        }


    }

    /**
     * Method that checks if Fae has gone out-of-bounds and performs corresponding action
     */
    public void checkOutOfBounds(Player player){

        Point currentPosition = player.getPosition();

        if (hasStartedStage0 && !playerWinStage0 &&
                ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeftStage0.y) ||
                (currentPosition.x < topLeftStage0.x) || (currentPosition.x > bottomRight.x))){
            player.moveBack();
        }

        if (playerWinStage0 && hasStartedStage1 &&
                ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeftStage1.y) ||
                (currentPosition.x < topLeftStage1.x) || (currentPosition.x > bottomRight.x))){
            player.moveBack();
        }
    }

    /**
     * Method that checks if demon and navec collides with objects and window bound
     * return boolean value to demon class
     */
    public boolean checkEnemiesObjectBound(Demon demon){

        Point currentPosition = demon.getPosition();
        Rectangle demonBox = demon.getBoundBox();

        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeftStage1.y) ||
                (currentPosition.x < topLeftStage1.x) || (currentPosition.x > bottomRight.x)){
            return true;
        }

        for (Sinkhole hole : sinkholes1) {
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && (demonBox.intersects(holeBox))) {
                return true;
            }
        }

        for (Tree tree: trees){
            Rectangle treeBox = tree.getBoundingBox();
            if (demonBox.intersects(treeBox)){
                return true;
            }
        }

        return false;
    }

    /**
     *  Method that render enemies attack behaviour (i.e. shooting fire)
     *  render shooting fire when reach the given distant
     */
    public void enemiesAttackRange(){

        // NAVEC
        Rectangle navecBox = navec.getBoundBox();

        double navecAttackRange = Math.sqrt(Math.pow((playerStage1.getCenter().x - navec.getCenter().x), 2)
                + Math.pow((playerStage1.getCenter().y - navec.getCenter().y), 2));

        if (playerStage1.getCenter().x <= navec.getCenter().x &&
        playerStage1.getCenter().y <= navec.getCenter().y && navecAttackRange <= navec.getAttackRange()){
            navec.drawFire(1, navecBox);
            enemiesAttackOnFae(navec);
        } else if (playerStage1.getCenter().x <= navec.getCenter().x &&
                playerStage1.getCenter().y > navec.getCenter().y && navecAttackRange <= navec.getAttackRange()){
            navec.drawFire(2, navecBox);
            enemiesAttackOnFae(navec);
        } else if (playerStage1.getCenter().x > navec.getCenter().x &&
                playerStage1.getCenter().y <= navec.getCenter().y && navecAttackRange <= navec.getAttackRange()){
            navec.drawFire(3, navecBox);
            enemiesAttackOnFae(navec);
        } else if (playerStage1.getCenter().x > navec.getCenter().x &&
                playerStage1.getCenter().y > navec.getCenter().y && navecAttackRange <= navec.getAttackRange()){
            navec.drawFire(4, navecBox);
            enemiesAttackOnFae(navec);
        }

        // DEMON
        for (Demon demon: demons) {

            Rectangle demonBox = demon.getBoundBox();
            double attackRange = Math.sqrt(Math.pow((playerStage1.getCenter().x - demon.getCenter().x), 2)
                    + Math.pow((playerStage1.getCenter().y - demon.getCenter().y), 2));

            if (!demon.isDead() && playerStage1.getCenter().x <= demon.getCenter().x &&
                    playerStage1.getCenter().y <= demon.getCenter().y && attackRange <= demon.getAttackRange()) {
                demon.drawFire(1, demonBox);
                enemiesAttackOnFae(demon);
            }

            if (!demon.isDead() && playerStage1.getCenter().x <= demon.getCenter().x &&
                    playerStage1.getCenter().y > demon.getCenter().y && attackRange <= demon.getAttackRange()) {
                demon.drawFire(2, demonBox);
                enemiesAttackOnFae(demon);
            }

            if (!demon.isDead() && playerStage1.getCenter().x > demon.getCenter().x &&
                    playerStage1.getCenter().y <= demon.getCenter().y && attackRange <= demon.getAttackRange()) {
                demon.drawFire(3, demonBox);
                enemiesAttackOnFae(demon);
            }

            if (!demon.isDead() && playerStage1.getCenter().x > demon.getCenter().x &&
                    playerStage1.getCenter().y > demon.getCenter().y && attackRange <= demon.getAttackRange()) {
                demon.drawFire(4, demonBox);
                enemiesAttackOnFae(demon);
            }
        }

    }

    /**
     * Method that render fae health when get damaged by the enemies
     * and also track fae's invincible state when being attacked
     */
    private void enemiesAttackOnFae(Demon demon){

        Rectangle faeBox = new Rectangle(playerStage1.getPosition(), playerStage1.getCurrentImage().getWidth(),
                playerStage1.getCurrentImage().getHeight());

        if (faeBox.intersects(demon.getFireBox()) && !demon.getVisibility() && !playerStage1.getVisibility()){
            playerStage1.setVisibility(true);
            playerStage1.setHealthPoints(Math.max(playerStage1.getHealthPoints() - demon.getDamagePoints(), 0));
        }

    }

    /**
     * Method that set demon's and navec's visibility state when being attacked
     * and set their health when being attacked by the player
     */
    public void playerAttackOnEnemies(){

        Rectangle faeBox = new Rectangle(playerStage1.getPosition(), playerStage1.getCurrentImage().getWidth(),
                playerStage1.getCurrentImage().getHeight());

        Rectangle navecBox = navec.getBoundBox();

        if (faeBox.intersects(navecBox) && playerStage1.playerIsAttacking()){

            if (!navec.getVisibility()){
                navec.setHealthPoints(navec.getHealthPoints() - playerStage1.getDamagePoints());
            }
            navec.setVisibility(true);
        }

        for (Demon demon: demons){

            Rectangle demonBox = demon.getBoundBox();

            if (faeBox.intersects(demonBox) && playerStage1.playerIsAttacking()){

                if (!demon.getVisibility()) {
                    demon.setHealthPoints(demon.getHealthPoints() - playerStage1.getDamagePoints());
                }
                demon.setVisibility(true);

            }

        }
    }

    /**
     * Method used to draw the start screen title and instructions
     */
    private void drawStartScreen(){
        TITLE_FONT.drawString(GAME_TITLE, TITLE_X_0, TITLE_Y_0);
        INSTRUCTION_FONT.drawString(INSTRUCTION_MESSAGE,TITLE_X_0 + INS_X_OFFSET, TITLE_Y_0 + INS_Y_OFFSET);
    }

    /**
     * Method used to draw end of Stage 0 transition to start screen and instructions for stage 1
     */
    private void drawStartScreenTransition(){

        if (refreshRateCal(time) < THREE_SECONDS) {
            drawMessage(LEVEL_COMP_MESSAGE);
            time++;
        }
        else {
            INSTRUCTION_FONT.drawString(STAGE1_INSTRUCTION_MESSAGE, TITLE_X_1, TITLE_Y_1);
        }

    }

    /**
     * Method used to draw end screen messages
     */
    private void drawMessage(String message){
        TITLE_FONT.drawString(message, (Window.getWidth()/2.0 - (TITLE_FONT.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (TITLE_FONT_SIZE/2.0)));
    }

    /**
     * Method used to calculate refresh rate
     */
    private double refreshRateCal(int time){
        return time/(REFRESH_RATE/MILLISECONDS);
    }


}