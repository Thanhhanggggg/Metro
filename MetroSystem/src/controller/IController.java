package controller;

public interface IController {
    void handleAction(String action, Object... params);
    boolean validate(Object input);
}
