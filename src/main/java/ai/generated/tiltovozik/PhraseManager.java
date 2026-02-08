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

import java.io.*;
import java.util.*;

/**
 * Менеджер для загрузки и управления фразами побед и поражений
 * с защитой от повторного показа одних и тех же фраз подряд
 */
public class PhraseManager {
    private static final int DEFAULT_HISTORY_SIZE = 5;
    
    private final List<String> winPhrases = new ArrayList<>();
    private final List<String> losePhrases = new ArrayList<>();
    private final Queue<String> winHistory = new ArrayDeque<>(DEFAULT_HISTORY_SIZE);
    private final Queue<String> loseHistory = new ArrayDeque<>(DEFAULT_HISTORY_SIZE);
    private final int historySize;
    
    public PhraseManager() {
        this(DEFAULT_HISTORY_SIZE);
    }
    
    public PhraseManager(int historySize) {
        this.historySize = historySize;
    }
    
    /**
     * Загружает фразы из файлов
     * @param winPhrasesFile путь к файлу с победными фразами
     * @param losePhrasesFile путь к файлу с фразами поражений
     * @throws IOException если произошла ошибка чтения файлов
     */
    public void loadPhrases(String winPhrasesFile, String losePhrasesFile) throws IOException {
        winPhrases.clear();
        losePhrases.clear();
        
        winPhrases.addAll(loadPhrasesFromFile(winPhrasesFile));
        losePhrases.addAll(loadPhrasesFromFile(losePhrasesFile));
        
        // Добавляем дефолтные фразы, если файлы пустые
        if (winPhrases.isEmpty()) {
            winPhrases.addAll(getDefaultWinPhrases());
        }
        if (losePhrases.isEmpty()) {
            losePhrases.addAll(getDefaultLosePhrases());
        }
        
        System.out.printf("Загружено фраз: побед - %d, поражений - %d%n", 
                         winPhrases.size(), losePhrases.size());
    }
    
    /**
     * Получить случайную фразу с защитой от повторений
     * @param isWin true для победной фразы, false для фразы поражения
     * @param username имя игрока для подстановки в шаблон
     * @param random экземпляр Random для генерации случайных чисел
     * @return отформатированная фраза с именем игрока
     */
    public String getRandomPhrase(boolean isWin, String username, Random random) {
        List<String> phrases = isWin ? winPhrases : losePhrases;
        Queue<String> history = isWin ? winHistory : loseHistory;
        
        if (phrases.isEmpty()) {
            return isWin ? "Победа!" : "Поражение...";
        }
        
        // Создаем список доступных фраз, исключая недавно использованные
        List<String> availablePhrases = new ArrayList<>(phrases);
        availablePhrases.removeAll(history);
        
        // Если все фразы уже были недавно, сбрасываем историю
        if (availablePhrases.isEmpty()) {
            availablePhrases = new ArrayList<>(phrases);
            history.clear();
        }
        
        // Выбираем случайную фразу из доступных
        String message = availablePhrases.get(random.nextInt(availablePhrases.size()));
        
        // Обновляем историю
        history.offer(message);
        if (history.size() > historySize) {
            history.poll();
        }
        
        // Заменяем плейсхолдер на имя пользователя
        return message.replace("{username}", username);
    }
    
    /**
     * Очищает историю показанных фраз
     */
    public void clearHistory() {
        winHistory.clear();
        loseHistory.clear();
    }
    
    /**
     * Возвращает количество загруженных победных фраз
     */
    public int getWinPhrasesCount() {
        return winPhrases.size();
    }
    
    /**
     * Возвращает количество загруженных фраз поражений
     */
    public int getLosePhrasesCount() {
        return losePhrases.size();
    }
    
    private List<String> loadPhrasesFromFile(String filename) throws IOException {
        List<String> phrases = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        
        if (inputStream == null) {
            File file = new File(filename);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            phrases.add(line.trim());
                        }
                    }
                }
            }
        } else {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        phrases.add(line.trim());
                    }
                }
            }
        }
        return phrases;
    }
    
    private List<String> getDefaultWinPhrases() {
        return Arrays.asList(
            "Так держать, {username}!",
            "Отличная игра, {username}!"
        );
    }
    
    private List<String> getDefaultLosePhrases() {
        return Arrays.asList(
            "{username}, время для секретного оружия!",
            "{username}, следующая игра будет лучше!"
        );
    }
}