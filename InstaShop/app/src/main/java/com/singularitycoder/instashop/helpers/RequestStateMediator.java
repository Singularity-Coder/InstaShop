package com.singularitycoder.instashop.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public final class RequestStateMediator<T, E, V, K> {

    @Nullable
    private T dataObject;

    @NonNull
    private E status;

    @Nullable
    private V message;

    @Nullable
    private K key;

    public void set(@Nullable T dataObject, @NonNull E status, @Nullable V message, @Nullable K key) {
        this.dataObject = dataObject;
        this.status = status;
        this.message = message;
        this.key = key;
    }

    public final T getData() {
        return dataObject;
    }

    @NotNull
    public final E getStatus() {
        return status;
    }

    @Nullable
    public final V getMessage() {
        return message;
    }

    @Nullable
    public final K getKey() {
        return key;
    }
}
