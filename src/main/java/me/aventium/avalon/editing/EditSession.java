package me.aventium.avalon.editing;

import org.bukkit.entity.Player;

public class EditSession {

    private Player user;
    private Editor editor;

    public EditSession(Player player, Editor editor) {
        this.user = player;
        this.editor = editor;
    }

    public Player getUser() {
        return user;
    }

    public Editor getEditor() {
        return editor;
    }

}
