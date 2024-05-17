package training;

public record EnrollmentDto(Long id, long employeeId, long courseId, EnrollmentState enrollmentState) {
}
