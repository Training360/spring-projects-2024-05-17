package training;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql(statements = "delete from enrollment")
class EnrollmentServiceIT {

    @Autowired
    EnrollmentService enrollmentService;

    @Test
    void enroll() {
        var enrollment = enrollmentService.enroll(new EnrollCommand(1, 1));
        var result = enrollmentService.findById(enrollment.id());
        assertEquals(EnrollmentState.ENROLLED, result.enrollmentState());
    }

    @Test
    void complete() {
        var enrollment = enrollmentService.enroll(new EnrollCommand(1, 1));
        enrollmentService.complete(new CompleteCommand(1, 1));
        var result = enrollmentService.findById(enrollment.id());
        assertEquals(EnrollmentState.COMPLETED, result.enrollmentState());
    }
}
