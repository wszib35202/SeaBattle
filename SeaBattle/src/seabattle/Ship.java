/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle;

import javafx.scene.Parent;

/**
 *
 * @author Kamil
 */
public class Ship extends Parent {
    public int size;
    private int leftLives;
    public boolean verticalPosition = true;

    public Ship(int size, boolean verticalPosition) {
        this.size = size;
        leftLives = size;
        this.verticalPosition = verticalPosition;
    }

    public boolean isAlive() {
        return leftLives > 0;
    }
    
    public void shooted() {
        leftLives--;
    }
}