/*Matthew Hall, June 4th 2018
 * Minesweeper
 * This is the main file for the program
 * it draws the board, flags, bombs, numbers
 * and contains the GUI and menu, as well as a timer
 * 
 * it also detects where the user clicked so as to reveal the correct cell
 * 
 * */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*; // For Ellipse2D, etc.
import java.util.*;
public class Display  extends JFrame implements MouseListener, ActionListener {
  
  Dimension d;
  double sWidth; //screen width
  double sHeight; //screen height
  //timer
  javax.swing.Timer t;
  int  timeEllapsed;
  int timeRemaining;
  int bombs;
  int totalLives;
  int gameCounter;
  
  
  int timeMin; //time limit: minutes
  int timeSec; //time limit: seconds
  
  int gameCols; //how many rows the board has
  int gameRows; //how many columns the board has
  
  boolean hasWon; //it the user won
  boolean started; //to start the timer when the game is started, and disable clicks when its not
  boolean isError; //to check for user input errors
  boolean gameOver; //to stop the timer and disable clicks
  boolean isTimeLimitTrue; //if the user wants a time limit
  boolean manual; //if the instructions button has been clicked
  
  private DrawCanvas canvas; //for drawing
  
  //GUI
  JButton  btnRestart;
  JButton instructions;
  JButton btnExit;
  
  JLabel bombLabel;
  JLabel timeLabel;
  JTextField bombSet;
  JTextField liveSet;
  JLabel lifeLabel;
  JTextField gameLives;
  JLabel gameLivesLabel;
  JTextField flagsLeft;
  JLabel flagsLeftLabel;
  JCheckBox isTimeLimit;
  JTextField timeEllapsedBox;
  JLabel timeEllapsedLabel;
  JTextField timeRemainingBox;
  JLabel timeRemainingLabel;
  JComboBox minutes;
  JComboBox seconds;
  JLabel minLabel;
  JLabel secLabel;
  JComboBox rowSelect;
  JComboBox columnSelect;
  JLabel colLabel;
  JLabel rowLabel;
  
  //items in the time combo boxes
  String[] mBox = new String[60];
  String[] sBox = new String[60];
  
  //items in the row and columns combo boxes
  String[] cols = new String[73];
  String[] rows = new String[42];
  
 
  
  //creates the default board (16 x 30, 99 mines, 1 life)
  Board b;
  
  //canvas for drawing
  class DrawCanvas extends JPanel {
    //painting method
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
      
