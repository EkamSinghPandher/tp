package seedu.address.model.meeting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

public class Time {
    public static final String MESSAGE_CONSTRAINTS =
            "Times should be in the format HH:MM";
    public static final String VALIDATION_REGEX = "\\d{3,}";
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    public final LocalTime value;

    /**
     * Constructs a {@code Time}.
     *
     * @param time A valid time.
     */
    public Time(String time) {
        requireNonNull(time);
        checkArgument(isValidTime(time), MESSAGE_CONSTRAINTS);
        value = LocalTime.parse(time, INPUT_FORMAT);
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidTime(String test) {
        return test.length() == 5
                && test.charAt(2) == ':';
        //todo: check if time is valid
    }

    @Override
    public String toString() {
        return value.format(OUTPUT_FORMAT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Time // instanceof handles nulls
                && value.equals(((Time) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
