package training.courses.course;

import java.util.List;

public record CourseResource(Long id, String name) {

    public CourseResource(String name) {
        this(null, name);
    }
}
