package org.vaadin.example;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatbotModel chatbotModel;

    public ChatService(ChatbotModel chatbotModel) {
        this.chatbotModel = chatbotModel;
    }

    public String getResponse(String userInput) {
        return chatbotModel.predict(userInput);
    }
}
