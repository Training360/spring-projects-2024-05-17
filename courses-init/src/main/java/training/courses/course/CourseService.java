package training.courses.course;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import training.courses.employee.EmployeeRepository;
import training.courses.employee.EmployeeResource;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {

    private CourseRepository courseRepository;

    private EmployeeRepository employeeRepository;

    public CourseResource createCourse(CourseResource courseResource) {
        var course = new Course(courseResource.name());
        courseRepository.save(course);
        return new CourseResource(course.getId(), course.getName());
    }

    @Transactional
    public void enroll(EnrollmentResource enrollment) {
        var course = courseRepository.findById(enrollment.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + enrollment.courseId()));
        course.getEnrolledEmployees().add(enrollment.employeeId());
    }

    public CourseResource findById(Long id) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
        return new CourseResource(course.getId(), course.getName());
    }

    public List<EmployeeResource> findEmployeesToCourse(Long id) {
        var course = courseRepository.findByIdWithEnrolledEmployees(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
//        return course.getEnrolledEmployees()
//                .stream().map(e -> new EmployeeResource(e.getId(), e.getName())).toList();
        return employeeRepository.findEmployeesByIdIn(course.getEnrolledEmployees());
    }

}
