package prm.be.dto.response.schedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleResponses {

    @Data
    public static class ScheduleResponse {
        private String id;
        private String customer;
        private String phone;
        private String address;
        private LocalDateTime dateTime;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
