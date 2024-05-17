package training.courses.employee;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResource createEmployee(EmployeeResource employeeResource) {
        var employee = new Employee(employeeResource.name());
        employeeRepository.save(employee);
        return new EmployeeResource(employee.getId(), employee.getName());
    }

    @Transactional
    public void leave(LeaveResource leave) {
        var employee = findById(leave.employeeId());
        employee.setLeavedAt(leave.leavedAt());
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
    }

    public List<EmployeeResource> findEmployeesByIds(List<Long> ids) {
        return employeeRepository.findEmployeesByIdIn(ids)
                .stream().map(e -> new EmployeeResource(e.id(), e.name())).toList();
    }
}
