/*Matthew Hall, June 4th 2018
 * Minesweeper
 * the class is a representation of a cell in minesweeper
 * it contains properties that tell if a cell is a bomb, is flagged, or how many bombs surround it
 * it also contains different shapes wich comprise the board
 * 
 * */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*; // For Ellipse2D, etc.
import java.util.*;
import java.util.*;

public class Cell{
  boolean isBomb;
  boolean isFlagged;
  boolean isRevealed;
  Color c;
  int num;
  
  //for the flag
  int[] fx = new int[3];
  int[] fy = new int[3];
  Polygon flag;
  Line2D.Double flagLine;
  
  int x1;
  int y1;
  int width;
  int height;
  //to draw the cells
  Rectangle2D rec;
  Ellipse2D.Double bomb;
  Line2D.Double line1;
  Line2D.Double line2;
  
  double sWidth;
  double sHeight;
  
  //creates a new cell given x and y coordinates on the board and how many rows and columns that board is
  public Cell(int x, int y, int columns, int rows){
    
    //gets screen size
   Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
  sWidth = d.getWidth();
  sHeight = d.getHeight();
  double temp = sWidth;
 
  
  
    sWidth = Math.floor((sWidth - (sWidth * (320.0 / 1920))) / columns);
    
    sHeight = Math.floor((sHeight - (sHeight * (120.0 / 1080))) / rows);
    
 //if the width given would make the board go off the bottom of the window,
    //create a new width based of the height of the screen instead of the width
    if(sWidth * rows > d.getHeight() - (d.getHeight() - (40.0 / 1080))){
      //uses the minimum (so it wont go off screen) as the width of each cell
  sWidth = Math.min(sHeight, sWidth);
    }
    isBomb = false;
    isFlagged = false;
    isRevealed = false;
    num = 0;
    fx = new int[4];
    fy = new int[4];
    
    //all ratios below are in attemp to make it work for any resolution
   
    //for drawing the cells
    
    x1 = y * (int)sWidth +  (int)(sWidth / 3.0);
    y1 = x * (int)sWidth +  (int)(sWidth /6.0);
    width = (int)(sWidth);
    height = (int)(sWidth);
    
   
    
    //for detected what cell the mouse clicked
    rec = new Rectangle2D.Double(x1, y1, (int)sWidth, (int)sWidth);
    
    bomb = new Ellipse2D.Double(y * (int)sWidth +  (int)(sWidth / 2.5 ), x * (int)sWidth +  (int)(sWidth / 3.5 ), (int)sWidth -  (int)(sWidth /6.0 ), (int)sWidth -  (int)(sWidth / 6.0));
    
    line1 = new Line2D.Double(x1, y1, x1 + width, y1 + width);
    line2 = new Line2D.Double(x1 + width, y1, x1, y1 + width);
    
    //the points of the flag
    fx[0] = y * (int)sWidth + (int)(sWidth * 0.6);
    fy[0] = x * (int)sWidth + (int)(sWidth * (1.0 / 3));
    
    fx[1] = y * (int)sWidth + (int)(sWidth * (29.0 / 30));
    fy[1] = x * (int)sWidth + (int)(sWidth * 0.5);
    
    fx[2] = y * (int)sWidth + (int)(sWidth * 0.6);
    fy[2] = x * (int)sWidth + (int)(sWidth * (2.0 / 3));
    
    flag = new Polygon(fx, fy, 3);
    flagLine =  new Line2D.Double(fx[0], fy[0], fx[0], fy[0] + height * (2 / 3.0) );
  }
  
  //sets the columns for each amount of bombs a cell could have surrounding it
  public void setColor(){
    switch(num){
      case 1: c = Color.BLACK;
      break;
      case 2: c = new Color(238, 238, 34);
      break;
      case 3: c = Color.RED;
      break;
      case 4: c = Color.MAGENTA;
      break;
      case 5: c = Color.ORANGE;
      break;
      case 6: c = Color.WHITE;
      break;
      case 7: c = Color.BLACK;
      break;
      case 8: c = Color.RED;
      break;
      default: 
        break;
    }
  }
}