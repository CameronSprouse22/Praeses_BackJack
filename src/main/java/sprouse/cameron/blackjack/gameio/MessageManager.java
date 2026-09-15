package sprouse.cameron.blackjack.gameio;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MessageManager {
 
    private static SimpMessagingTemplate messagingTemplate;

    public static void setMessagingTemplate(SimpMessagingTemplate template) {
        messagingTemplate = template;
    }

    public static void promptUpdateJson(String json, String string) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.ChatMessage+":"+string);
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.JsonUpdate+":"+json);
        }
    }

    public static void messageUser(String badPlayerDataString) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", badPlayerDataString);
        }
    }

    public static void gameTableCreated(String string) {
        messageUser(EnumGameFunction.PlayerJoin + ":" + string);
    }

    public static void playerAdded(String string) {
        messageUser(EnumGameFunction.PlayerJoin + ":" + string);
    }

    public static void playerAlreadyAdded(String string) {
        messageUser(EnumGameFunction.PlayerJoin + ":" + string);
    }

    public static void giveUserError(Object object, int id, String string) {
        promptUpdateJson(object == null ? null : object.toString(),
                EnumGameFunction.ErrorMessage + " for player " + id + ": " + string);
    }




}
