import objectdraw.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Acme.*;
import javax.swing.event.*;

public class tictactoe extends WindowController implements MouseListener,
       KeyListener {

  private static final int WINSIZE = 600; // initial canvas size

  private static int turn;
  private static boolean playing;
  private static int gamemode;
  
  private Line htline;
  private Line hbline;
  private Line vlline;
  private Line vrline;

  private FramedRect[] board;
  private int[] filled; 

  private Text prompt1;
  private Text prompt2;
  
  public static void main (String[] args) {
    new Acme.MainFrame (new tictactoe(), args, WINSIZE, WINSIZE); 
  }

  public void begin () {

    initialize();

    canvas.addMouseListener (this);
    canvas.addKeyListener (this);
    canvas.requestFocusInWindow();
  }
  
  public void initialize () {
    canvas.clear();
    turn = 0;
    playing = false;
    gamemode = 0;

    // lines of the board
    htline = new Line (0, WINSIZE / 3, WINSIZE, WINSIZE / 3, canvas);
    hbline = new Line (0, 2 * WINSIZE / 3, WINSIZE, 2 * WINSIZE / 3, canvas);
    vlline = new Line (WINSIZE / 3, 0, WINSIZE / 3, WINSIZE, canvas); 
    vrline = new Line (2 * WINSIZE / 3, 0, 2 * WINSIZE / 3, WINSIZE, canvas);

    // quadrants of the board
    filled = new int[9];
    board = new FramedRect[9];

    //options
    prompt1 = new Text ("Press 1 to play computer", WINSIZE / 3 + 20,
                        WINSIZE / 3 + 10, canvas);
    prompt2 = new Text ("Press 2 for two-player", WINSIZE / 3 + 20,
                        WINSIZE / 3 + 50, canvas);

    prompt1.setBold (true);
    prompt2.setBold (true);


    for (int i = 0; i < board.length; i++) {
      double x = (i % 3) * WINSIZE / 3;
      double y = (i / 3) * WINSIZE / 3;

      board[i] = new FramedRect (x, y, WINSIZE / 3, WINSIZE / 3, canvas);
      
      board[i].setColor (Color.WHITE);
      board[i].sendToBack();
    }
  
  }

  public void mouseReleased (MouseEvent evt) {
    Location pt = new Location( evt.getPoint() );

    if (playing) {
      for (int i = 0; i < board.length; i++) {
        if (board[i].contains (pt) && filled[i] == 0) {
          drawShape(i, gamemode != 1);
          playing = checkWin();
          break;
        }
      }
    }
  }

  public boolean checkWin () {
    int win = checkRow();

    if (win > 0) {
      Text text = new Text ("Player " + win + " Wins", 
                            WINSIZE / 5, WINSIZE / 3, canvas);
      Text restart = new Text ("Press SPACE to restart",
                               WINSIZE / 12, text.getY() + 100, canvas);
      
      text.setFontSize (WINSIZE / 10);
      restart.setFontSize (WINSIZE / 12);

      text.setBold (true);
      restart.setBold (false);

      if (gamemode == 1) {
        text.setText ("YOU != GOOD");
      }

    } else if (turn == 9) {
      win = -1;
      Text tie = new Text ("TIE", 20, 20, canvas);
      tie.setFontSize (WINSIZE / 10);
      tie.setBold (true);

      Text restart = new Text ("Press SPACE to restart", 20, 
                               tie.getY() + WINSIZE / 12, canvas);
      
      restart.setFontSize (WINSIZE / 12);
      restart.setBold (false);
    }

    return win == 0;
  }

  public int checkRow () {
    int first = filled[0] + filled[1] + filled[2];
    int second = filled[3] + filled[4] + filled[5];
    int third = filled[6] + filled[7] + filled[8];

    if (first == 3 || second == 3 || third == 3) {
      return 1;
    } else if (first == -3 || second == -3 || third == -3) {
      return 2;
    } else {
      return checkColumn();
    }
  }

  public int checkColumn () {
    int first = filled[0] + filled[3] + filled[6];
    int second = filled[1] + filled[4] + filled[7];
    int third = filled[2] + filled[5] + filled[8];

    if (first == 3 || second == 3 || third == 3) {
      return 1;
    } else if (first == -3 || second == -3 || third == -3) {
      return 2;
    } else {
      return checkDiagonal();
    }
  }

  public int checkDiagonal () {
    int first = filled[0] + filled[4] + filled[8];
    int second = filled[2] + filled[4] + filled[6];

    if (first == 3 || second == 3) {
      return 1;
    } else if (first == -3 || second == -3) {
      return 2;
    } else {
      return 0;
    }
  }

  public void keyPressed (KeyEvent evt) {
    switch (evt.getKeyCode()) {
      case KeyEvent.VK_SPACE: // user presses SPACE bar
        if (!playing) {
          initialize();
        }
        break;
      case KeyEvent.VK_1:
        if (gamemode == 0) {
          playing = true;
          gamemode = 1;

          prompt1.removeFromCanvas();
          prompt2.removeFromCanvas();
          
          drawShape(4, true);
        }
        break;
      case KeyEvent.VK_2:
        if (gamemode == 0) {
          playing = true;
          gamemode = 2;

          prompt1.removeFromCanvas();
          prompt2.removeFromCanvas();
        }
        break;
    }
  }

  public void drawShape (int i, boolean playerTurn) {
    if (turn++ % 2 == 0) {
      filled[i] = 1;
      // draw the x
      new Line (board[i].getX(), board[i].getY(), 
                board[i].getX() + board[i].getWidth(),
                board[i].getY() + board[i].getHeight(), canvas);

      new Line (board[i].getX(), board[i].getY() + board[i].getHeight(),
                board[i].getX() + board[i].getWidth(), 
                board[i].getY(), canvas);

    } else {
      filled[i] = -1;
      // draw the o
      new FramedOval (board[i].getLocation(), WINSIZE / 3, WINSIZE / 3,
                      canvas);
      
      if (gamemode == 1 && !playerTurn) {
        drawShape (evaluate1 (i), true);
      }
    }
  }

  // maybe use methods (playAcross, playLeft, etc) instead for compactness

  /**
   * For the robot overlord
   *
   * @param xo the last move of the player
   * @return the move to play
   */
  public int evaluate1 (int xo) {
    switch (turn) {
      case 2: // third turn
        if (xo == 1 || xo == 3 || xo == 8) {
          return 0;
        } else if (xo == 5 || xo == 7 || xo == 0) {
          return 8;
        } else if (xo == 2) {
          return 6;
        } else if (xo == 6) {
          return 2;
        }
        break;
      case 4: // fifth turn
        if (filled[0] == 1) {
          if (filled[8] == 0) {
            return 8;
          } else if (filled[1] == -1 || filled[7] == -1) {
            return 6;
          } else if (filled[3] == -1 || filled[5] == -1) {
            return 2;
          } else if (filled[2] == -1) {
            return 5;
          } else if (filled[6] == -1) {
            return 7;
          }
        }

        if (filled[8] == 1) {
          if (filled[0] == 0) {
            return 0;
          } else if (filled[1] == -1 || filled[7] == -1) {
            return 2;
          } else if (filled[3] == -1 || filled[5] == -1) {
            return 6;
          } else if (filled[2] == -1) {
            return 1;
          } else if (filled[6] == -1) {
            return 3;
          }
        }
   
        if (filled[6] == 1) {
          if (xo == 1 || xo == 7) {
            return 0;
          } else if (xo == 3 || xo == 5) {
            return 8;
          } else if (xo == 0) {
            return 1;
          } else if (xo == 8) {
            return 5;
          }
        }

        if (filled[2] == 1) {
          if (xo == 1 || xo == 7) {
            return 8;
          } else if (xo == 3 || xo == 5) {
            return 0;
          } else if (xo == 0) {
            return 3;
          } else if (xo == 8) {
            return 7;
          }
        }

        break;
      case 6: // seventh turn
        if (filled[0] == 1) {
          if (filled[2] == 1) {
            return xo == 1 ? (filled[6] == 0 ? 6 : 8) : 1;
          } else if (filled[6] == 1) {
            return xo == 3 ? (filled[2] == 0 ? 2 : 8) : 3;
          }
        } 

        if (filled[8] == 1) {
          if (filled[2] == 1) {
            return xo == 5 ? (filled[6] == 0 ? 6 : 0) : 5;
          } else if (filled[6] == 1) {
            return xo == 7 ? (filled[2] == 0 ? 2 : 0) : 7;
          }
        }

        if (filled[7] == 1 || filled[1] == 1) {
          if (filled[1] == 0 || filled[7] == 0) {
            return filled[1] == 0 ? 1 : 7;
          } 

          // determine which column is empty of computer moves 
          int start = filled[0] == 1 || filled[6] == 1 ? 2 : 0;

          for (int i = 0; i < 3; i++) {
            if (filled[start + 3 * i] == 0) {
              return start + 3 * i;
            }
          }
        }

        if (filled[5] == 1 || filled[3] == 1) {
          if (filled[3] == 0 || filled[5] == 0) {
            return filled[3] == 0 ? 3: 5;
          }

          // determine which row is empty of computer moves 
          int start = filled[0] == 1 || filled[2] == 1 ? 6 : 0;

          for (int i = 0; i < 3; i++) {
            if (filled[start + i] == 0) {
              return start + i;
            }
          }
        }

        break;
      case 8: // last turn

        // find last empty space
        for (int i = 0; i <= turn; i++) {
          if (filled[i] == 0) {
            return i;
          }
        }
        break;
    }
    return 0;
  }

  // sum of all filled squares for faster evaluation?

  public int evaluate2 (int xo) {
    switch (turn) {
      case 1:
        return xo == 4 ? 0 : 4;
        break;
      case 3:
        if (filled[4] == -1) {
          
        } else {
        
        }
        break;
      case 5:
        break;
      case 7:
        break;
    }
    return 0; 
  }

  public void keyReleased( KeyEvent evt ) {}

  public void keyTyped( KeyEvent evt ) {}

  public void mouseEntered (MouseEvent evt) {}

  public void mouseExited (MouseEvent evt) {}

  public void mousePressed (MouseEvent evt) {}

  public void mouseClicked (MouseEvent evt) {}

}
