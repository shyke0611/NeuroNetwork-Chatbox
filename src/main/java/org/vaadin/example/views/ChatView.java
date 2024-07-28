package org.vaadin.example.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.example.ChatService;

@Route("")
@CssImport("./css/styles.css")
public class ChatView extends VerticalLayout {

    private Div chatContainer;
    private TextField messageInput;
    private final ChatService chatService;

    
    public ChatView(ChatService chatService) {
        this.chatService = chatService;

        chatContainer = new Div();
        chatContainer.setId("chat-container");

        messageInput = new TextField();
        messageInput.setPlaceholder("Type your message...");
        messageInput.setWidthFull();

        Button sendButton = new Button("Send", event -> onSubmit());
        sendButton.setId("send-button");

        HorizontalLayout inputLayout = new HorizontalLayout(messageInput, sendButton);
        inputLayout.setId("input-container");

        add(chatContainer, inputLayout);
        setSizeFull();
        setHorizontalComponentAlignment(Alignment.CENTER, chatContainer, inputLayout);

        messageInput.addKeyDownListener(Key.ENTER, event -> onSubmit());
    }

    private void onSubmit() {
        String userInput = messageInput.getValue();
        if (!userInput.trim().isEmpty()) {
            addMessage("User", userInput);
            messageInput.clear();

            String botResponse = chatService.getResponse(userInput);
            addMessage("Bot", botResponse);
        }
    }

    private void addMessage(String sender, String content) {
        Div messageDiv = new Div();
        messageDiv.setText(content);

        Image profilePicture = new Image();
        profilePicture.setClassName("profile-picture");

        HorizontalLayout messageLayout;
        if ("User".equals(sender)) {
            profilePicture.setSrc("images/user.png");
            messageLayout = new HorizontalLayout(messageDiv, profilePicture);
            messageLayout.addClassName("user-message-container");
            messageDiv.addClassName("user-message");
        } else {
            profilePicture.setSrc("images/bot.png");
            messageLayout = new HorizontalLayout(profilePicture, messageDiv);
            messageLayout.addClassName("bot-message-container");
            messageDiv.addClassName("bot-message");
        }

        chatContainer.add(messageLayout);
        scrollToEnd();
    }

    private void scrollToEnd() {
        getElement().executeJs("this.querySelector('#chat-container').scrollTop = this.querySelector('#chat-container').scrollHeight;");
    }
}
