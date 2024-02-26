## LOGIC / DESIGN 

This small game demonstrates behaviour of "Ants" as defined by a finite-state machine.
This is an implementation based on the Algorithms in "AI for Games" by Ian Millington
Upon executing the game by running the .jar file, the main editor is opened.
From here, the player may use right click to place the Ants' home location, as well as re-randomize the 
items's positions. The player may also choose the amount of ants by using the numerical input in the
bottom-right corner of the screen.
Upon clicking the "Start Game" button, the user is brought to the simulation screen. 
By clicking the "Play" button, the simulation proceeds at a step-rate as defined by the "Autoplay Rate"
box. The simulation may also be played forward one step at a time by clicking the "Frame Advance" button.
The simulation may be stopped at any time by pressing the "Exit Simulation" button, which brings the user
back to the level editor.

The state machine consists of the following states:
SearchForFood, ReturnToBase, SearchForWater, PickupFood, DrinkWater, Reproduce, Die 

While in either the SearchForFood, or SearchForWater states, the ant moves to a new position that is
adjacent to its current position each step.
While in the SearchForFood state, if the ant finds food, it proceeds to "pick up" the food, and
transitions into the ReturnToBase state. While in the ReturnToBase state, the ant is shown to be carrying
food, and will find the most direct path back to its home position. Upon reaching the home position,
The ant drops the food off, and transitions into the Reproduce state. Once in the reproduce state,
a new ant is created at the location of the home position, which starts with the state SearchForFood.
The ant that was previously in the Reproduce state transitions into the SearchForWater state.
While in the SearchForWater state, the ant is denoted with a blue circle. If the ant finds water, it 
transitions into the DrinkWater state, which it is stationary for, and then the ant transitions 
into the SearchForFood state once more.
If at any point the ant enters a tile with poison, the ant's state transitions to the Die State. After
the next step, the Ant is removed from the simulation


## HOW TO COMPILE / RUN

To run game, simply execute the included .jar file.

To compile game from source into .jar file, ensure you have java jdk installed, 
then open up your command-line interface.
navigate to the "source" folder included in this zip file, and run the following 2 commands:


javac *.java

jar cvfm game.jar MANIFEST.MF *.class util/*.class resource/*.png


the .jar file may then be executed from the command line by: java -jar game.jar



BUGS -----------------------------------------------------------------------------------------------------

At autoplay rates greater than the monitor's refresh rate, the ants movement can appear choppy, as not all
intermediate movements are able to be displayed. However, all movements are still simulated, even if they
are not displayed.
