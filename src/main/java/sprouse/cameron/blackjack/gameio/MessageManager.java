package sprouse.cameron.blackjack.gameio;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MessageManager {
 
    private static SimpMessagingTemplate messagingTemplate;

    public static void setMessagingTemplate(SimpMessagingTemplate template) {
        messagingTemplate = template;
    }

    
    public static void playerLogin( int userId) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.PlayerLogin+": "+userId);
        }
    }

    //send round json added with log
    public static void promptUpdateJson(String json, String string) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.Log+":"+string);
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.JsonUpdate+":"+json);
        }
    }

    public static void messageSingleUser(String message, int userId) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", userId);
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.SingleUser+":"+userId+" "+message);
        }
    }

    public static void messageAllUser(String message) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/messages", EnumGameFunction.AllUsers+": "+message);
        }
    }

}