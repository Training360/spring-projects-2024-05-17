package training;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;

    @Transactional
    public EnrollmentDto enroll(EnrollCommand enrollCommand) {
        enrollmentRepository.findEnrollmentByEmployeeIdAndCourseId(enrollCommand.employeeId(), enrollCommand.courseId())
                .ifPresent(enrollment -> toDto(enrollment));

        var enrollment = new Enrollment(enrollCommand.employeeId(), enrollCommand.courseId());
        enrollment.setEnrollmentState(EnrollmentState.initial());
        enrollmentRepository.save(enrollment);

        return toDto(enrollment);
    }

    @Transactional
    public void complete(CompleteCommand completeCommand) {
        var enrollment = enrollmentRepository.findEnrollmentByEmployeeIdAndCourseId(completeCommand.employeeId(), completeCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        enrollment.setEnrollmentState(enrollment.getEnrollmentState().sendEvent(EnrollmentEvent.COMPLETE));
    }

    public EnrollmentDto findById(long id) {
        var enrollment = enrollmentRepository.findById(id).orElseThrow();
        log.info("Get: {}", enrollment.getEnrollmentState());
        return toDto(enrollment);
    }

    private EnrollmentDto toDto(Enrollment enrollment) {
        return new EnrollmentDto(enrollment.getId(), enrollment.getEmployeeId(), enrollment.getCourseId(), enrollment.getEnrollmentState());
    }
}
