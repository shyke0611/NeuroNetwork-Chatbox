package org.vaadin.example;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class OpenAI {

    public List<ChatMessage> send(String userInput) {
        return List.of(new ChatMessage("Bot", "I'm sorry, I don't understand that."));
    }

    public CompletableFuture<List<ChatMessage>> sendAsync(String userInput) {
        return CompletableFuture.completedFuture(send(userInput));
    }

    public static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
