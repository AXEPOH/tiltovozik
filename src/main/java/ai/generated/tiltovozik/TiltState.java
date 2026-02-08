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

/**
 * Перечисление состояний тильта с соответствующими символами и цветами
 */
public enum TiltState {
    ABSOLUTE_CALM(-200, -151, "АБСОЛЮТНОЕ СПОКОЙСТВИЕ", "☮", "#2980b9"),
    COLD_BLOOD(-150, -101, "ХЛАДНОКРОВИЕ", "❄", "#3498db"),
    BALANCE(-100, -51, "РАВНОВЕСИЕ", "⚖", "#1abc9c"),
    NORMAL(-50, -1, "НОРМА", "✓", "#2ecc71"),
    NEUTRAL(0, 0, "НЕЙТРАЛЬНО", "◎", "#f1c40f"),
    LIGHT_TILT(1, 49, "ЛЕГКИЙ ТИЛЬТ", "⚠", "#f39c12"),
    TILT(50, 99, "ТИЛЬТ", "⚡", "#e67e22"),
    RAGE(100, 149, "ЯРОСТЬ", "🔥", "#e74c3c"),
    APOCALYPSE(150, 200, "АПОКАЛИПСИС", "☠", "#8b0000");
    
    private final int minValue;
    private final int maxValue;
    private final String displayName;
    private final String symbol;
    private final String color;
    
    TiltState(int minValue, int maxValue, String displayName, String symbol, String color) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.displayName = displayName;
        this.symbol = symbol;
        this.color = color;
    }
    
    /**
     * Определяет состояние тильта по числовому значению
     * @param значение тильта от -200 до 200
     * @return соответствующее состояние тильта
     */
    public static TiltState fromValue(int value) {
        for (TiltState state : values()) {
            if (value >= state.minValue && value <= state.maxValue) {
                return state;
            }
        }
        return NEUTRAL;
    }
    
    /**
     * Возвращает полное отображаемое имя состояния с символом
     */
    public String getFullDisplayName() {
        return displayName + " " + symbol;
    }
    
    /**
     * Возвращает цвет, соответствующий состоянию
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Проверяет, является ли состояние экстремальным (Ярость или Апокалипсис)
     */
    public boolean isExtreme() {
        return this == RAGE || this == APOCALYPSE;
    }
    
    public int getMinValue() {
        return minValue;
    }
    
    public int getMaxValue() {
        return maxValue;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getSymbol() {
        return symbol;
    }
}