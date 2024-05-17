package training.courses.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<EmployeeResource> findEmployeesByIdIn(List<Long> ids);
}
