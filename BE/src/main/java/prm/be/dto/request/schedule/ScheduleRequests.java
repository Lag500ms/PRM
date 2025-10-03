package prm.be.dto.request.schedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRequests {

    @Data
    public static class CreateScheduleRequest {
        @NotBlank
        private String customer;
        @NotBlank
        private String phone;
        @NotBlank
        private String address;
        @NotNull
        @Future
        private LocalDateTime dateTime;
    }

    @Data
    public static class UpdateScheduleRequest {
        @NotBlank
        private String id;
        @NotBlank
        private String customer;
        @NotBlank
        private String phone;
        @NotBlank
        private String address;
        @NotNull
        @Future
        private LocalDateTime dateTime;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotBlank
        private String id;
        @NotBlank
        private String status;
    }
}
