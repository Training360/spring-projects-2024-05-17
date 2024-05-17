package training.courses.employee;

import lombok.Data;

public record EmployeeResource(Long id, String name) {

    public EmployeeResource(String name) {
        this(null, name);
    }
}
