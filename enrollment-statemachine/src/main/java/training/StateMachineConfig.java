package training;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<EnrollmentState, EnrollmentEvent> {



    @Override
    public void configure(StateMachineStateConfigurer<EnrollmentState, EnrollmentEvent> states) throws Exception {
        states
                .withStates()
                .initial(EnrollmentState.ENROLLED)
                .states(EnumSet.allOf(EnrollmentState.class))
                .end(EnrollmentState.COMPLETED)
                .end(EnrollmentState.REFUSED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EnrollmentState, EnrollmentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(EnrollmentState.ENROLLED).event(EnrollmentEvent.COMPLETE).target(EnrollmentState.COMPLETED)
                .and()
                .withExternal()
                .source(EnrollmentState.ENROLLED).event(EnrollmentEvent.REFUSE).target(EnrollmentState.REFUSED);
    }
}
