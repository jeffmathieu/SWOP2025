package com.tablr.undoRedo;

/**
 * Represents undoable command.
 * Each command needs to specify how it is executed and undone.
 */
public interface Command {

    /**
     * Executes the command action.
     */
    void execute();

    /**
     * Reverts the effects of this action.
     */
    void undo();
}