// Generic inputs from the front end

package sprouse.cameron.blackjack.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import sprouse.cameron.blackjack.gameLogic.GameStateManager;
import sprouse.cameron.blackjack.gameio.GameMessage;
import sprouse.cameron.blackjack.gameio.MessageManager;

@Controller
public class WebSocketMessageController {

    private GameStateManager gameStateManager;

    public WebSocketMessageController(SimpMessagingTemplate messagingTemplate) {
        MessageManager.setMessagingTemplate(messagingTemplate);
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String receiveMessage(GameMessage message) {
        if (message == null || message.getAction() == null || message.getAction().isBlank()) {
            return "Invalid game action.";
        }

         switch (message.getAction()) {

        case "ADD_PLAYER":
            if (message.getId() == null || message.getUsername() == null || message.getUsername().isBlank()) {
                return "Invalid player ID or username.";
            }
            if (gameStateManager == null) {
                gameStateManager = new GameStateManager();
            }
            gameStateManager.AddPlayerToGame(
                message.getId(),
                message.getUsername()
            );
            break;

        case "START_ROUND":
            gameStateManager.startRound(
                message.getWager()
            );
            break;

        case "SPLIT_HAND":
            gameStateManager.splitHand(
                message.getPlayerId(),
                message.getHandArrayNum()
            );
            break;

        case "ADD_CARD":
            gameStateManager.addCard(
                message.getPlayerId(),
                message.getUsername(),
                message.getHandArrayNum(),
                message.getAddedCardNumber()
            );
            break;

        case "HOLD_HAND":
            gameStateManager.holdHand(
                message.getPlayerId(),
                message.getUsername(),
                message.getHandArrayNum()
            );
            break;

        case "END_ROUND":
            gameStateManager.endRound();
            break;

        case "UPDATE_ACCOUNT":
            gameStateManager.updateAccountRound();
            break;

        case "END_GAME":
            gameStateManager.endGame();
            break;

        case "PLAYER_EXIT":
            gameStateManager.playerExit(
                message.getPlayerId()
            );
            break;
        case "ADD_CREDITS":
            gameStateManager.addCredits(
                message.getPlayerId()
            );
            break;
        default:
            throw new IllegalArgumentException(
                "Unknown action: " + message.getAction()
            );
        }

        return null;
    }


}