package training;

public enum EnrollmentState {

    ENROLLED {
        @Override
        public EnrollmentState sendEvent(EnrollmentEvent event) {
            if (event == EnrollmentEvent.COMPLETE) {
                return EnrollmentState.COMPLETED;
            } else if (event == EnrollmentEvent.REFUSE) {
                return EnrollmentState.REFUSED;
            }
            return this;
        }
    }, REFUSED {
    }, COMPLETED {};

    public EnrollmentState sendEvent(EnrollmentEvent event) {
        return this;
    }

    public static EnrollmentState initial() {
        return ENROLLED;
    }
}
