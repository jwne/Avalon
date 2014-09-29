package me.aventium.avalon.editing;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditSessions {

    private static List<EditSession> sessions = new ArrayList<>();

    public static void createNewSession(Player user, Editor editor) {
        removeSession(user);
        EditSession session = new EditSession(user, editor);
        sessions.add(session);
    }

    public static EditSession getSession(Player user, Editor editor) {
        for(EditSession session : sessions) {
            if(session.getUser().getUniqueId().equals(user.getUniqueId()) && session.getEditor().name().equals(editor.name())) {
                return session;
            }
        }
        return null;
    }

    public static void removeSession(Player user) {
        EditSession remove = null;
        for(EditSession session : sessions) {
            if(session.getUser().getUniqueId().equals(user.getUniqueId())) {
                remove = session;
            }
        }

        if(remove != null) sessions.remove(remove);
    }

}
