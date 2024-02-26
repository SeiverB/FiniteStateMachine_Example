import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList; // For arbitrary size lists 

import util.Board;
import util.Vector2;

public class AntGame extends JPanel implements MouseListener, MouseMotionListener{
    
    static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 655;

    // add a timer for automatically stepping
    private Timer timer; 

    // Game state
    // 0 = level build, 1 = running level
    private int gameState;

    // Game board
    private Board board;

    public int mousex = 0;
    public int mousey = 0;

    public int numInitAnts;

    public ArrayList<Ant> aliveAnts = new ArrayList<Ant>();

    public Vector2 startPos;

    private static Image foodImage, smallFood, waterImage, poisonImage;

    public AntGame(){

        int boardWidth = 16;
        int boardHeight = 16;
        int cellSize = 42;

        this.gameState = 0;

        // Center board on screen
        Vector2 position = new Vector2((WINDOW_WIDTH - (boardWidth * cellSize)) / 2, 8);

        this.board = new Board(boardWidth, boardHeight, cellSize, position);

        //listen for mouse events (clicks and movements) on this object
        addMouseMotionListener(this);
        addMouseListener(this);

         // Get image for food
        try {                
           BufferedImage foodImage1 = ImageIO.read(getClass().getResourceAsStream("resource/borgir.png"));
           AntGame.foodImage = foodImage1.getScaledInstance(board.cellSize, board.cellSize, Image.SCALE_SMOOTH);
           AntGame.smallFood = foodImage1.getScaledInstance((int)(board.cellSize * 0.23), (int)(board.cellSize * 0.23), Image.SCALE_AREA_AVERAGING);
         } catch (IOException e) {
            e.printStackTrace();
         }

        // Get image for water
        try {                
           BufferedImage waterImage1 = ImageIO.read(getClass().getResourceAsStream("resource/water.png"));
           AntGame.waterImage = waterImage1.getScaledInstance(board.cellSize, board.cellSize, Image.SCALE_SMOOTH);
         } catch (IOException e) {
            e.printStackTrace();
         }
        // Get image for poison
        try {                
           BufferedImage poisonImage1 = ImageIO.read(getClass().getResourceAsStream("resource/poison.png"));
           AntGame.poisonImage = poisonImage1.getScaledInstance(board.cellSize, board.cellSize, Image.SCALE_SMOOTH);
         } catch (IOException e) {
            e.printStackTrace();
         }

    }

    public boolean isInRange(Vector2 position, Vector2 minBounds, Vector2 maxBounds){
        if(((position.x >= minBounds.x) && (position.x <= maxBounds.x)) &&
        ((position.y >= minBounds.y) && (position.y <= maxBounds.y))){
            return true;
        }
        return false;
    }

    public void drawCenteredString(Graphics g, Vector2 pos, String text, Font font){
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        g.drawString(text, Math.round(pos.x - (width / 2)), Math.round(pos.y + (height / 2)));
    }
    
    public void redistribute(){
        this.board.initializeValues();
    }

    public void startGame(){
        this.gameState = 1;
        this.aliveAnts = new ArrayList<Ant>();
        // Set StateMachine board
        StateMachine.setBoard(this.board);

        // Get image for ants
        Image antImage = null;
        try {                
           BufferedImage antImage1 = ImageIO.read(getClass().getResourceAsStream("resource/ant.png"));
           antImage = antImage1.getScaledInstance(board.cellSize, board.cellSize, Image.SCALE_SMOOTH);
         } catch (IOException e) {
            e.printStackTrace();
         }

        this.startPos = Ant.indexToPos(this.board.startIndex);
        this.board.setValue(this.board.startIndex, Board.TileType.HomeBase);
        Ant.setImage(antImage);
        Ant.setHomePosition(startPos);

        for(int i = 0; i < this.numInitAnts; i++){
            Ant newAnt = new Ant(startPos);
            aliveAnts.add(newAnt);
        }

        this.timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStep();
                timer.restart();
            }
        });
    }
    
    public void setFrameAdvanceTime(float time){
        int newTime = Math.round(time * 1000);
        this.timer.setDelay(newTime);
        this.timer.setInitialDelay(newTime);
    }

    public void exitGame(){
        this.gameState = 0;
        this.aliveAnts = new ArrayList<Ant>();
    }

    public void resetGame(){
        this.board.initializeValues();
    }

    public void doStep(){
        for(int i = 0; i < aliveAnts.size(); i++){
            Ant curAnt = aliveAnts.get(i);
            curAnt.updateAnt();

            // Handle special cases for certain states
            switch(curAnt.myState){
                case Die:
                    aliveAnts.remove(curAnt);
                    i = i-1;
                    break;
                case DrinkWater:
                    break;
                case PickupFood:
                    break;
                case Reproduce:
                    Ant newAnt = new Ant(this.startPos); 
                    aliveAnts.add(newAnt);
                    break;
                case ReturnToBase:
                    break;
                case SearchForFood:
                    break;
                case SearchForWater:
                    break;
                default:
                    break;

            }


        }
    }

    public void setPauseState(Boolean state){
        // if we are to be paused
        if(state){
            timer.stop();
        }
        // if we are to be unpaused
        else{
            doStep();
            timer.start();
        }
    }

    public void advanceFrame(){
        doStep();
    }

    // Redraws the graphics on the game window
    public void paintComponent(Graphics g){
        // Set background to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, 800);

        g.setColor(Color.WHITE);
        this.board.drawBoard(g, AntGame.foodImage, AntGame.waterImage, AntGame.poisonImage);

        for(int i = 0; i < aliveAnts.size(); i++){
            Ant currentAnt = aliveAnts.get(i);
            currentAnt.drawAnt(g, AntGame.smallFood);
        }
        g.setColor(Color.WHITE);
        g.drawString("Ants: " + aliveAnts.size(), 4, 16);

    }

    // Capture mouse drag events
    @Override
    public void mouseDragged(MouseEvent e) {
        this.mousex = e.getX();
        this.mousey = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e){
        Vector2 clicked = new Vector2(e.getX(), e.getY());

        if((this.gameState == 0) && isInRange(clicked, board.minBounds, board.maxBounds)){
            this.board.isClickedOn(new Vector2(e.getX(), e.getY()), e.getButton());
        }

    }

    // Capture mouse move events
    @Override
    public void mouseMoved(MouseEvent e) {
        this.mousex = e.getX();
        this.mousey = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e){
    }

    @Override
    public void mouseEntered(MouseEvent e){
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

}