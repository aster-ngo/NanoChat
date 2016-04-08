package codelab.gdg.nanochat;

/**
 * Created by khacpham on 2/29/16.
 */
public class ChatMessage {
    private String name;
    private String text;

    public ChatMessage() {
        // dùng cho Firebase's deserializer
    }

    public ChatMessage(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}