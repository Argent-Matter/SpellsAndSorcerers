package dev.screret.modularui.value;

import dev.screret.modularui.api.value.IValue;

import lombok.Getter;
import lombok.Setter;

/**
 * @deprecated use {@link ObjectValue} instead
 */
@Deprecated
public class ConstValue<T> implements IValue<T> {

    @Getter
    @Setter
    protected T value;

    public ConstValue(T value) {
        this.value = value;
    }
}
