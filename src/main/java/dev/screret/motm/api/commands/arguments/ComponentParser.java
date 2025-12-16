package dev.screret.motm.api.commands.arguments;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class ComponentParser<T, V, B, R> {
    static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType(
            componentType -> Component.translatableEscape("arguments.item.component.unknown", componentType)
    );
    static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType(
            (componentType, error) -> Component.translatableEscape("arguments.item.component.malformed", componentType, error)
    );
    static final SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(
            Component.translatable("arguments.item.component.expected")
    );
    static final DynamicCommandExceptionType ERROR_REPEATED_COMPONENT = new DynamicCommandExceptionType(
            componentType -> Component.translatableEscape("arguments.item.component.repeated", componentType)
    );
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;

    public static final char SYNTAX_START_COMPONENTS = '[';
    public static final char SYNTAX_END_COMPONENTS = ']';
    public static final char SYNTAX_COMPONENT_SEPARATOR = ',';
    public static final char SYNTAX_COMPONENT_ASSIGNMENT = '=';
    public static final char SYNTAX_REMOVED_COMPONENT = '!';

    protected final DynamicOps<Tag> nbtOps;
    protected final Supplier<B> builder;
    protected final Function<B, R> resultProvider;
    protected final KeyProvider<T> keyProvider;
    protected final CodecProvider<T, V> codecProvider;

    protected final Setter<B, T, V> setter;
    protected final @Nullable Remover<B, T> remover;

    protected final boolean requireListStartEnd;
    protected final boolean allowComponentRemoval;

    public ComponentParser(DynamicOps<Tag> nbtOps, Supplier<B> builder, Function<B, R> resultProvider,
                           KeyProvider<T> keyProvider, CodecProvider<T, V> codecProvider,
                           Setter<B, T, V> setter, @Nullable Remover<B, T> remover,
                           boolean requireListStartEnd, boolean allowComponentRemoval) {
        this.nbtOps = nbtOps;
        this.builder = builder;
        this.resultProvider = resultProvider;
        this.codecProvider = codecProvider;
        this.keyProvider = keyProvider;

        this.setter = setter;
        this.remover = remover;

        this.requireListStartEnd = requireListStartEnd;
        this.allowComponentRemoval = allowComponentRemoval && remover != null;
    }

    public static <T> ComponentParser.DataComponents<T> dataComponents(HolderLookup.Provider registries,
                                                                       Registry<DataComponentType<? extends T>> componentTypeRegistry,
                                                                       boolean requireListStartEnd, boolean allowComponentRemoval) {
        return ComponentParser.dataComponents(registries.createSerializationContext(NbtOps.INSTANCE), componentTypeRegistry,
                requireListStartEnd, allowComponentRemoval);
    }

    public static <T> ComponentParser.DataComponents<T> noContextDataComponents(Registry<DataComponentType<? extends T>> componentTypeRegistry,
                                                                                boolean requireListStartEnd, boolean allowComponentRemoval) {
        return ComponentParser.dataComponents(NbtOps.INSTANCE, componentTypeRegistry, requireListStartEnd, allowComponentRemoval);
    }

    public static <T> ComponentParser.DataComponents<T> dataComponents(DynamicOps<Tag> nbtOps,
                                                                       Registry<DataComponentType<? extends T>> componentTypeRegistry,
                                                                       boolean requireListStartEnd, boolean allowComponentRemoval) {
        return new ComponentParser.DataComponents<>(nbtOps, componentTypeRegistry, requireListStartEnd, allowComponentRemoval);
    }

    public R parse(StringReader reader) throws CommandSyntaxException {
        final B builder = this.builder.get();
        this.parse(reader, new Visitor<>() {

            @Override
            public void visitValue(T type, V value) {
                setter.set(builder, type, value);
            }

            @Override
            public void visitRemovedValue(T type) {
                if (remover != null) remover.remove(builder, type);
            }
        });
        return resultProvider.apply(builder);
    }

    public void parse(StringReader reader, Visitor<T, V> visitor) throws CommandSyntaxException {
        int originalCursor = reader.getCursor();
        try {
            new State(reader, visitor).parse();
        } catch (CommandSyntaxException exception) {
            reader.setCursor(originalCursor);
            throw exception;
        }
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        SuggestionsVisitor<T, V> suggestions = new SuggestionsVisitor<>();
        State state = new State(reader, suggestions);

        try {
            state.parse();
        } catch (CommandSyntaxException ignored) {
        }

        return suggestions.resolveSuggestions(builder, reader);
    }

    protected class State {
        private final StringReader reader;
        private final Visitor<T, V> visitor;

        State(StringReader reader, Visitor<T, V> visitor) {
            this.reader = reader;
            this.visitor = visitor;
        }

        public void parse() throws CommandSyntaxException {
            this.visitor.visitSuggestions(this::suggestStartComponents);
            if (this.reader.canRead() && (!ComponentParser.this.requireListStartEnd || this.reader.peek() == SYNTAX_START_COMPONENTS)) {
                this.visitor.visitSuggestions(SUGGEST_NOTHING);
                this.readComponents();
            }
        }

        private void readComponents() throws CommandSyntaxException {
            if (ComponentParser.this.requireListStartEnd) {
                this.reader.expect(SYNTAX_START_COMPONENTS);
            } else if (this.reader.peek() == SYNTAX_START_COMPONENTS) {
                this.reader.skip();
            }
            this.visitor.visitSuggestions(this::suggestComponentAssignmentOrRemoval);
            Set<T> components = new ReferenceArraySet<>();

            while (this.reader.canRead() && this.reader.peek() != SYNTAX_END_COMPONENTS) {
                this.reader.skipWhitespace();
                if (this.reader.canRead() && (ComponentParser.this.allowComponentRemoval && this.reader.peek() == SYNTAX_REMOVED_COMPONENT)) {
                    this.reader.skip();
                    this.visitor.visitSuggestions(this::suggestComponent);
                    T type = readType(this.reader);
                    if (!components.add(type)) {
                        throw ERROR_REPEATED_COMPONENT.create(type);
                    }

                    this.visitor.visitRemovedValue(type);
                    this.visitor.visitSuggestions(SUGGEST_NOTHING);
                    this.reader.skipWhitespace();
                } else {
                    T type = readType(this.reader);
                    if (!components.add(type)) {
                        throw ERROR_REPEATED_COMPONENT.create(type);
                    }

                    this.visitor.visitSuggestions(this::suggestAssignment);
                    this.reader.skipWhitespace();
                    this.reader.expect(SYNTAX_COMPONENT_ASSIGNMENT);
                    this.visitor.visitSuggestions(SUGGEST_NOTHING);
                    this.reader.skipWhitespace();
                    this.readComponent(type);
                    this.reader.skipWhitespace();
                }

                this.visitor.visitSuggestions(this::suggestNextOrEndComponents);
                if (!this.reader.canRead() || this.reader.peek() != SYNTAX_COMPONENT_SEPARATOR) {
                    break;
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.visitor.visitSuggestions(this::suggestComponentAssignmentOrRemoval);
                if (!this.reader.canRead()) {
                    throw ERROR_EXPECTED_COMPONENT.createWithContext(this.reader);
                }
            }

            if (ComponentParser.this.requireListStartEnd) {
                this.reader.expect(SYNTAX_END_COMPONENTS);
            } else if (this.reader.canRead() && this.reader.peek() == SYNTAX_END_COMPONENTS) {
                this.reader.skip();
            }
            this.visitor.visitSuggestions(SUGGEST_NOTHING);
        }

        public T readType(StringReader reader) throws CommandSyntaxException {
            if (!reader.canRead()) {
                throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
            } else {
                int originalCursor = reader.getCursor();
                ResourceLocation id = ResourceLocation.read(reader);
                T type = ComponentParser.this.keyProvider.get(reader);
                if (type != null && ComponentParser.this.codecProvider.hasCodec(type)) {
                    return type;
                } else {
                    reader.setCursor(originalCursor);
                    throw ERROR_UNKNOWN_COMPONENT.createWithContext(reader, id);
                }
            }
        }

        private void readComponent(T type) throws CommandSyntaxException {
            int originalCursor = this.reader.getCursor();
            Tag tag = new TagParser(this.reader).readValue();
            DataResult<? extends V> result = ComponentParser.this.codecProvider.getCodec(type)
                    .parse(ComponentParser.this.nbtOps, tag);

            this.visitor.visitValue(type, result.getOrThrow(error -> {
                this.reader.setCursor(originalCursor);
                return ERROR_MALFORMED_COMPONENT.createWithContext(this.reader, type.toString(), error);
            }));
        }

        private CompletableFuture<Suggestions> suggestStartComponents(SuggestionsBuilder builder) {
            if (ComponentParser.this.requireListStartEnd && builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf(SYNTAX_START_COMPONENTS));
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestNextOrEndComponents(SuggestionsBuilder builder) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf(SYNTAX_COMPONENT_SEPARATOR));
                if (ComponentParser.this.requireListStartEnd) {
                    builder.suggest(String.valueOf(SYNTAX_END_COMPONENTS));
                }
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestAssignment(SuggestionsBuilder builder) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf('='));
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestComponentAssignmentOrRemoval(SuggestionsBuilder builder) {
            if (ComponentParser.this.allowComponentRemoval) {
                builder.suggest(String.valueOf(SYNTAX_REMOVED_COMPONENT));
            }
            return this.suggestComponent(builder, String.valueOf(SYNTAX_COMPONENT_ASSIGNMENT));
        }

        private CompletableFuture<Suggestions> suggestComponent(SuggestionsBuilder builder) {
            return this.suggestComponent(builder, "");
        }

        private CompletableFuture<Suggestions> suggestComponent(SuggestionsBuilder builder, String suffix) {
            String input = builder.getRemaining().toLowerCase(Locale.ROOT);
            SharedSuggestionProvider.filterResources(
                    BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet(), input, entry -> entry.getKey().location(), entry -> {
                        DataComponentType<?> componentType = entry.getValue();
                        if (componentType.codec() != null) {
                            ResourceLocation location = entry.getKey().location();
                            builder.suggest(location + suffix);
                        }
                    }
            );
            return builder.buildFuture();
        }
    }

    protected static class SuggestionsVisitor<K, V> implements Visitor<K, V> {
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

        @Override
        public void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {
            this.suggestions = suggestions;
        }

        public CompletableFuture<Suggestions> resolveSuggestions(SuggestionsBuilder builder, StringReader reader) {
            return this.suggestions.apply(builder.createOffset(reader.getCursor()));
        }
    }

    public interface Visitor<K, V> {

        default void visitValue(K type, V value) {}

        default void visitRemovedValue(K type) {}

        default void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {}
    }

    @FunctionalInterface
    public interface KeyProvider<T> {

        T get(StringReader reader) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public interface CodecProvider<T, V> {

        Codec<? extends V> getCodec(T type);

        default boolean hasCodec(T type) {
            return getCodec(type) != null;
        }
    }

    @FunctionalInterface
    public interface Setter<B, T, V> {

        void set(B builder, T type, V value);
    }

    @FunctionalInterface
    public interface Remover<B, T> {

        void remove(B builder, T type);
    }

    public static class DataComponents<T> extends ComponentParser<DataComponentType<? extends T>, Object, DataComponentPatch.Builder, DataComponentPatch> {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public DataComponents(DynamicOps<Tag> nbtOps, Registry<DataComponentType<? extends T>> componentTypeRegistry,
                              boolean requireListStartEnd, boolean allowComponentRemoval) {
            super(nbtOps, DataComponentPatch::builder, DataComponentPatch.Builder::build,
                    key -> componentTypeRegistry.get(ResourceLocation.read(key)), DataComponentType::codecOrThrow,
                    (builder, type, value) -> builder.set((DataComponentType) type, value), DataComponentPatch.Builder::remove,
                    requireListStartEnd, allowComponentRemoval);
        }
    }
}
