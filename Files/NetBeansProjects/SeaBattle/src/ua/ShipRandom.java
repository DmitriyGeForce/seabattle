/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author User
 */
public class ShipRandom {
    static int shipcells = 1;
    static String[] alphabet = new String[]{"A", "B", "C"};
    static Set<String> allShipsAndBorders = new HashSet<>();
    static Set<String> allShips = new HashSet<>();

    static Set<String> randomship() {
        Set<String> ship = new HashSet<>();
        Set<String> cells = new HashSet<>();
        int position = (int) (Math.random() * 2); // random choice of vertical or horizontal position
        if (position == 0) {                      // vertical position
            int randomLetter = (int) (Math.random() * 3);
            int randomNumber = (int) (Math.random() * (10 - shipcells) + 1);
            for (int i = 0; i < shipcells; i++) {
                cells.add(alphabet[randomLetter] + (randomNumber + i));
            }
        } else if (position == 1) {               // horizontal position
            int randomLetter = (int) (Math.random() * (3 - shipcells));
            int randomNumber = (int) (Math.random() * 10 + 1);
            for (int i = 0; i < shipcells; i++) {
                cells.add(alphabet[randomLetter + i] + randomNumber);
            }
        }
        boolean occupied = false;
        for (String cell : cells) {                   // check if any cells are occupied, if so it start again
            if (allShipsAndBorders.contains(cell)) {
                occupied = true;
            }
        }
        if (occupied) {
            ship = randomship();
        } else {
            allShipsAndBorders.addAll(cells);
            allShips.addAll(cells);
            addBorder(cells);
            ship.addAll(cells);
            shipcells++;
        }
        return ship;
    }

    static void addBorder(Set<String> cells) { // method which add borders to every cell to prevent touching allShips
        for (String cell : cells) {
            String letter = cell.charAt(0) + "";
            int iLetter = 0;
            int occupied = 0;
            for (String l : alphabet) {
                if (l.equals(letter)) {
                    iLetter = occupied;
                }
                occupied++;
            }
            int number = Integer.parseInt(cell.charAt(1) + "");
            if (number == 1) {
                allShipsAndBorders.add(letter + 2);
            }
            if (number == 10) {
                allShipsAndBorders.add(letter + 9);
            }
            if (letter.equals("A")) {
                allShipsAndBorders.add("B" + number);
            }
            if (letter.equals("C")) {
                allShipsAndBorders.add("I" + number);
            }
            if (!letter.equals("A") && !letter.equals("C") && number != 1 && number != 10) {
                allShipsAndBorders.add(letter + (number + 1));
                allShipsAndBorders.add(letter + (number - 1));
                allShipsAndBorders.add(alphabet[iLetter + 1] + number);
                allShipsAndBorders.add(alphabet[iLetter - 1] + number);

                allShipsAndBorders.add(alphabet[iLetter + 1] + (number + 1));
                allShipsAndBorders.add(alphabet[iLetter + 1] + (number - 1));
                allShipsAndBorders.add(alphabet[iLetter - 1] + (number + 1));
                allShipsAndBorders.add(alphabet[iLetter - 1] + (number - 1));

            }
        }
    }
}
