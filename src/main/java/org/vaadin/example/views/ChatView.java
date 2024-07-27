package org.vaadin.example.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.example.OpenAI;

@Route("")
@CssImport("./css/styles.css")
public class ChatView extends VerticalLayout {

    private Div chatContainer;
    private TextField messageInput;
    private final OpenAI openAI;

    public ChatView(OpenAI openAI) {
        this.openAI = openAI;

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

            openAI.sendAsync(userInput).whenComplete((messages, throwable) -> {
                if (throwable == null) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        messages.forEach(message -> addMessage("Bot", message.getContent()));
                    }));
                }
            });
        }
    }

    private void addMessage(String sender, String content) {
        Div messageDiv = new Div();
        messageDiv.setText(sender + ": " + content);
        messageDiv.addClassName(sender.equals("User") ? "user-message" : "bot-message");
        chatContainer.add(messageDiv);
        scrollToEnd();
    }

    private void scrollToEnd() {
        getElement().executeJs("this.querySelector('#chat-container').scrollTop = this.querySelector('#chat-container').scrollHeight;");
    }
}
