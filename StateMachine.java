import util.Board;
import util.Board.TileType;

public class StateMachine {
    public static enum State{
        SearchForFood, ReturnToBase, SearchForWater, PickupFood, DrinkWater,
        Reproduce, Die 
    }

    public State myState;

    // Board that FSM is on
    public static Board board;

    public TileType currentTile;

    public StateMachine(){
        this.myState = State.SearchForFood;
    }

    public static Board getBoard(){
        return StateMachine.board;
    }

    public static void setBoard(Board board){
        StateMachine.board = board;
    }

    public void update(){
        if(isOnTile(TileType.Poison)){
            this.myState = State.Die;
            return;
        }
        switch(this.myState){
            case SearchForFood:
                if(isOnTile(TileType.Food)){
                    this.myState = State.PickupFood;
                }
                break;
            case ReturnToBase:
                if(isOnTile(TileType.HomeBase)){
                    this.myState = State.Reproduce;
                }
                break;
            case SearchForWater: 
                if(isOnTile(TileType.Water)){
                        this.myState = State.DrinkWater;
                    }
                break;
            case PickupFood:
                this.myState = State.ReturnToBase;
                break;
            case DrinkWater:
                this.myState = State.SearchForFood;
                break;
            case Reproduce:
                this.myState = State.SearchForWater;
                break;
            case Die:
                break;
        }
    }

    public boolean isOnTile(TileType t){
        if(currentTile == t){
            return true;
        } 
        return false;
    }
}
