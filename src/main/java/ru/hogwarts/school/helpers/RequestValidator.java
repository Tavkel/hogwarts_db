package ru.hogwarts.school.helpers;

public class RequestValidator {
    public static <T extends Number & Comparable<T>> void validateRange(T floor, T ceiling) {
        if (floor.compareTo(ceiling) >= 0) {
            throw new IllegalArgumentException("Floor value must be lower than ceiling value.");
        }
    }

    public static void validateMustBeZero(long input, String key) {
        if(input != 0) {
            throw new IllegalArgumentException(key + " must be equal zero for this operation.");
        }
    }

    public static void validateMustBeGreaterThanZero(long input, String key) {
        if(input <= 0) {
            throw new IllegalArgumentException(key + " must be greater than zero for this operation.");
        }
    }
}
