/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Kamil
 */
public class Board extends Parent {
    public int shipsCount = 10;
    private VBox rows = new VBox();
    private boolean isComputerTurn = false;

    public Board(boolean isComputerTurn, EventHandler<? super MouseEvent> eventHandler) {
        this.isComputerTurn = isComputerTurn;
        
        for (int i = 0; i < 10; i++) {
            HBox row = new HBox();
            for (int j = 0; j < 10; j++) {
                Cell cell = new Cell(j, i, this);
                cell.setOnMouseClicked(eventHandler);
                row.getChildren().add(cell);
            }
            rows.getChildren().add(row);
        }

        getChildren().add(rows);
    }

    public boolean placeShipIntoBoard(Ship ship, int x, int y) {
        if (checkIfShipCanBePlace(ship, x, y)) {
            int length = ship.size;

            if (ship.verticalPosition) {
                for (int i = y; i < y + length; i++) {
                    Cell cell = getCell(x, i);
                    cell.ship = ship;
                    if (!isComputerTurn) cell.setFill(Color.GREEN);
                }
            } else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    cell.ship = ship;
                    if (!isComputerTurn) cell.setFill(Color.GREEN);
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean checkIfShipCanBePlace(Ship ship, int x, int y) {
        int size = ship.size;

        if (ship.verticalPosition) {
            for (int i = y; i < y + size; i++) {
                if (!isPointValid(x, i)) return false;
                Cell cell = getCell(x, i);
                if (cell.ship != null) return false;

                for (Cell neighbor : getNeighbors(x, i)) {
                    if (!isPointValid(x, i)) return false;
                    if (neighbor.ship != null) return false;
                }
            }
        }
        else {
            for (int i = x; i < x + size; i++) {
                if (!isPointValid(i, y))
                    return false;

                Cell cell = getCell(i, y);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, y)) {
                    if (!isPointValid(i, y)) return false;

                    if (neighbor.ship != null) return false;
                }
            }
        }

        return true;
    }

    public Cell getCell(int col, int row) {
        return (Cell)((HBox)rows.getChildren().get(row)).getChildren().get(col);
    }

    private Cell[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Cell> neighbors = new ArrayList<>();

        for (Point2D point : points) {
            if (isPointValid(point)) {
                neighbors.add(getCell((int)point.getX(), (int)point.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    private boolean isPointValid(Point2D point) {
        return isPointValid(point.getX(), point.getY());
    }

    private boolean isPointValid(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    public class Cell extends Rectangle {
        public Ship ship = null;
        public int x, y;
        public boolean wasShot = false;

        private Board board;

        public Cell(int x, int y, Board board) {
            super(40, 40);
            this.x = x;
            this.y = y;
            this.board = board;
            setStroke(Color.BLACK);
            setFill(Color.GRAY);
        }

        public boolean shoot() {
            wasShot = true;
            setFill(Color.BLACK);

            if (ship != null) {
                ship.shooted();
                setFill(Color.RED);
                if (!ship.isAlive()) {
                    board.shipsCount--;
                }
                return true;
            }

            return false;
        }
    }
}