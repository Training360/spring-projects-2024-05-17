package training.courses.employee;

import java.time.LocalDate;

public record LeaveResource(Long employeeId, LocalDate leavedAt) {
}
