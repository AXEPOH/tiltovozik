/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ai.generated.tiltovozik;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.util.*;

public class TiltTrackerApp extends Application {
    
    private static final int MIN_TILT = -200;
    private static final int MAX_TILT = 200;
    private static final String WIN_PHRASES_FILE = "win_phrases.txt";
    private static final String LOSE_PHRASES_FILE = "lose_phrases.txt";
    
    private String username;
    private int currentTilt;
    private PhraseManager phraseManager;
    private final Random random = new Random(); // Единый экземпляр Random
    
    // UI компоненты
    private Label tiltLabel;
    private ProgressBar tiltBar;
    private Label messageLabel;
    private StackPane messageContainer;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        phraseManager = new PhraseManager();
        try {
            phraseManager.loadPhrases(WIN_PHRASES_FILE, LOSE_PHRASES_FILE);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки фраз: " + e.getMessage());
        }
        
        showLoginDialog();
        
        primaryStage.setTitle("🔥 Tiltovozik - " + username);
        primaryStage.setScene(createMainScene());
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void showLoginDialog() {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Добро пожаловать в Tiltovozik!");
        dialog.setHeaderText("Введите данные игрока");
        
        TextField usernameField = new TextField("Игрок");
        usernameField.setPromptText("Имя игрока");
        
        Spinner<Integer> tiltSpinner = new Spinner<>(MIN_TILT, MAX_TILT, 0);
        tiltSpinner.setEditable(true);
        
        GridPane grid = createLoginGrid(usernameField, tiltSpinner);
        dialog.getDialogPane().setContent(grid);
        
        setupDialogButtons(dialog);
        
        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresentOrElse(pair -> {
            username = pair.getKey().isEmpty() ? "Игрок" : pair.getKey();
            currentTilt = pair.getValue();
        }, Platform::exit);
    }
    
    private GridPane createLoginGrid(TextField usernameField, Spinner<Integer> tiltSpinner) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        grid.add(new Label("Имя игрока:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Начальный уровень тильта:"), 0, 1);
        grid.add(tiltSpinner, 1, 1);
        
