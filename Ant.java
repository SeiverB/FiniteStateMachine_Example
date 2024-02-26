import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import util.Board;
import util.Vector2;

public class Ant extends StateMachine {
    private Vector2 position;
    private int arrPosition;

    public static Vector2 homePosition;

    
    // Image of ant
    private static Image antImage;

    public Ant(Vector2 homePosition){
        super();
        this.position = homePosition;
        
        this.arrPosition = posToArr(this.position);
        this.currentTile = StateMachine.board.getValueFromArr(arrPosition);
    }

    public static Vector2 getHomePosition() {
        return homePosition;
    }

    public static void setHomePosition(Vector2 homePosition) {
        Ant.homePosition = homePosition;
    }

    public static void setImage(Image antImage){
        Ant.antImage = antImage;
    }

    public void updateAnt(){
        // Update State Machine
        update();
        // Update position
        // Navigate home if in RTB state
        if(myState == State.ReturnToBase){
            // Navigate one tile at a time (allows diag)
            Vector2 homeDir = homePosition.subtract(position).normalize();
            homeDir = new Vector2(Math.round(homeDir.x), Math.round(homeDir.y));
            Vector2 newPos = this.position.add(homeDir);
            Boolean validPos = setPosition(newPos);
            
            this.currentTile = StateMachine.board.getValueFromArr(this.arrPosition);
            if(!validPos){
                System.out.println("INVALID POS SOMEHOW!!");
            }
            
        }
        // Randomly wander
        else if(myState == State.SearchForFood || myState == State.SearchForWater){
            int index = StateMachine.getBoard().arrToIndex(arrPosition);
            ArrayList<Integer> neighbours = StateMachine.getBoard().getNeighbours(index);
            int neighIndex = (int)(Math.floor((Math.random() * neighbours.size())));
            this.arrPosition = neighbours.get(neighIndex);
            int newPosition = StateMachine.getBoard().arrToIndex(this.arrPosition);
            this.position = indexToPos(newPosition);
            this.currentTile = StateMachine.board.getValueFromArr(this.arrPosition);
        }

    }

    public int posToArr(Vector2 position){
        int index = (int)position.x + ((int)position.y * StateMachine.getBoard().width);
        return StateMachine.getBoard().indexToArr(index);
    }

    public static Vector2 indexToPos(int index){
        int width = StateMachine.getBoard().width;
        int y = Math.floorDiv(index, width);
        int x = index % width;
        return new Vector2(x, y);
    }

    public Boolean validPosition(int arr){
        if((arr >= 0) && StateMachine.board.getValueFromArr(arr) != Board.TileType.Invalid){
            return true;
        }
        return false;
    }

    // Set ant position
    // Returns true if valid position was provided, else returns false without changing position
    public Boolean setPosition(Vector2 position){
        int arr = posToArr(position);
        if(validPosition(arr)){
            this.position = position;
            this.arrPosition = arr;
            return true;
        }
        return false;
    }

    public void drawAnt(Graphics g, Image img){
        Vector2 imagePos = StateMachine.board.getTileCoords(StateMachine.board.arrToIndex(this.arrPosition));
        int imgWidth = Ant.antImage.getWidth(null);
        int imgHeight = Ant.antImage.getHeight(null);
        g.drawImage(Ant.antImage, Math.round(imagePos.x), Math.round(imagePos.y), imgWidth, imgHeight, null);
        if(myState == State.ReturnToBase){
            Vector2 dim2 = new Vector2(img.getWidth(null)*2, img.getHeight(null)*2);
            Vector2 imagePos2 = imagePos.add(dim2);
            g.drawImage(img, Math.round(imagePos2.x), Math.round(imagePos2.y), (int)dim2.x, (int)dim2.y, null);
        }
        else if(myState == State.SearchForWater){
            g.setColor(Color.BLUE);
            g.drawOval((int)Math.round(imagePos.x + imgWidth*0.25), (int)Math.round(imagePos.y + imgWidth*0.25), (int)Math.round(imgWidth*0.5), (int)Math.round(imgHeight*0.5));
        }
    }

    // Get a random value from -1 to 1
    // That is skewed towards values of either -1 or 1
    public float getRandom(){
        // Random number from 0 to 1, skewed towards 1.
        double a = (Math.random() + Math.random()) / 2;

        // Random number being either 0 or 1, which is used for the sign of the above.
        int b = (int)Math.round(Math.random());

        double result = (b == 0) ? (a) : (-a) ;
        return (float)result;
    }

}
