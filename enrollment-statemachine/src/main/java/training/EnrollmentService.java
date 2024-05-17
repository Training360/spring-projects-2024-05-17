package training;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;

    private StateMachineFactory<EnrollmentState, EnrollmentEvent>  stateMachineFactory;

    @Transactional
    public EnrollmentDto enroll(EnrollCommand enrollCommand) {
        enrollmentRepository.findEnrollmentByEmployeeIdAndCourseId(enrollCommand.employeeId(), enrollCommand.courseId())
                .ifPresent(enrollment -> toDto(enrollment));

        var enrollment = new Enrollment(enrollCommand.employeeId(), enrollCommand.courseId());

        enrollmentRepository.save(enrollment);

        var stateMachine = initStateMachine(enrollment.getId(), null);
        log.info("New state: {}", stateMachine.getState().getId());
        enrollment.setEnrollmentState(stateMachine.getState().getId());

        return toDto(enrollment);
    }

    @Transactional
    public void complete(CompleteCommand completeCommand) {
        var enrollment = enrollmentRepository.findEnrollmentByEmployeeIdAndCourseId(completeCommand.employeeId(), completeCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        var stateMachine = initStateMachine(enrollment.getId(), enrollment.getEnrollmentState());
        stateMachine.sendEvent(Mono.just(new GenericMessage<>(EnrollmentEvent.COMPLETE))).subscribe();
        enrollment.setEnrollmentState(stateMachine.getState().getId());
    }

    public EnrollmentDto findById(long id) {
        var enrollment = enrollmentRepository.findById(id).orElseThrow();
        log.info("Get: {}", enrollment.getEnrollmentState());
        return toDto(enrollment);
    }

    private EnrollmentDto toDto(Enrollment enrollment) {
        return new EnrollmentDto(enrollment.getId(), enrollment.getEmployeeId(), enrollment.getCourseId(), enrollment.getEnrollmentState());
    }

    private StateMachine<EnrollmentState, EnrollmentEvent> initStateMachine(long id, EnrollmentState enrollmentState) {
        var stateMachine = stateMachineFactory.getStateMachine(Long.toString(id));
        if (enrollmentState != null) {
            stateMachine.getStateMachineAccessor().doWithAllRegions(access ->
                    access.resetStateMachineReactively(new DefaultStateMachineContext<>(enrollmentState, null, null, null)).subscribe());
        }
        stateMachine.startReactively().subscribe();

        return stateMachine;
    }
}
