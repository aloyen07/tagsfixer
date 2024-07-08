package ru.aloyenz.tagsfixer;

import net.minecraft.tags.ITag;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class EmptyTag <T> implements ITag<T> {

    @Override
    public boolean contains(@Nonnull T o) {
        return false;
    }

    @Override
    public @Nonnull List<T> getValues() {
        return Collections.emptyList();
    }
}
