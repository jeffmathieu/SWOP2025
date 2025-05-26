package com.tablr.undoRedo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages execution of commands and undo/redo functionality.
 * Commands are tracked using two stacks: one for undo and one for redo.
 */
public class CommandManager {
    /** Stack holding undoable commands */
    private final Deque<Command> undoStack = new ArrayDeque<>();
    /** Stack holding re-doable commands */
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /**
     * Looks at the head of the undoStack but does not change the stack.
     * @return head of undoStack.
     */
    public Command peekUndo() {
        return undoStack.peek();
    }

    /**
     * Looks at the head of the redoStack but does not change the stack.
     * @return head of redoStack.
     */
    public Command peekRedo() {
        return redoStack.peek();
    }

    /**
     * Executes given command and stores it in undo stack.
     * Clears the redo stack since redo history becomes invalid after a new command.
     *
     * @param command | the command to execute
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes last command in undo stack (if there is one).
     * Moves command to redo stack.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Re-executes last command in redo stack (if there is one).
     * Moves command to undo stack.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

}