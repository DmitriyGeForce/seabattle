/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

/**
 *
 * @author User
 */
public class GameLogic {

    static Set<Set<String>> shipSets;
    static int count = 0;
    static int x = 0;
    JRootPane pane;
    private static GameFrame frame;
    
    GameLogic(GameFrame frame, JRootPane pane){
        GameLogic.frame = frame;
        this.pane = pane;
    }
    
    GameLogic() {
        
    }
    
    static void fillShipSets() {
        shipSets = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            shipSets.add(ShipRandom.randomship());
        }
        for (Set<String> ship : shipSets) {
            System.out.println(ship);        // location of all three ships
        }
    }

    void GameAction(String userMove, JLabel cell) {
        count++;
        if (ShipRandom.allShips.contains(userMove)) {
            Iterator<Set<String>> ships = shipSets.iterator();
            while (ships.hasNext()) {
                Set<String> ship = ships.next();
                if (ship.contains(userMove)) {
                    cell.setText("");
                    ship.remove(userMove);
                    ShipRandom.allShips.remove(userMove);
                    System.out.println("Попал");
                    ImageIcon icon = new ImageIcon("boom.jpg");
                    GameLogic.frame.setTitle("Попал");
                    ImageIcon imageIcon = new ImageIcon(icon.getImage().getScaledInstance(cell.getWidth() - 8, cell.getHeight() - 8, Image.SCALE_SMOOTH));
                    cell.setIcon(imageIcon);
                    cell.setEnabled(false);
                    MouseListener[] mouseListeners = cell.getMouseListeners();
                    cell.removeMouseListener(mouseListeners[0]);
                    if (ship.isEmpty()) {
                        GameLogic.frame.setTitle("Потопил");
                        System.out.println("Потопил");
                        if (++x == 3) {
                            JOptionPane.showMessageDialog(pane, "Вы попедили и вам потребовалось " + count + " попыток");
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("Мимо");
            cell.setBackground(Color.cyan);
            GameLogic.frame.setTitle("Мимо");
        }
    }
}
