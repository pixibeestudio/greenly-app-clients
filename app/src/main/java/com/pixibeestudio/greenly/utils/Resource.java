package com.pixibeestudio.greenly.utils;

public class Resource<T> {
    public enum Status { SUCCESS, ERROR, LOADING }

    public final Status status;
    public final T data;
    public final String message;
    public final com.pixibeestudio.greenly.data.model.ErrorResponse errorData;

    private Resource(Status status, T data, String message, com.pixibeestudio.greenly.data.model.ErrorResponse errorData) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.errorData = errorData;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> error(String msg, com.pixibeestudio.greenly.data.model.ErrorResponse errorData) {
        return new Resource<>(Status.ERROR, null, msg, errorData);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, null);
    }
}
