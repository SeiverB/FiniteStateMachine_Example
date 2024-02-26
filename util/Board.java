package util;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.Color;


public class Board {

    public static enum TileType{
        Open, Food, Water, Poison, Invalid, HomeBase
    }

    private int neighbourOffsets[];
    private TileType board[];
    public int width, height, indexLimit, cellSize, startIndex;

    public Vector2 minBounds;
    public Vector2 maxBounds;
    public Vector2 position;

    public Boolean isPlacingStart = true;
    
    // Whether the start & goal states are valid.
    public Boolean validGS = true;

    public Board(int width, int height, int cellSize, Vector2 position) {
        this.width = width;
        this.height = height;
        this.position = position;
        this.cellSize = cellSize;
        
        this.minBounds = position;
        this.maxBounds = position.add(new Vector2(width*cellSize - 1, height*cellSize - 1));

        // Left, right, up, down, NE, SE, SW, NW
        //this.neighbourOffsets = new int[] {-1, 1, -width - 1, width + 1, -width - 2, -width, width + 2, width};
        // Left, right, up, down
        this.neighbourOffsets = new int[] {-1, 1, -width - 1, width + 1};

        this.indexLimit = (width + 1) * (height + 2);

        this.board = new TileType[this.indexLimit];

        // set all values to invalid
        for(int i = 0; i < this.indexLimit; i++){
            this.board[i] = TileType.Invalid;
        }

        // DEBUG: Randomize cells
        //Random r = new Random();
        //r.setSeed(1535483);

        // Set values of board that are part of game to 0
        initializeValues();
        this.startIndex = 0;

        // DEBUG
        /*
        for(int i = 0; i < this.board.length; i++){
            if((i % 5) == 0){
                System.out.println();
            }
            System.out.printf(" %s ", this.board[i].toString());
        }
        for(int i = 0; i < this.board.length; i++){
            if((i % 5) == 0){
                System.out.println();
            }
            System.out.printf("|%4d %4d", i, arrToIndex(i));
        }

        */
    }

    public void initializeValues(){

        for(int y = 0; y < this.height; y++){
            int index = (y + 1) * (this.width + 1);
            // DEBUG: randomize cells
            /*
            for(int j = 0; j < this.width; j++){
                // Generate random number from [0,3], biased towards 0.
                double randVal = Math.abs(r.nextFloat()) * 3;
                board[index + j] = (int)Math.round(randVal);
            }
            */

            // default behaviour 
            for(int j = 0; j < this.width; j++){
                board[index + j] = TileType.Open;
            }

        }
        // setValue(startIndex, TileType.HomeBase);

        // Setup food, poison, water.
        ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
        TileType[] addTiles = {TileType.Food, TileType.Water, TileType.Poison};
        usedIndexes.add(-1);
        for(int i = 0; i < addTiles.length; i++){
            int newIndex = -1;
            while(usedIndexes.contains(newIndex) && (newIndex != this.startIndex)){
                newIndex = (int)Math.floor(Math.random() * (this.width * this.height));
            }
            usedIndexes.add(newIndex);
            setValue(newIndex, addTiles[i]);
        }

    }

    // returns a list of neighbours in arr reference
    public ArrayList<Integer> getNeighbours(int index){
        ArrayList<Integer> results = new ArrayList<Integer>();

        int arrIndex = indexToArr(index);
        for(int i = 0; i < neighbourOffsets.length; i++){
            int a = arrIndex + neighbourOffsets[i];
            if((a < 0) || (a >= this.indexLimit)){
                continue;
            }
            TileType newNeighbour = this.board[a];
            if(newNeighbour != TileType.Invalid){
                results.add(a);
            }
        }
        return results;
    }

    public int indexToArr(int index){
        int a = Math.floorDiv(index, this.width);
        return index + (this.width + 1) + a;
    }

    public int arrToIndex(int arr){
        int a = Math.floorDiv(arr, this.width+1);
        return arr - this.width - a;
    }

    public void setValue(int index, TileType value){
        int a = indexToArr(index);
        this.board[a] = value;
    }

    public TileType getValue(int index){
        int a = indexToArr(index);
        return this.board[a];
    }

    public TileType getValueFromArr(int arr){
        return this.board[arr];
    }

    // performs appropriate behaviour to tile that is clicked on according to mouse button 
    // returns index of tile that is clicked on
    public int isClickedOn(Vector2 position, int mouseButton){
        // Get local coordinates of tile that is clicked on
        int basex = Math.round(position.x - this.position.x);
        int basey = Math.round(position.y - this.position.y);
        int xindex = Math.floorDiv(basex, this.cellSize);
        int yindex = Math.floorDiv(basey, this.cellSize);
        int a = xindex + (yindex * this.width);

        switch(mouseButton){
            case(MouseEvent.BUTTON1):
                break;

            case(MouseEvent.BUTTON3):
                if(getValue(a) == TileType.Open){
                    this.startIndex = a;
                }
                break;
        }

        return xindex + (yindex * this.width);
    }

    public void drawTile(Graphics g, Image img, Vector2 position){
        Vector2 dim = new Vector2(img.getWidth(null), img.getHeight(null));
        g.drawImage(img, Math.round(position.x), Math.round(position.y), (int)dim.x, (int)dim.y, null);
    }

    public void drawBoard(Graphics g, Image food, Image water, Image poison){
        int index = 0;
        for(int y = 0; y < this.height; y++){
            for(int x = 0; x < this.width; x++){
                g.setColor(Color.LIGHT_GRAY);
                // g.setColor(this.colors[a]);
                int posx = Math.round(this.position.x + this.cellSize * x);
                int posy = Math.round(this.position.y + this.cellSize * y);
                Vector2 pos = new Vector2(posx, posy);
                TileType a = getValue(index);
                g.fillRect(posx, posy, this.cellSize, this.cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(posx, posy, this.cellSize, this.cellSize);
                
                switch(a){
                    case Food:
                        drawTile(g, food, pos);
                    case Invalid:
                        break;
                    case Open:
                        break;
                    case Poison:
                        drawTile(g, poison, pos);
                        break;
                    case Water:
                        drawTile(g, water, pos);
                        break;
                    case HomeBase:
                        break;
                    default:
                        break;
                }
                
                posx += 2;
                posy += this.cellSize - 2;

                if(index == this.startIndex){
                    g.drawString("S", posx, posy);
                }
                index++;
            }
        }
    }

    // Get coordinates of top left corner of particular tile
    public Vector2 getTileCoords(int index){
        int y = Math.floorDiv(index, this.width);
        int x = index % this.width;
        float posx = this.position.x + this.cellSize * x;
        float posy = this.position.y + this.cellSize * y;
        return new Vector2(posx, posy);
    }

}