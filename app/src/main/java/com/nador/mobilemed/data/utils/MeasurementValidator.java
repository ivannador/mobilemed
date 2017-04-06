package com.nador.mobilemed.data.utils;

/**
 * Created by nador on 05/12/2016.
 */

public abstract class MeasurementValidator {

    public enum Result {
        EMPTY, LOW, HIGH, OK
    }

    protected static <T extends Number> Result validate(T value, T lowThreshold, T highThreshold) {
        if (value.doubleValue() > highThreshold.doubleValue()) {
            return Result.HIGH;
        }

        if (value.doubleValue() < lowThreshold.doubleValue()) {
            return Result.LOW;
        }

        return Result.OK;
    }

    private static Result validate(final String value) { return null; }

    public static class BloodPressureValidator extends MeasurementValidator {

        private static final Long SYS_THRESHOLD_HIGH = 250L;
        private static final Long SYS_THRESHOLD_LOW = 60L;

        private static final Long DIA_THRESHOLD_HIGH = 200L;
        private static final Long DIA_THRESHOLD_LOW = 50L;

        private static final Long PUL_THRESHOLD_HIGH = 200L;
        private static final Long PUL_THRESHOLD_LOW = 40L;

        public static Result validateSystolic(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Long.parseLong(value), SYS_THRESHOLD_LOW, SYS_THRESHOLD_HIGH);
        }

        public static Result validateDiastolic(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Long.parseLong(value), DIA_THRESHOLD_LOW, DIA_THRESHOLD_HIGH);
        }

        public static Result validatePulse(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Long.parseLong(value), PUL_THRESHOLD_LOW, PUL_THRESHOLD_HIGH);
        }
    }

    public static class GlucometerValidator extends MeasurementValidator {

        private static final Float THRESHOLD_HIGH = 20f;
        private static final Float THRESHOLD_LOW = 0f;

        public static Result validate(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Float.parseFloat(value), THRESHOLD_LOW, THRESHOLD_HIGH);
        }
    }

    public static class TemperatureValidator extends MeasurementValidator {

        private static final Float THRESHOLD_HIGH = 45f;
        private static final Float THRESHOLD_LOW = 24f;

        public static Result validate(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Float.parseFloat(value), THRESHOLD_LOW, THRESHOLD_HIGH);
        }
    }

    public static class WeightValidator extends MeasurementValidator {

        private static final Float THRESHOLD_HIGH = 400f;
        private static final Float THRESHOLD_LOW = 20f;

        public static Result validate(final String value) {
            if (value.isEmpty()) {
                return Result.EMPTY;
            }
            return validate(Float.parseFloat(value), THRESHOLD_LOW, THRESHOLD_HIGH);
        }
    }
}