      //font for the numbers on the cells
      if(!manual){
      g2d.setFont(new Font("TimesRoman", Font.BOLD, (int)(b.cells[0][0].sWidth / 2)));
      //iterates through the board
      for(int i = 0; i < b.rows; i++){
        for(int j = 0; j < b.columns; j++){
          if(gameOver && !b.cells[i][j].isFlagged){
            b.cells[i][j].isRevealed = true;
          }
          //if the cell has been revealed and there are no bombs around it
          if(b.cells[i][j].isRevealed && !b.cells[i][j].isBomb){
            //sets color to a light blue
            g2d.setColor(new Color(7, 249, 233));
            
            g2d.fill3DRect(b.cells[i][j].x1, b.cells[i][j].y1, b.cells[i][j].width, b.cells[i][j].height, false);
            g2d.setColor(Color.BLACK);
            //draws the outline
            g2d.draw3DRect(b.cells[i][j].x1, b.cells[i][j].y1, b.cells[i][j].width, b.cells[i][j].height, false);
            
            //if the cell has not been revealed
          }else{
            //same as above
            g2d.setColor(Color.BLUE);
            g2d.fill3DRect(b.cells[i][j].x1, b.cells[i][j].y1, b.cells[i][j].width, b.cells[i][j].height, true);
            g2d.setColor(Color.BLACK);
            g2d.draw3DRect(b.cells[i][j].x1, b.cells[i][j].y1, b.cells[i][j].width, b.cells[i][j].height, true);
          }
          
          //if the cell has been flagged but the game has not been lost
          if(b.cells[i][j].isFlagged && !gameOver){
            g2d.setColor(Color.RED);
            //draw the stem of the flag
            g2d.draw(b.cells[i][j].flagLine);
              //draws the flag
            g2d.fill(b.cells[i][j].flag);
            g2d.setColor(Color.BLACK);
            
            //if the cell is revealed and contains a number other than 0
          }else if(b.cells[i][j].isRevealed && b.cells[i][j].num !=0 && !b.cells[i][j].isBomb){
            g2d.setColor(b.cells[i][j].c);
            //draws the number
            g2d.drawString(Integer.toString(b.cells[i][j].num), b.cells[i][j].x1 + (int)(b.cells[i][j].sWidth / 2.5) , b.cells[i][j].y1 + (int)(b.cells[i][j].sWidth * (2.0 / 3)));
            
            //if the player lost, draw a bomb in all cells that have bombs, unless they are flagged
          }else if(gameOver && b.cells[i][j].isBomb && !b.cells[i][j].isFlagged){
            g2d.setColor(Color.RED);
            g2d.fill(b.cells[i][j].bomb);
          }else if(!gameOver && b.cells[i][j].isRevealed && b.cells[i][j].isBomb){
            g2d.setColor(Color.RED);
            g2d.fill(b.cells[i][j].bomb);
          }else if(gameOver && b.cells[i][j].isBomb && b.cells[i][j].isFlagged){
            g2d.setColor(Color.RED);
            //draw the stem of the flag
           g2d.draw(b.cells[i][j].flagLine);
            //draws the flag
            g2d.fill(b.cells[i][j].flag);
            g2d.setColor(Color.BLACK);
            
            //if the play lost, draw an x through all cells that are flagged incorrectly (dont acutally contain a bomb)
          }else if(gameOver && b.cells[i][j].isFlagged && !b.cells[i][j].isBomb){
            g2d.setColor(Color.RED);
            g2d.draw(b.cells[i][j].flagLine);
            g2d.fill(b.cells[i][j].flag);
            g2d.setColor(Color.BLACK);
            g2d.draw(b.cells[i][j].line1);
            g2d.draw(b.cells[i][j].line2);
          }          
        }
      }
      
      //draw the outline around cells with numbers again, incase they were drawn over by cleared cells
      for(int i = 0; i < b.rows; i++){
        for(int j = 0; j < b.columns; j++){
          if(b.cells[i][j].num > 0){
            g2d.setColor(Color.BLACK);
          g2d.draw3DRect(b.cells[i][j].x1, b.cells[i][j].y1, b.cells[i][j].width, b.cells[i][j].height, true);
          }
        }
      }
      
      //ratios are in attempt to make this compatable with all resolutions
      //the were found multiplying the width or height of the screen by is desire postion over 1080 (for height) or
      //1920 (for width)
      
      if(gameOver){
        
       g2d.setFont(new Font("TimesRoman", Font.BOLD, 150));
       g2d.setColor(Color.BLACK);
       g2d.drawString("GAME OVER", (int)(sWidth * (350.0 / 1920)), (int)(sHeight / 2 - 100));       
      }
      
      if(gameCounter == 0){
        g2d.setFont(new Font("TimesRoman", Font.BOLD, 150)); 
         g2d.setColor(Color.BLACK);
       g2d.drawString("Minesweeper", (int)(sWidth * (275.0 / 1920)), (int)(sHeight / 2 - 100));       
      }
      
      if(hasWon){
        g2d.setFont(new Font("TimesRoman", Font.BOLD, 150));
       g2d.setColor(Color.BLACK);
       g2d.drawString("YOU WIN!", (int)(sWidth * (400.0 / 1920)), (int)(sHeight / 2 - 100));     
      }
    }else{
    g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
      g2d.drawString("Instructions:", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (50.0 / 1080)));
      g2d.drawString("The goal of the game is to clear the board without hitting any mines.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (100.0 / 1080)));
      g2d.drawString("Click a cell to reveal it. The number shown gives how many of the 8 cells surrounding it contains mines.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (150.0 / 1080)));
      g2d.drawString("Right click a cell to flag it. This indicates that you think there is a mine in that cell. Right click a flagged cell to unflag it.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (200.0 / 1080)));
      g2d.drawString("Any cell that is dark blue is unrevealed (can contain a mine).", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (250.0 / 1080)));
      g2d.drawString("You may choose the number of bombs you want by typing into the box labeled Total Bombs. This value is restrictect from 1 to a board with 80% mines.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (300.0 / 1080)));
      g2d.drawString("You may also choose how many lives you have by typing into the Total Lives box. This must be greater than 0.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (350.0 / 1080)));
      g2d.drawString("Having more than 1 life means you wont lose on the first bomb you click.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (400.0 / 1080)));
      g2d.drawString("You can also set a time limit. Check the checkbox to do so, then select minutes and seconds in the scroll boxes. If you have not won when the time runs out, you lose.", (int)(sWidth * (10.0 / sWidth)), (int)(sHeight * (450.0 / 1080)));
      g2d.drawString("Custumize the rows and columns of the board by using the scroll boxes label rows and columns.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (500.0 / 1080)));
      g2d.drawString("Click the instructions button again to exit this page.", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (550.0 / 1080)));
      g2d.drawString("YOU MUST CLICK RESTART FOR ANY CHANGES TO COME INTO EFFECT", (int)(sWidth * (10.0 / 1920)), (int)(sHeight * (600.0 / 1080)));
    }
    
  }
  }
  
  
  public Display(){
    
    for(int i = 0; i <= 59; i++){
      mBox[i] = Integer.toString(i);
      sBox[i] = Integer.toString(i);
    }
    
    for(int i = 8; i < 81; i++){
      if(i <= 49){
        rows[i - 8] = Integer.toString(i);
      }
      cols[i - 8] = Integer.toString(i);
    }
    
   
   d = Toolkit.getDefaultToolkit().getScreenSize();
 sWidth = d.getWidth();
 sHeight = d.getHeight();
 System.out.println(sWidth);
 System.out.println(sHeight);
    gameCounter = 0;
    totalLives = 0;
     gameCols = 30;
    gameRows = 16;
    
    b = new Board(gameRows, gameCols, 99, 1, 0, 0);
    
    timeEllapsed = 0;
    
    isError = true;
    started = false;
    gameOver = false;
    isTimeLimitTrue = false;
    hasWon = false;
    manual = false;
    
    bombs = 0;
    gameCols = 30;
    gameRows = 16;
    
    //ratios are in attempt to make this compatable with all resolutions
    //the were found multiplying the width or height of the screen by is desire postion over 1080 (for height) or
    //1920 (for width)
    
    //cases involving "panel wifth" were found uses ratios of the desire size of buttom to the size of the button panel
    
    int componentHeight = (int)(sHeight * (30.0 / 1080));
    
    
    int panelWidth = (int)(sWidth * (300.0 / 1920));
    
    t = new javax.swing.Timer(1000, this);
    JPanel btnPanel = new JPanel(null);
    btnPanel.setBackground(Color.WHITE);
    btnPanel.setPreferredSize(new Dimension(panelWidth, (int)sHeight));
    //new text field for setting the bombs
    
    
    //new button
    btnRestart = new JButton("Start/Restart");
    //adds it to the panel
    btnPanel.add(btnRestart);
    //adds a listener
    btnRestart.addActionListener(this);
    btnRestart.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (335.0 / 1080)), (int)(panelWidth / 2.0) , componentHeight);
 //   timeLimit = new JTextField(7);
 //   timeLimit.setEditable(false);
//    btnPanel.add(timeLimit);
 //   timeLimit.setBounds((int)(panelWidth / 6.0), (int)(sHeight * (50.0 / 1080)), (int)(panelWidth / 6.0),  componentHeight);
 //   timeLimit.setText("none");
    
    bombSet = new JTextField(20);
    //sets the field to be editable
    bombSet.setEditable(true);
    //adds it to the panel
    btnPanel.add(bombSet);
    bombSet.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (115.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    bombLabel = new JLabel("Total Bombs");
    btnPanel.add(bombLabel);
    bombLabel.setBounds((int)(panelWidth / 3.0), (int)(sHeight * (90.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    

    
    instructions = new JButton("Instructions");
    btnPanel.add(instructions);
    instructions.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (295.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    instructions.addActionListener(this);
    
    liveSet = new JTextField(3);
    liveSet.setEditable(true);
    btnPanel.add(liveSet);
    liveSet.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (180.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    lifeLabel = new JLabel("Total Lives");
    btnPanel.add(lifeLabel);
    lifeLabel.setBounds((int)(panelWidth / 2.8), (int)(sHeight * (150.0 / 1080)), (int)(panelWidth / 4.0),  componentHeight);
    
    gameLives = new JTextField(16);
    gameLives.setEditable(false);
    btnPanel.add(gameLives);
    gameLives.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (400.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    gameLivesLabel = new JLabel("Lives Remaining");
    btnPanel.add(gameLivesLabel);
    gameLivesLabel.setBounds((int)(panelWidth / 3.0), (int)(sHeight * (375.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    flagsLeft = new JTextField(16);
    flagsLeft.setEditable(false);
    btnPanel.add(flagsLeft);
    flagsLeft.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (475.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    flagsLeftLabel = new JLabel("Flags Remaining");
    btnPanel.add(flagsLeftLabel);
    flagsLeftLabel.setBounds((int)(panelWidth / 3.0), (int)(sHeight * (450.0 / 1080)), (int)(panelWidth / 2.0),  componentHeight);
    
    isTimeLimit = new JCheckBox("Time Limit");
    btnPanel.add(isTimeLimit);
    isTimeLimit.setBounds((int)(panelWidth / 1.8), (int)(sHeight * (50.0 / 1080)), (int)(panelWidth / 3.0),  componentHeight);
    isTimeLimit.addActionListener(this);
    
    timeEllapsedBox = new JTextField(7);
    timeEllapsedBox.setEditable(false);
    btnPanel.add(timeEllapsedBox);
    timeEllapsedBox.setBounds((int)(panelWidth / 6.0), (int)(sHeight * (550.0 / 1080)), (int)(panelWidth / 6.0),  componentHeight);
    timeEllapsedBox.setText(timeFormater(timeEllapsed));
    
    timeEllapsedLabel = new JLabel("Time Ellapsed");
    btnPanel.add(timeEllapsedLabel);
    timeEllapsedLabel.setBounds((int)(panelWidth / 10.0), (int)(sHeight * (525.0 / 1080)), (int)(panelWidth / 3.75),  componentHeight);
    
    timeRemainingBox = new JTextField(7);
    timeRemainingBox.setEditable(false);
    btnPanel.add(timeRemainingBox);
    timeRemainingBox.setBounds((int)(panelWidth / 1.8), (int)(sHeight * (550.0 / 1080)), (int)(panelWidth / 6.0),  componentHeight);
    
    timeRemainingLabel = new JLabel("Time Remaining");
    btnPanel.add(timeRemainingLabel);
    timeRemainingLabel.setBounds((int)(panelWidth / 2.1), (int)(sHeight * (525.0 / 1080)), (int)(panelWidth / 3.0),  componentHeight);
    timeRemainingBox.setText("Infinite");
    
    minutes = new JComboBox(mBox);
    minutes.setEnabled(false);
    btnPanel.add(minutes);
    minutes.setBounds((int)(panelWidth / 12.0), (int)(sHeight * (50.0 / 1080)), (int)(panelWidth / 7.0), componentHeight);
    
      minLabel = new JLabel("Mins");
    btnPanel.add(minLabel);
    minLabel.setBounds((int)(panelWidth / 12.0), (int)(sHeight * (25.0 / 1080)), (int)(panelWidth / 7.0), componentHeight);
    
    seconds = new JComboBox(sBox);
    seconds.setEnabled(false);
    btnPanel.add(seconds);
    seconds.setBounds((int)(panelWidth / 3.0), (int)(sHeight * (50.0 / 1080)),(int)(panelWidth / 7.0), componentHeight);
    
    secLabel = new JLabel("Secs");
    btnPanel.add(secLabel);
    secLabel.setBounds((int)(panelWidth / 3.0), (int)(sHeight * (25.0 / 1080)),(int)(panelWidth / 7.0), componentHeight);
      
    rowSelect = new JComboBox(rows);
    rowSelect.setEnabled(true);
    btnPanel.add(rowSelect);
    rowSelect.setBounds((int)(panelWidth / 4.5), (int)(sHeight * (240.0 / 1080)),(int)(panelWidth / 7.0), componentHeight);
    rowSelect.setSelectedIndex(8);
    
    rowLabel = new JLabel("rows");
    btnPanel.add(rowLabel);
    rowLabel.setBounds((int)(panelWidth / 4.5), (int)(sHeight * (215.0 / 1080)),(int)(panelWidth / 7.0), componentHeight);
    
    columnSelect = new JComboBox(cols);
    columnSelect.setEnabled(true);
    btnPanel.add(columnSelect);
    columnSelect.setBounds((int)(panelWidth / 2.0), (int)(sHeight * (240.0 / 1080)),(int)(panelWidth / 7.0), componentHeight);
    columnSelect.setSelectedIndex(22);
    
    colLabel = new JLabel("columns");
    btnPanel.add(colLabel);
    colLabel.setBounds((int)(panelWidth / 2.1), (int)(sHeight * (215.0 / 1080)),(int)(panelWidth / 6.0), componentHeight);
   
    btnExit = new JButton("Exit");
    btnPanel.add(btnExit);
    btnExit.addActionListener(this);
    btnExit.setBounds((int)(panelWidth / 4.0), (int)(sHeight * (600.0 / 1080)), (int)(panelWidth / 2.0) , componentHeight);
   
    //new instance of drawing class 
    canvas = new DrawCanvas();
    canvas.setPreferredSize(d);
    
    liveSet.setText(Integer.toString(b.lives));
    bombSet.setText(Integer.toString(b.bombs));
    gameLives.setText(Integer.toString(b.lives));
    flagsLeft.setText(Integer.toString(b.flagsRemaining));
    Container cp = getContentPane();
    
    cp.setLayout(new BorderLayout());
    
    //adds the drawcanvas and JPanel to the Container
    cp.add(canvas, BorderLayout.CENTER); //centre of container
    cp.add(btnPanel, BorderLayout.EAST); //right of container
    
    //adds a mouse listner
    addMouseListener(this);
    // Handle the CLOSE button
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    
    setTitle("Minesweeper");
    // pack all the components
    pack();         
    
    setVisible(true);
  }
  
  //handles mouse clicks and the timer
  public void actionPerformed(ActionEvent a){
    
    //if the start/restart button if clicked
    if (a.getSource()== btnRestart){
      manual = false; //exit the manual
      
      //if there is a time limit
      if(isTimeLimit.isSelected()){
        isTimeLimitTrue = true;
        
        //if not
      }else{
       isTimeLimitTrue = false; 
       timeRemainingBox.setText("infinite");
      }
      
      //resets variables
    hasWon = false;
      gameOver = false;
      
      try{
        //if this runs, sets totalLives to the value in the textbox
        totalLives = Integer.parseInt(liveSet.getText());
        isError = false; //no input error
        liveSet.setText(Integer.toString(totalLives));
        
        if(totalLives <= 0){
         isError = true;
         liveSet.setText("must be > 0"); 
        }
        
        //if value in box could not be converted to an integer
      }catch(Exception e){
        //if box is empty
        if(liveSet.getText().length() == 0){
          totalLives = 1;
          liveSet.setText(Integer.toString(totalLives));
          isError = false; //no error
          
          //if not empty
        }else{
          isError = true; //error
         liveSet.setText("must be a number"); 
        }
      }
      
      //if user wants a timeLimit
      if(isTimeLimit.isSelected()){
        //gets the time
       timeMin = minutes.getSelectedIndex(); 
       timeSec = seconds.getSelectedIndex();
       
       //sets to no limit if 0
       if(timeSec == 0 && timeMin == 0){
        timeRemainingBox.setText("inifinite"); 
        
        //if not 0
       }else{
        timeRemaining = 60 * timeMin + timeSec;  
       }
      }
      
      //lowest number is 8, so add 8 to every index
      gameRows = rowSelect.getSelectedIndex() + 8;
      gameCols = columnSelect.getSelectedIndex() + 8;
      
      //gets the text in the bombSet field
      String s = bombSet.getText();
      
      //same as error checking for lives
      try{ 
        bombs = Integer.parseInt(s);
       
        if(bombs <= 0){
         isError = true;
         bombSet.setText("must be > 0");
        }
       // b = new Board(16, 30, bombs, 1);
        //if the percent of the board that is bombs is more than 80%
        if((bombs /(double)(gameRows * gameCols)) > 0.8){
          //displays error message
          bombSet.setText("Bomb percent too high");
          //there is an input error
          isError = true;
          
          //if not
        }else{
          //no input error
          if(!isError){
          isError = false;
          }
        }
        
        //if the text in the field could not be converted into an integer
      }catch(Exception f){
        //it the field if empty
        if(s.length() == 0){
          //no error, number of bombs to default amout (20.625%)
          if(!isError){
          isError = false;
          }
          bombSet.setText(Integer.toString((int)(Math.floor(gameRows * gameCols * 0.20625))));
          bombs = (int)(Math.floor(gameRows * gameCols * 0.20625));
         
          //if not empty
        }else{
          bombSet.setText("must be a number"); 
          //there is an error
          isError = true;
        }
      }
      
      //if no error an field conains a number
      if(!isError && s.length() > 0){
        b = new Board(gameRows, gameCols, bombs, totalLives, 0, 0); 
         gameLives.setText(Integer.toString(b.lives));
         flagsLeft.setText(Integer.toString(b.flagsRemaining));
        gameCounter++;
        started = true;
        
        //if no error and no number
      }else if(!isError && s.length() == 0){
        
        //default
        b = new Board(gameRows, gameCols, 99, totalLives, 0, 0);  
         gameLives.setText(Integer.toString(b.lives));
         flagsLeft.setText(Integer.toString(b.flagsRemaining));
        started = true;
         gameCounter++;
        //if there is an error
      }else if(isError){
        //dont start the game
        started = false;
      }
      
      //is the game has started
      if(started){
        //restart the timer
        t.restart();
        //reset the game clock
        timeEllapsed = 0;
        if(isTimeLimit.isSelected()){
      //  timeLimit.setText(timeFormater((timeRemaining)));
        }
      }
      
      //if the event is the timer
    }else if(a.getSource() == t){
      //increment the game clock
      timeEllapsed++;
      if(isTimeLimitTrue){
      timeRemaining--;
      if(timeRemaining == 0){
       gameOver = true;
       started = false;
       t.stop();
      }
      }
      //update the time textfield (timeEllapse)
      timeEllapsedBox.setText(timeFormater(timeEllapsed));
      if(isTimeLimitTrue && timeRemaining >= 0){
      timeRemainingBox.setText(timeFormater(timeRemaining));
      }
    
      //if status of check box is changed
    }else if(a.getSource() == isTimeLimit){
      if(isTimeLimit.isSelected()){
   
        minutes.setEnabled(true);
        seconds.setEnabled(true);
      }else{
  
        minutes.setEnabled(false);
        seconds.setEnabled(false);
     
      }
    }else if(a.getSource() == instructions){
      if(!manual){
        manual = true;
      }else{
       manual = false; 
      }
    }else if(a.getSource() == btnExit){
     System.exit(0); 
    }
    //redraws everything
    repaint();
  }
  
  //mouse clicks
  public void mouseClicked(MouseEvent e){
    if(started){
      int x = e.getX() - (int)(sWidth * (10.0 / 1920));
      int y = e.getY() - (int)(sHeight * (40.0 / 1080));
      int i = -1;
      
      //iterates though cells
      for(int x1 = 0; x1 < b.rows; x1++){
        for(int y1 = 0; y1 < b.columns; y1++){
          //if a cell contains the mouse coordinates
          if(b.cells[x1][y1].rec.contains(x, y)){
            x = x1;
            i = 0; //to break out of outer loop
            y = y1;
            
            break;
          }
        }
        //if cell is found
        if(i != -1){
          break;
        }
      }
      
      //in try block to avoid going out of bounds
      try{
        //right click
        if(e.getButton() == 3 && b.revealCounter > 0){ 
         
          //do nothing if cell is revealed
          if(b.cells[x][y].isRevealed){ 
          }
          //unflag if already flagged
          else if(b.cells[x][y].isFlagged){
            if(b.cells[x][y].isBomb){
             b.correctFlagCount--; 
            }
            b.cells[x][y].isFlagged = false;
            b.flagsRemaining++;
            flagsLeft.setText(Integer.toString(b.flagsRemaining));
            
            //flag if not flagged
          }else{
            if(b.flagsRemaining > 0){
              
              //if cell is bomb
            if(b.cells[x][y].isBomb){
             b.correctFlagCount++; 
            }
            
            b.flagsRemaining--;
            b.cells[x][y].isFlagged = true;
            flagsLeft.setText(Integer.toString(b.flagsRemaining));
          }
          }
          
          if(b.correctFlagCount == b.bombs){
            started = false;
            hasWon = true;
            t.stop();
          }
          //left click
        }else if(e.getButton() == 1){
          if(!gameOver){
            //to avoid clicking a bomb on the first click
            if(b.revealCounter == 0){
             b = new Board(gameRows, gameCols, bombs, totalLives, x, y);
             flagsLeft.setText(Integer.toString(b.flagsRemaining));
              gameLives.setText(Integer.toString(b.lives));
             b.revealCounter++;
            }
            //if cell clicked is a bomb
            if(b.cells[x][y].isBomb && !b.cells[x][y].isRevealed){
              b.cells[x][y].isRevealed = true;
              //take away a life
              b.lives--;
              b.flagsRemaining--;
              b.correctFlagCount++;
                gameLives.setText(Integer.toString(b.lives));
             
              
              //if out of lives
              if(b.lives == 0){
                //
                gameOver = true; 
                //stop the timer
                t.stop();
                
              }
              //do nothing if the cell if flagged
            }else if(b.cells[x][y].isFlagged){
              
              //expand if the cell has no bombs around it
            }else if(b.cells[x][y].num == 0){
              b.expand(x, y);
              
              //reveal cell if it has 1 or more bombs around it
            }else{
              b.cells[x][y].isRevealed = true;
              b.revealCounter++;
            }
          }
        }
      }catch(Exception g){}
      repaint();
      
    }
  }
  
  public void mouseEntered(MouseEvent e){
  }
  
  public void mouseExited(MouseEvent e){
  }
  
  public void mousePressed(MouseEvent e){
  }
  
  public void mouseReleased(MouseEvent e){
  }
  
  //formats time given number of seconds
  public String timeFormater(int s){
    String time = "";
    
    //if time = 0
    if(s == 0){
      return "00 : 00"; 
    }
    
    int minutes = s / 60;
    int seconds = s % 60;
    
    //add a zero infront of the number of minutes if its less than 10
    if(minutes < 10){
      time += "0";
      time += minutes;
      
      //if minutes is > 10
    }else{
      time += Integer.toString(minutes);
    }
    
    time += " : ";
    
    //same as above
    if(seconds < 10){
      time += "0";
      time += seconds;
    }else{
      time += seconds; 
    }
    
    return time;
  }
  
  public static void main(String[]args){
    //opens the window but creating a new Display class
    Display d = new Display();
  }
  
}