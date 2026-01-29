package com.gtceu.syncsystem;

import com.gtceu.syncsystem.transformers.ValueTransformer;

import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Represents a class with fields that have sync annotations.
 * <p>
 * A field of type {@code T} can be marked with sync annotations if:
 * <ul>
 * <li>{@code T} is primitive
 * <li>{@code T} has a {@link ValueTransformer} registered
 * <li>{@code T} implements {@link INBTSerializable INBTSerializable&lt;T&gt;}
 * <li>{@code T} implements {@link ISyncManaged}
 * </ul>
 *
 * @see SyncDataHolder
 */
public interface ISyncManaged {

    SyncDataHolder getSyncDataHolder();

    /**
     * Function called when a synced field requests a rerender
     */
    void scheduleRenderUpdate();

    /**
     * Function called to notify the server that this object has been updated and must be synced to clients
     */
    void markAsChanged();
}
