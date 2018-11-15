/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import seabattle.Board.Cell;

/**
 *
 * @author Kamil
 */
public class SeaBattle extends Application {
    
    private Random rand = new Random();
    private boolean gameStarted = false;
    private Board computerBoard, playerBoard;    
    private int[] shipsSizes = new int[] {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private int shipsPlaced = 0;
    private boolean computerTurn = false;
    private Label resultLabel = new Label();
    private Label actualStateLabel = new Label();


    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(1300, 600);
        
        Label titleLabel = new Label("SeaBattle");
        HBox hTitleLabel = new HBox(titleLabel);
        hTitleLabel.setAlignment(Pos.BASELINE_CENTER);
        titleLabel.setFont(new Font(72));
        
        VBox actualStateVbox = new VBox();
        
        actualStateLabel.setFont(new Font(18));
        actualStateLabel.setMinWidth(200);
        actualStateLabel.textAlignmentProperty().set(TextAlignment.CENTER);
        
        computerBoard = new Board(true, event -> {
            if (!gameStarted) return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot) return;

            computerTurn = !cell.shoot();
            
            int playerShips = playerBoard.shipsCount;
            int computerShips = computerBoard.shipsCount;
            actualStateLabel.setText("Game just started!\n\nComputer ships: " + computerShips + "\nPlayer ships: " + playerShips);

            if (computerBoard.shipsCount == 0) resultLabel.textProperty().set("YOU WIN");

            if (computerTurn) computerMove();
        });
        
        VBox actualShipToPlace = new VBox();
        displayActualShipToPlace(actualShipToPlace, shipsSizes[0]);
        
        playerBoard = new Board(false, event -> {
            if (gameStarted) return;

            Cell cell = (Cell) event.getSource();
            
            if (playerBoard.placeShipIntoBoard(
                    new Ship(shipsSizes[shipsPlaced], event.getButton() == MouseButton.PRIMARY)
                    , cell.x, cell.y)) {
                
                shipsPlaced++;
                
                if(shipsPlaced < shipsSizes.length - 1) displayActualShipToPlace(actualShipToPlace, shipsSizes[shipsPlaced]);
                
                if(shipsPlaced == shipsSizes.length) {
                    actualShipToPlace.getChildren().clear();
                    startGame();
                }
            }
        });
        
        VBox computerVbox = new VBox();
        Label computerLabel = new Label("Computer Board");
        computerVbox.getChildren().addAll(computerLabel, computerBoard);
        
        VBox playerVbox = new VBox();
        Label playerLabel = new Label("Player Board");
        playerVbox.getChildren().addAll(playerLabel, playerBoard);
        
        
        
        if (actualShipToPlace.getChildren().toArray().length > 0) {
            actualStateLabel.setText("Place ships on board!\nLeft click: VERTICAL\nRight click: HORIZONTAL\n\n");
        }
        
        actualShipToPlace.setAlignment(Pos.CENTER);
        actualStateVbox.getChildren().addAll(actualStateLabel, actualShipToPlace);
        
        actualStateVbox.setAlignment(Pos.TOP_CENTER);
        
        HBox hBoards = new HBox(100, computerVbox, actualStateVbox, playerVbox);   
        hBoards.setAlignment(Pos.TOP_CENTER);
        hBoards.setPadding(new Insets(0, 0, 0, 0));
        
        
        HBox hResultLabel = new HBox(resultLabel);
        hResultLabel.setAlignment(Pos.BASELINE_CENTER);
        resultLabel.setFont(new Font(50));
        
        
        VBox mainV = new VBox(10, hTitleLabel, hBoards, hResultLabel);
        
        root.setCenter(mainV);
        
        return root;
    }
    
    public void displayActualShipToPlace (VBox vbox, Integer shipSize) {
        vbox.getChildren().clear();
        for (int i = 0; i < shipsSizes[shipsPlaced]; i++) {
            Rectangle rect = new Rectangle(40, 40);
            rect.setStroke(Color.BLACK);
            rect.setFill(Color.GREEN);
            vbox.getChildren().add(rect);
        }
    }

    private void computerMove() {
        while (computerTurn) {
            int row = rand.nextInt(10);
            int column = rand.nextInt(10);

            Cell cell = playerBoard.getCell(column, row);
            if (cell.wasShot) continue;

            computerTurn = cell.shoot();

            if (playerBoard.shipsCount == 0) resultLabel.textProperty().set("YOU LOSE");
        }
    }

    private void startGame() {
        int playerShips = playerBoard.shipsCount;
        int computerShips = computerBoard.shipsCount;
        actualStateLabel.setText("Game just started!\n\nComputer ships: " + computerShips + "\nPlayer ships: " + playerShips);
        
        for (int size : shipsSizes) {
            int row = rand.nextInt(10);
            int column = rand.nextInt(10);
            boolean shipPlaced = computerBoard.placeShipIntoBoard(new Ship(size, Math.random() < 0.5), column, row);
            
            while (!shipPlaced) {
                column = rand.nextInt(10);
                row = rand.nextInt(10);
                shipPlaced = computerBoard.placeShipIntoBoard(new Ship(size, Math.random() < 0.5), column, row);
            }
        }
        gameStarted = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("SeaBattle");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
