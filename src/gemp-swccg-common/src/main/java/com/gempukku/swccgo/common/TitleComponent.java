package com.gempukku.swccgo.common;

import java.util.Objects;

/** A printed title component: punctuation and restriction symbols are part of its identity. */
public record TitleComponent(String name, Uniqueness uniqueness) {
    public TitleComponent {
        Objects.requireNonNull(name, "Component name");
        if (name.isBlank()) throw new IllegalArgumentException("Component name must not be blank");
        if (uniqueness == null) uniqueness = Uniqueness.UNRESTRICTED;
    }

    @Override public boolean equals(Object other) {
        return other instanceof TitleComponent component
                && name.equalsIgnoreCase(component.name) && uniqueness == component.uniqueness;
    }

    @Override public int hashCode() {
        int nameHash = name.codePoints().map(c -> Character.toLowerCase(Character.toUpperCase(c)))
                .reduce(0, (hash, c) -> 31 * hash + c);
        return 31 * nameHash + uniqueness.hashCode();
    }
}
