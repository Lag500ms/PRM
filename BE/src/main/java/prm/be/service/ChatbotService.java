package prm.be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import prm.be.dto.response.chatbot.ChatbotResponse;
import prm.be.entity.Vehicle;
import prm.be.entity.VehicleInventory;
import prm.be.repository.VehicleRepository;
import prm.be.repository.VehicleInventoryRepository;
import prm.be.repository.OrderRepository;
import prm.be.repository.ScheduleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    @Value("${gemini.api.key:AIzaSyDFlWOFU0ax64CmChbdt9hrSbljN0fNy0U}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String geminiApiUrl;

    private final VehicleRepository vehicleRepository;
    private final VehicleInventoryRepository vehicleInventoryRepository;
    private final OrderRepository orderRepository;
    private final ScheduleRepository scheduleRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Xử lý câu hỏi từ user và trả về câu trả lời thông minh với fallback logic
     */
    public ChatbotResponse processQuestion(String question, String accountId) {
        try {
            // 1. Phân tích intent với fallback
            String intent = analyzeIntentWithFallback(question);

            // 2. Lấy dữ liệu từ database dựa trên intent
            String data = retrieveDataFromDatabase(intent, accountId);

            // 3. Tạo câu trả lời với fallback
            String answer = generateAnswerWithFallback(question, data, intent);

            return ChatbotResponse.builder()
                    .answer(answer)
                    .intent(intent)
                    .data(data)
                    .timestamp(new Date())
                    .build();

        } catch (Exception e) {
            log.error("Error processing chatbot question: {}", e.getMessage());
            return ChatbotResponse.builder()
                    .answer("Xin lỗi, tôi không thể xử lý câu hỏi này lúc này. Vui lòng thử lại sau.")
                    .intent("error")
                    .data("")
                    .timestamp(new Date())
                    .build();
        }
    }

    /**
     * Phân tích intent với fallback logic
     */
    private String analyzeIntentWithFallback(String question) {
        try {
            // Thử Gemini AI trước
            return analyzeIntentWithGemini(question);
        } catch (Exception e) {
            log.warn("Gemini API failed, using local pattern matching: {}", e.getMessage());
            // Fallback: Local pattern matching
            return analyzeIntentLocally(question);
        }
    }

    /**
     * Local intent analysis khi Gemini API không available
     */
    private String analyzeIntentLocally(String question) {
        String lowerQuestion = question.toLowerCase().trim();

        // Greeting patterns
        if (lowerQuestion.matches(".*(hello|hi|chào|xin chào|chào bạn|hey).*")) {
            return "greeting";
        }

        // Help patterns
        if (lowerQuestion.matches(".*(help|giúp|hướng dẫn|trợ giúp).*")) {
            return "help";
        }

        // Inventory patterns
        if (lowerQuestion.matches(".*(kho|inventory|xe trong kho|số lượng xe|có bao nhiêu xe).*")) {
            return "inventory_count";
        }

        // Vehicle patterns
        if (lowerQuestion.matches(".*(danh sách xe|list xe|xe nào|xe gì).*")) {
            return "vehicle_list";
        }

        // Statistics patterns
        if (lowerQuestion.matches(".*(thống kê|statistics|báo cáo|doanh số).*")) {
            return "statistics";
        }

        // Orders patterns
        if (lowerQuestion.matches(".*(đơn hàng|order|orders).*")) {
            return "orders";
        }

        // Schedules patterns
        if (lowerQuestion.matches(".*(lịch hẹn|schedule|appointment).*")) {
            return "schedules";
        }

        // Default to general
        return "general";
    }

    /**
     * Phân tích intent của câu hỏi bằng Gemini AI với system prompt rõ ràng
     */
    private String analyzeIntentWithGemini(String question) {
        try {
            String systemPrompt = """
                    Bạn là AI Assistant cho hệ thống quản lý xe hơi của dealer.
                    Nhiệm vụ: Phân tích câu hỏi và xác định intent chính xác.

                    Context:
                    - User là DEALER trong hệ thống quản lý xe
                    - Có thể hỏi về kho xe, đơn hàng, lịch hẹn, thống kê
                    - Cần trả lời thân thiện và chuyên nghiệp

                    Các intent có thể:
                    - greeting: Chào hỏi (hello, hi, xin chào, chào bạn)
                    - inventory_count: Hỏi về số lượng xe trong kho
                    - vehicle_list: Hỏi về danh sách xe
                    - vehicle_details: Hỏi về chi tiết xe cụ thể
                    - statistics: Hỏi về thống kê doanh số
                    - orders: Hỏi về đơn hàng
                    - schedules: Hỏi về lịch hẹn
                    - help: Hỏi về cách sử dụng
                    - general: Câu hỏi chung khác

                    Quy tắc:
                    1. Ưu tiên greeting nếu có từ chào hỏi
                    2. Phân tích ngữ cảnh để xác định intent chính xác
                    3. Trả về chỉ tên intent (ví dụ: greeting)
                    """;

            String prompt = String.format("""
                    %s

                    Câu hỏi cần phân tích: "%s"

                    Trả về chỉ tên intent:
                    """, systemPrompt, question);

            String response = callGeminiAPI(prompt);
            return response.trim().toLowerCase();

        } catch (Exception e) {
            log.error("Error analyzing intent: {}", e.getMessage());
            return "general";
        }
    }

    /**
     * Lấy dữ liệu từ database dựa trên intent với RAG cải thiện
     */
    private String retrieveDataFromDatabase(String intent, String accountId) {
        try {
            switch (intent) {
                case "greeting":
                    return getGreetingResponse();
                case "help":
                    return getHelpResponse();
                case "inventory_count":
                    return getInventoryCount(accountId);
                case "vehicle_list":
                    return getVehicleList();
                case "vehicle_details":
                    return getVehicleDetails();
                case "statistics":
                    return getStatistics(accountId);
                case "orders":
                    return getOrdersInfo(accountId);
                case "schedules":
                    return getSchedulesInfo(accountId);
                default:
                    return "Không có dữ liệu cụ thể cho intent: " + intent;
            }
        } catch (Exception e) {
            log.error("Error retrieving data: {}", e.getMessage());
            return "Lỗi khi lấy dữ liệu từ database";
        }
    }

    /**
     * Tạo câu trả lời với fallback logic
     */
    private String generateAnswerWithFallback(String question, String data, String intent) {
        try {
            // Thử Gemini AI trước
            return generateAnswerWithGemini(question, data);
        } catch (Exception e) {
            log.warn("Gemini API failed for answer generation, using local responses: {}", e.getMessage());
            // Fallback: Local responses
            return generateAnswerLocally(intent, data);
        }
    }

    /**
     * Local answer generation khi Gemini API không available
     */
    private String generateAnswerLocally(String intent, String data) {
        switch (intent) {
            case "greeting":
                return "Xin chào! Tôi là AI Assistant của hệ thống quản lý xe hơi. Tôi sẽ giúp bạn quản lý kho xe, đơn hàng và lịch hẹn của cửa hàng. Bạn cần hỗ trợ gì về cửa hàng xe hơi? 😊";
            case "help":
                return getContextualHelpResponse();
            case "inventory_count":
                return data.isEmpty() ? "Kho của bạn hiện tại chưa có xe nào. Hãy liên hệ admin để nhận xe vào kho!"
                        : data;
            case "vehicle_list":
                return data.isEmpty() ? "Hiện tại chưa có xe nào trong hệ thống. Hãy liên hệ admin để thêm xe!" : data;
            case "statistics":
                return data.isEmpty() ? "Chưa có thống kê cửa hàng. Vui lòng thử lại sau." : data;
            case "orders":
                return data.isEmpty() ? "Chưa có thông tin đơn hàng của cửa hàng. Vui lòng thử lại sau." : data;
            case "schedules":
                return data.isEmpty() ? "Chưa có thông tin lịch hẹn của cửa hàng. Vui lòng thử lại sau." : data;
            default:
                return "Tôi hiểu câu hỏi của bạn về cửa hàng xe hơi nhưng chưa thể trả lời chi tiết lúc này. Vui lòng thử lại sau hoặc hỏi cụ thể hơn! 😊";
        }
    }

    /**
     * Tạo câu trả lời thông minh bằng Gemini AI với system prompt rõ ràng
     */
    private String generateAnswerWithGemini(String question, String data) {
        try {
            String systemPrompt = """
                    Bạn là AI Assistant chuyên nghiệp cho hệ thống quản lý xe hơi của dealer.

                    Vai trò:
                    - Hỗ trợ dealer quản lý kho xe, đơn hàng, lịch hẹn
                    - Trả lời câu hỏi về thống kê, báo cáo
                    - Tư vấn về xe hơi và dịch vụ

                    Tính cách:
                    - Thân thiện, chuyên nghiệp, nhiệt tình
                    - Sử dụng ngôn ngữ dễ hiểu
                    - Luôn sẵn sàng hỗ trợ

                    Quy tắc trả lời:
                    1. Luôn trả lời bằng tiếng Việt
                    2. Sử dụng dữ liệu thực tế để trả lời chính xác
                    3. Nếu không có dữ liệu, hãy nói rõ và đề xuất cách khác
                    4. Giới hạn trong 200 từ
                    5. Thêm emoji phù hợp để thân thiện
                    6. Luôn kết thúc bằng câu hỏi để tương tác
                    """;

            String prompt = String.format("""
                    %s

                    Câu hỏi của dealer: "%s"
                    Dữ liệu từ database: "%s"

                    Tạo câu trả lời thông minh và hữu ích:
                    """, systemPrompt, question, data);

            return callGeminiAPI(prompt);

        } catch (Exception e) {
            log.error("Error generating answer: {}", e.getMessage());
            return "Xin lỗi, tôi không thể tạo câu trả lời lúc này. Vui lòng thử lại sau! 😊";
        }
    }

    /**
     * Gọi Gemini API với model chính thức và retry mechanism
     */
    private String callGeminiAPI(String prompt) {
        int maxRetries = 3;
        int retryDelay = 1000; // 1 second

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String url = geminiApiUrl + "?key=" + geminiApiKey;

                // Tạo request body với Jackson
                String requestBody = String.format("""
                        {
                            "contents": [{
                                "parts": [{
                                    "text": "%s"
                                }]
                            }]
                        }
                        """, prompt.replace("\"", "\\\"").replace("\n", "\\n"));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                // Parse response với Jackson
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                return responseJson.get("candidates")
                        .get(0)
                        .get("content")
                        .get("parts")
                        .get(0)
                        .get("text").asText();

            } catch (Exception e) {
                log.warn("Gemini API attempt {} failed: {}", attempt, e.getMessage());

                if (attempt == maxRetries) {
                    log.error("All Gemini API attempts failed after {} retries", maxRetries);
                    throw new RuntimeException("Gemini API không khả dụng sau " + maxRetries + " lần thử");
                }

                // Wait before retry
                try {
                    Thread.sleep(retryDelay * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted");
                }
            }
        }

        throw new RuntimeException("Unexpected error in retry logic");
    }

    // Các method để lấy dữ liệu từ database
    private String getGreetingResponse() {
        return "Xin chào! Tôi là AI Assistant của hệ thống quản lý xe hơi. Tôi sẽ giúp bạn quản lý kho xe, đơn hàng và lịch hẹn của cửa hàng. Bạn cần hỗ trợ gì về cửa hàng xe hơi? 😊";
    }

    private String getHelpResponse() {
        return getContextualHelpResponse();
    }

    /**
     * Tạo help response thông minh dựa trên context và trạng thái hệ thống
     */
    private String getContextualHelpResponse() {
        try {
            // Lấy thông tin tổng quan hệ thống
            long totalVehicles = vehicleRepository.count();
            long totalInventory = vehicleInventoryRepository.count();
            long totalOrders = orderRepository.count();
            long totalSchedules = scheduleRepository.count();

            StringBuilder helpResponse = new StringBuilder();
            helpResponse.append("🤖 **AI Assistant - Hệ thống Quản lý Xe hơi**\n\n");

            // System status
            helpResponse.append("📊 **Trạng thái hệ thống:**\n");
            helpResponse.append(String.format("• Tổng xe trong hệ thống: %d\n", totalVehicles));
            helpResponse.append(String.format("• Tổng kho xe: %d\n", totalInventory));
            helpResponse.append(String.format("• Tổng đơn hàng: %d\n", totalOrders));
            helpResponse.append(String.format("• Tổng lịch hẹn: %d\n\n", totalSchedules));

            // Main features với examples cụ thể
            helpResponse.append("🎯 **Tôi có thể giúp bạn:**\n\n");

            helpResponse.append("📦 **Quản lý Kho xe:**\n");
            helpResponse.append("• \"Kho của tôi có bao nhiêu xe?\"\n");
            helpResponse.append("• \"Xe nào còn ít trong kho?\"\n");
            helpResponse.append("• \"Kho tôi có những xe gì?\"\n");
            helpResponse.append("• \"Xe BMW X5 còn bao nhiêu?\"\n\n");

            helpResponse.append("🚗 **Xe có sẵn để nhận:**\n");
            helpResponse.append("• \"Có những xe nào tôi có thể nhận?\"\n");
            helpResponse.append("• \"Xe nào đang có sẵn?\"\n");
            helpResponse.append("• \"Xe Tesla Model 3 còn không?\"\n");
            helpResponse.append("• \"Xe Ferrari có sẵn không?\"\n\n");

            helpResponse.append("📊 **Thống kê & Báo cáo:**\n");
            helpResponse.append("• \"Doanh số tháng này của tôi?\"\n");
            helpResponse.append("• \"Tôi có bao nhiêu đơn hàng?\"\n");
            helpResponse.append("• \"Lịch hẹn tuần này?\"\n");
            helpResponse.append("• \"Thống kê bán hàng của tôi?\"\n\n");

            helpResponse.append("🔍 **Tìm kiếm & Lọc:**\n");
            helpResponse.append("• \"Xe BMW có những model nào?\"\n");
            helpResponse.append("• \"Xe màu đỏ còn bao nhiêu?\"\n");
            helpResponse.append("• \"Xe giá dưới 2 tỷ có không?\"\n\n");

            // Quick actions
            helpResponse.append("⚡ **Quick Actions:**\n");
            helpResponse.append("• \"Hiện trạng kho\" - Xem tổng quan kho\n");
            helpResponse.append("• \"Xe mới\" - Xe mới có sẵn\n");
            helpResponse.append("• \"Báo cáo\" - Thống kê tổng quan\n");
            helpResponse.append("• \"Lịch hẹn\" - Xem lịch hẹn\n\n");

            // Tips và best practices
            helpResponse.append("💡 **Mẹo sử dụng:**\n");
            helpResponse.append("• Hỏi bằng ngôn ngữ tự nhiên, tôi sẽ hiểu\n");
            helpResponse.append("• Có thể hỏi về xe cụ thể theo tên, màu, giá\n");
            helpResponse.append("• Tôi có thể tư vấn về xu hướng bán xe\n");
            helpResponse.append("• Luôn sẵn sàng hỗ trợ 24/7!\n\n");

            helpResponse.append("🎉 **Bắt đầu ngay:** Hãy hỏi tôi bất cứ điều gì về cửa hàng xe hơi của bạn!");

            return helpResponse.toString();

        } catch (Exception e) {
            log.error("Error generating contextual help: {}", e.getMessage());
            // Fallback to basic help
            return getBasicHelpResponse();
        }
    }

    /**
     * Help response cơ bản khi có lỗi
     */
    private String getBasicHelpResponse() {
        return """
                🤖 **AI Assistant - Hệ thống Quản lý Xe hơi**

                Tôi có thể giúp bạn:
                📦 Quản lý kho xe
                🚗 Xem xe có sẵn
                📊 Thống kê cửa hàng
                🔍 Tìm kiếm xe

                💡 Hãy hỏi tôi bằng ngôn ngữ tự nhiên!
                """;
    }

    private String getInventoryCount(String accountId) {
        try {
            // Lấy thông tin chi tiết inventory của dealer
            List<VehicleInventory> dealerInventory = vehicleInventoryRepository.findAll().stream()
                    .filter(vi -> vi.getInventory().getAccount().getId().equals(accountId))
                    .filter(vi -> vi.getQuantity() != null && vi.getQuantity() > 0)
                    .collect(Collectors.toList());

            if (dealerInventory.isEmpty()) {
                return "Kho của bạn hiện tại chưa có xe nào. Hãy liên hệ admin để nhận xe vào kho!";
            }

            // Tính tổng số xe
            long totalVehicles = dealerInventory.stream()
                    .mapToLong(vi -> vi.getQuantity())
                    .sum();

            // Tạo danh sách chi tiết từng xe
            StringBuilder detailInfo = new StringBuilder();
            detailInfo.append(String.format("Kho của bạn có tổng cộng %d xe:\n\n", totalVehicles));

            // Group theo category
            Map<String, List<VehicleInventory>> byCategory = dealerInventory.stream()
                    .collect(Collectors.groupingBy(vi -> vi.getVehicle().getCategory().getName()));

            for (Map.Entry<String, List<VehicleInventory>> entry : byCategory.entrySet()) {
                String categoryName = entry.getKey();
                List<VehicleInventory> vehicles = entry.getValue();

                detailInfo.append(String.format("📦 **%s:**\n", categoryName));

                for (VehicleInventory vi : vehicles) {
                    String vehicleInfo = String.format("  • %s %s (%s) - %d xe\n",
                            vi.getVehicle().getModel(),
                            vi.getVehicle().getVersion(),
                            vi.getVehicle().getColor(),
                            vi.getQuantity());
                    detailInfo.append(vehicleInfo);
                }
                detailInfo.append("\n");
            }

            return detailInfo.toString().trim();
        } catch (Exception e) {
            return "Không thể lấy thông tin kho của bạn";
        }
    }

    private String getVehicleList() {
        try {
            // Lấy danh sách vehicles có sẵn để dealer có thể nhận vào inventory
            List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                    .filter(v -> v.getQuantity() > 0) // Chỉ lấy xe còn hàng
                    .collect(Collectors.toList());

            if (vehicles.isEmpty()) {
                return "Hiện tại chưa có xe nào trong hệ thống. Hãy liên hệ admin để thêm xe!";
            }

            // Tạo danh sách chi tiết
            StringBuilder detailInfo = new StringBuilder();
            detailInfo.append("Các xe có sẵn để bạn nhận vào kho:\n\n");

            // Group theo category
            Map<String, List<Vehicle>> byCategory = vehicles.stream()
                    .collect(Collectors.groupingBy(v -> v.getCategory().getName()));

            for (Map.Entry<String, List<Vehicle>> entry : byCategory.entrySet()) {
                String categoryName = entry.getKey();
                List<Vehicle> categoryVehicles = entry.getValue();

                detailInfo.append(String.format("🚗 **%s:**\n", categoryName));

                for (Vehicle vehicle : categoryVehicles) {
                    String vehicleInfo = String.format("  • %s %s (%s) - Còn %d xe\n",
                            vehicle.getModel(),
                            vehicle.getVersion(),
                            vehicle.getColor(),
                            vehicle.getQuantity());
                    detailInfo.append(vehicleInfo);
                }
                detailInfo.append("\n");
            }

            return detailInfo.toString().trim();
        } catch (Exception e) {
            return "Không thể lấy danh sách xe có sẵn";
        }
    }

    private String getVehicleDetails() {
        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            if (vehicles.isEmpty()) {
                return "Không có xe nào trong hệ thống";
            }

            Vehicle vehicle = vehicles.get(0); // Lấy xe đầu tiên làm ví dụ
            return String.format("Xe: %s %s, Màu: %s, Giá: %s, Số lượng: %d",
                    vehicle.getModel(), vehicle.getVersion(), vehicle.getColor(),
                    vehicle.getPrice(), vehicle.getQuantity());
        } catch (Exception e) {
            return "Không thể lấy chi tiết xe";
        }
    }

    private String getStatistics(String accountId) {
        try {
            long totalOrders = orderRepository.findByAccount_Id(accountId,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();

            long totalSchedules = scheduleRepository.findByAccount_Id(accountId,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();

            return String.format("Thống kê: %d đơn hàng, %d lịch hẹn", totalOrders, totalSchedules);
        } catch (Exception e) {
            return "Không thể lấy thống kê";
        }
    }

    private String getOrdersInfo(String accountId) {
        try {
            long totalOrders = orderRepository.findByAccount_Id(accountId,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
            return String.format("Bạn có %d đơn hàng", totalOrders);
        } catch (Exception e) {
            return "Không thể lấy thông tin đơn hàng";
        }
    }

    private String getSchedulesInfo(String accountId) {
        try {
            long totalSchedules = scheduleRepository.findByAccount_Id(accountId,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
            return String.format("Bạn có %d lịch hẹn", totalSchedules);
        } catch (Exception e) {
            return "Không thể lấy thông tin lịch hẹn";
        }
    }
}
