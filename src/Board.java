/*Matthew Hall, June 4th 2018
 * Minesweeper
 * Board class:
 * This class is a representation of a board in minesweeper all relevant properties
 * these properties include how many bombs there are and how many flags have been placed
 * is also contains method to place bombs on the board set the num property of each Cell object to 
 * how many bombs are around it
 * it contains the reveal method, which expands number of revealed cells
 * */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*; // For Ellipse2D, etc.
import java.util.*;
public class Board{
  Cell[][] cells;
  int bombs;
  int setBombCount;
  int lives;
  Random r = new Random();
  int x = 0;
  int y = 0;
  int notX;
  int notY;
  int rows;
  int columns;
  int c = 0;
 
  int flagsRemaining; //to determine if the player can place more flags
  int correctFlagCount; //to determine whether the player have won
  
  //to not have a bomb on the first click
  //if this is 0 and a cell is clicked, a board, and that clicked cell will never be a bomb
  int revealCounter; 
   boolean noBomb;
  
  //to iterate through all cells surrouding another cell
  int[] xOffset = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
  int[] yOffset = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
  
  //creates a new board, given the rows and columns, number of bombs, number of lives,
  //and the x and y coordinates with there cant be a bomb
  public Board(int rows, int columns, int bombs, int lives, int notX, int notY){
    
//inits variables
    correctFlagCount = 0;
    this.lives = lives;
    this.bombs = bombs;
    this.notX = notX;
    this.notY = notY;
    noBomb = false;
    cells = new Cell[rows][columns];
    this.rows = rows;
    this.columns = columns;
    revealCounter = 0;
    lives = 1;
    flagsRemaining = bombs;
    
    for(int i = 0; i < rows; i++){
      for(int j = 0; j < columns; j++){
        //adds a cell to the array
        cells[i][j] = new Cell(i, j, columns, rows);
      }
    }
    
    //sets the locations of the bombs in the board
    setBombs();
    //sets the numbers of the cells
    setNum();
    
    //sets the colors 
    for(int i = 0; i < rows; i++){
      for(int j = 0; j < columns; j++){
        cells[i][j].setColor();
      }
    }
  }
  
  //set the bomb locations
  public void setBombs(){
    //while there are less bombs than specified
    while(setBombCount < bombs){
      //random x and y coordinates
      x = r.nextInt(rows);
      y = r.nextInt(columns);
      
      //if the generated y and y are the the same coordinates where the user first clicked
      if(x == notX && y == notY){
       noBomb = true; 
       
       //if not
      }else{
       noBomb = false; 
      }
      
      //if the cell doesnt already have bomb in it, and the cell is not the specified cell that cant be a bomb
      if(!cells[x][y].isBomb && !noBomb){
        cells[x][y].isBomb = true;
        setBombCount++;
      }
    }  
  }
  
  //sets the number of bombs around a given cell
  public void setNum(){
    //iterates through the cells
    for(int i = 0; i < rows; i++){
      for(int j = 0; j < columns; j++){
        //iterates through all the surrounding cells
        for(int k = 0; k < 8; k++){
          //so it doesnt go out of bounds (the lazy way)
          try{
            if(cells[i + xOffset[k]][j + yOffset[k]].isBomb){
              cells[i][j].num++;
            }
            
          }catch(Exception e){}
          
        }
      }
    }
  }
  
  //a recursive procedure that expands the number of reveal cells, if the clicked cell is zero
  //(this is only called when the clicked cell has no bombs around it)
  //runs each instance of the method until it reaches a cell the has bombs surrounding it
  public void expand(int x, int y){
    //iterates though all cells surrounding the given cell
    //always goes though all cells direcrly adjacent to the given cell
      for(int i = 0; i < 8; i++){
    //   c++;
      // System.out.println(c);
        //to not go out of bounds
        try{
          //if the number of bombs around the cell is not 0, and the cell is not a bomb and is not flagged
        if(cells[x + xOffset[i]][y + yOffset[i]].num > 0 &&  !cells[x + xOffset[i]][y + yOffset[i]].isBomb && !cells[x + xOffset[i]][y + yOffset[i]].isFlagged ){
          //sets the revealed property to be true
          //does not run the method again
          cells[x + xOffset[i]][y + yOffset[i]].isRevealed = true;  
          revealCounter++;
          
          //if the cell has no bombs around it has not been reveal yet (this is to avoid the stackOverflow error)
          //and the cell is not a bomb and it not flagged
        }else if(!cells[x + xOffset[i]][y + yOffset[i]].isRevealed && !cells[x + xOffset[i]][y + yOffset[i]].isBomb && !cells[x + xOffset[i]][y + yOffset[i]].isFlagged){
          //sets the revealed property to be true
          cells[x + xOffset[i]][y + yOffset[i]].isRevealed = true;
          revealCounter++;
          //runs the method again, with the cell to the upper left corner passed to it
          expand(x + xOffset[i], y + yOffset[i]);
        }
        
        }catch(Exception e){}
    }
      //sets the passed cell's recealed property to be true
    cells[x][y].isRevealed = true;
    revealCounter++;
  }
  
}