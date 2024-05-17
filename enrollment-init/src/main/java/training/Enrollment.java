package training;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long employeeId;

    private long courseId;

    @Enumerated(EnumType.STRING)
    private EnrollmentState enrollmentState;

    public Enrollment(long employeeId, long courseId) {
        this.employeeId = employeeId;
        this.courseId = courseId;
    }
}
