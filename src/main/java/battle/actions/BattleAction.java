package battle.actions;

public interface BattleAction {
    String name();         // For displaying/logging

    void execute();        // Actually perform the action
}


