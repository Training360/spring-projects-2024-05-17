package training.courses.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select distinct c from Course c left join fetch c.enrolledEmployees where c.id = :id")
    Optional<Course> findByIdWithEnrolledEmployees(long id);
}
