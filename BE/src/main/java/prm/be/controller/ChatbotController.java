package prm.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.chatbot.ChatbotRequest;
import prm.be.dto.response.chatbot.ChatbotResponse;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import prm.be.service.ChatbotService;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final AccountService accountService;

    /**
     * Chatbot endpoint - DEALER có thể hỏi về xe, kho, thống kê
     */
    @PostMapping("/ask")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ChatbotResponse> askQuestion(@RequestBody ChatbotRequest request) {
        try {
            // Lấy thông tin user hiện tại
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = accountService.getAccountByUsername(username);

            // Xử lý câu hỏi bằng AI
            ChatbotResponse response = chatbotService.processQuestion(request.getQuestion(), account.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ChatbotResponse.builder()
                            .answer("Xin lỗi, có lỗi xảy ra khi xử lý câu hỏi của bạn.")
                            .intent("error")
                            .data("")
                            .timestamp(new java.util.Date())
                            .build());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }
}
