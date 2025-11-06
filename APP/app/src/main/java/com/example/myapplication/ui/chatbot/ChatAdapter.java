package com.example.myapplication.ui.chatbot;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;

    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == TYPE_USER) ? R.layout.item_chat_user : R.layout.item_chat_bot;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        String formattedText = formatMessage(message.getText());
        holder.tvMessage.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? TYPE_USER : TYPE_BOT;
    }

    private String formatMessage(String text) {
        if (text == null) return "";
        
        // Escape HTML
        text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        
        // Format bold text **text** thành <b>text</b>
        text = text.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        
        // Format emoji và text
        text = text.replace("🚗", "🚗 ");
        
        // Xử lý bullet points và xuống dòng
        String[] lines = text.split("\n");
        StringBuilder formatted = new StringBuilder();
        boolean inList = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                if (inList) {
                    formatted.append("</ul><br/>");
                    inList = false;
                } else {
                    formatted.append("<br/>");
                }
            } else if (line.startsWith("•") || line.startsWith("-")) {
                if (!inList) {
                    formatted.append("<ul style='margin: 8px 0; padding-left: 20px;'>");
                    inList = true;
                }
                String itemText = line.substring(1).trim();
                formatted.append("<li style='margin: 4px 0;'>").append(itemText).append("</li>");
            } else {
                if (inList) {
                    formatted.append("</ul>");
                    inList = false;
                }
                formatted.append(line).append("<br/>");
            }
        }
        
        if (inList) {
            formatted.append("</ul>");
        }
        
        return formatted.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    public static class ChatMessage {
        private final String text;
        private final boolean isUser;

        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }

        public String getText() {
            return text;
        }

        public boolean isUser() {
            return isUser;
        }
    }
}

