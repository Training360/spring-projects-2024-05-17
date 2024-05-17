package training.courses.course;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "courses_enrolled_id"))
//    @AttributeOverride(name="enrolledEmployees", column = @Column(name = "enrolled_employees_id"))
    @Column(name = "enrolled_employees_id")
    private List<Long> enrolledEmployees;

    public Course(String name) {
        this.name = name;
    }
}