        return grid;
    }
    
    private void setupDialogButtons(Dialog<Pair<String, Integer>> dialog) {
        ButtonType loginButtonType = new ButtonType("Начать отслеживание", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                TextField usernameField = (TextField) ((GridPane) dialog.getDialogPane().getContent())
                    .getChildren().get(1);
                Spinner<Integer> tiltSpinner = (Spinner<Integer>) ((GridPane) dialog.getDialogPane().getContent())
                    .getChildren().get(3);
                return new Pair<>(usernameField.getText(), tiltSpinner.getValue());
            }
            return null;
        });
    }
    
    private Scene createMainScene() {
        VBox root = createRootLayout();
        
        root.getChildren().addAll(
            createHeader(),
            createTiltScale(),
            createMessageContainer(),
            createButtons()
        );
        
        updateTiltBar();
        
        return new Scene(root, 420, 420); // Увеличили высоту окна
    }
    
    private VBox createRootLayout() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");
        return root;
    }
    
    private HBox createHeader() {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label fireIcon = new Label("🔥");
        fireIcon.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label("TILTOVOZIK");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        headerBox.getChildren().addAll(fireIcon, titleLabel);
        return headerBox;
    }
    
    private VBox createTiltScale() {
        VBox scaleBox = new VBox(5);
        scaleBox.setAlignment(Pos.CENTER);
        scaleBox.setPadding(new Insets(15, 0, 10, 0));
        scaleBox.setMinHeight(80);
        
        tiltBar = new ProgressBar();
        tiltBar.setPrefWidth(380);
        tiltBar.setPrefHeight(28);
        tiltBar.setStyle("-fx-accent: #3498db; -fx-border-color: #95a5a6; -fx-border-width: 1px; -fx-border-radius: 4px;");
        
        tiltLabel = new Label();
        tiltLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        tiltLabel.setMinHeight(40);
        tiltLabel.setAlignment(Pos.CENTER);
        
        scaleBox.getChildren().addAll(tiltBar, tiltLabel);
        return scaleBox;
    }
    
    private StackPane createMessageContainer() {
        messageLabel = new Label("Удачи в игре! 🎮");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(380);
        messageLabel.setMaxWidth(380);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPadding(new Insets(12, 15, 12, 15));
        
        messageContainer = new StackPane(messageLabel);
        messageContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); " +
                                 "-fx-background-radius: 10px; -fx-padding: 12; " +
                                 "-fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 10px;");
        messageContainer.setMaxWidth(380);
        messageContainer.setMinHeight(100); // Увеличили высоту контейнера
        messageContainer.setPrefHeight(100);
        
        return messageContainer;
    }
    
    private HBox createButtons() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 5, 0));
        
        Button winButton = createStyledButton("ПОБЕДА 🏆", true);
        Button loseButton = createStyledButton("ПОРАЖЕНИЕ 💀", false);
        
        winButton.setOnAction(e -> handleWin());
        loseButton.setOnAction(e -> handleLose());
        
        buttonBox.getChildren().addAll(winButton, loseButton);
        return buttonBox;
    }
    
    private Button createStyledButton(String text, boolean isWin) {
        Button button = new Button(text);
        String baseColor = isWin ? "#2ecc71" : "#e74c3c";
        String hoverColor = isWin ? "#27ae60" : "#c0392b";
        
        String baseStyle = String.format(
            "-fx-font-size: 16px; -fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; -fx-padding: 12 28; " +
            "-fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;",
            baseColor, hoverColor
        );
        
        String hoverStyle = String.format(
            "-fx-font-size: 16px; -fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; -fx-padding: 12 28; " +
            "-fx-background-radius: 6px; -fx-border-radius: 6px; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);",
            hoverColor, isWin ? "#219653" : "#a93226"
        );
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }
    
    private void handleWin() {
        int decrease = random.nextInt(5) + 1; // Используем единый Random
        currentTilt = Math.max(MIN_TILT, currentTilt - decrease);
        updateTiltBar();
        showRandomMessage(true);
    }
    
    private void handleLose() {
        int increase = random.nextInt(9) + 2; // Используем единый Random
        currentTilt = Math.min(MAX_TILT, currentTilt + increase);
        updateTiltBar();
        showRandomMessage(false);
    }
    
    private void updateTiltBar() {
        if (tiltBar == null || tiltLabel == null) return;
        
        double normalized = (currentTilt - MIN_TILT) / (double) (MAX_TILT - MIN_TILT);
        tiltBar.setProgress(normalized);
        
        TiltState currentState = TiltState.fromValue(currentTilt);
        
        String style = "-fx-accent: " + currentState.getColor() + ";";
        if (currentState.isExtreme()) {
            style += " -fx-effect: dropshadow(gaussian, rgba(231, 76, 60, 0.3), 15, 0, 0, 0);";
        }
        
        tiltBar.setStyle(style + " -fx-border-color: #95a5a6; -fx-border-width: 1px; -fx-border-radius: 4px;");
        tiltLabel.setText(currentState.getFullDisplayName());
        
        // Используем цвет из enum, а не дублируем логику
        tiltLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + 
                          currentState.getColor() + ";");
    }
    
    private void showRandomMessage(boolean isWin) {
        if (messageLabel == null || messageContainer == null) return;
        
        String message = phraseManager.getRandomPhrase(isWin, username, random);
        
        // Убрали обрезку сообщений - теперь все фразы будут отображаться полностью
        messageLabel.setText(message);
        
        if (isWin) {
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60; -fx-font-weight: 500;");
            messageContainer.setStyle("-fx-background-color: rgba(46, 204, 113, 0.1); " +
                                     "-fx-background-radius: 10px; -fx-padding: 12; " +
                                     "-fx-border-color: #2ecc71; -fx-border-width: 1px; -fx-border-radius: 10px;");
        } else {
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #c0392b; -fx-font-weight: 500;");
            messageContainer.setStyle("-fx-background-color: rgba(231, 76, 60, 0.1); " +
                                     "-fx-background-radius: 10px; -fx-padding: 12; " +
                                     "-fx-border-color: #e74c3c; -fx-border-width: 1px; -fx-border-radius: 10px;");
        }
    }
    
    private static class Pair<K, V> {
        private final K key;
        private final V value;
        
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() { return key; }
        public V getValue() { return value; }
    }
}