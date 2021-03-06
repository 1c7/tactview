package com.helospark.tactview.ui.javafx.inputmode.strategy;

public interface InputTypeStrategy<T> {

    public default void onMouseDownEvent(StrategyMouseInput input) {
    }

    public default void onMouseMovedEvent(StrategyMouseInput input) {
    }

    public default void onMouseUpEvent(StrategyMouseInput input) {
    }

    public default void onMouseDraggedEvent(StrategyMouseInput input) {
    }

    public default void onKeyPressedEvent(StrategyKeyInput input) {
    }

    public ResultType getResultType();

    public void draw(DrawRequestParameter parameterObject);

    public T getResult();

    public default String getStatusMessage() {
        return "Use left mouse putton to place or drag points\nRight click to finish, esc to cancel";
    }

}
