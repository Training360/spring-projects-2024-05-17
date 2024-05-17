package training.courses;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import training.courses.course.CourseResource;
import training.courses.course.CourseService;
import training.courses.course.EnrollmentResource;
import training.courses.employee.EmployeeResource;
import training.courses.employee.EmployeeService;
import training.courses.employee.LeaveResource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CourseIT {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CourseService courseService;


    @Test
    void testEnroll() {
        var employee = employeeService.createEmployee(new EmployeeResource("John Doe"));
        var course = courseService.createCourse(new CourseResource("Java"));
        courseService.enroll(new EnrollmentResource(course.id(), employee.id()));
        var employees = courseService.findEmployeesToCourse(course.id());
        assertThat(employees).extracting(EmployeeResource::name).containsExactly("John Doe");
    }

    @Test
    void testLeave() {
        var employee = employeeService.createEmployee(new EmployeeResource("John Doe"));
        var course = courseService.createCourse(new CourseResource("Java"));
        courseService.enroll(new EnrollmentResource(course.id(), employee.id()));

        employeeService.leave(new LeaveResource(employee.id(), LocalDate.now()));

        var employees = courseService.findEmployeesToCourse(course.id());
        assertThat(employees).isEmpty();
    }

}
