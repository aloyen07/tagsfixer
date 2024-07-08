package ru.aloyenz.tagsfixer;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class FixedTagCollectionSupplier implements ITagCollectionSupplier {

    private final ITagCollectionSupplier oldTags;
    private final Multimap<ResourceLocation, ResourceLocation> unfoundedTags;

    public FixedTagCollectionSupplier(@Nonnull ITagCollectionSupplier oldTags,
                                      @Nonnull Multimap<ResourceLocation, ResourceLocation> unfoundedTags) {
        this.oldTags = oldTags;
        this.unfoundedTags = unfoundedTags;
    }

    @Override
    public @Nonnull ITagCollection<Block> getBlocks() {
        return getModifiedTags(oldTags.getBlocks(), new ResourceLocation("minecraft", "block"));
    }

    @Override
    public @Nonnull ITagCollection<Item> getItems() {
        return getModifiedTags(oldTags.getItems(), new ResourceLocation("minecraft", "item"));
    }

    @Override
    public @Nonnull ITagCollection<Fluid> getFluids() {
        return getModifiedTags(oldTags.getFluids(), new ResourceLocation("minecraft", "fluid"));
    }

    @Override
    public @Nonnull ITagCollection<EntityType<?>> getEntityTypes() {
        return getModifiedTags(oldTags.getEntityTypes(), new ResourceLocation("minecraft", "entity"));
    }

    private @Nonnull <T> ITagCollection<T> getModifiedTags(@Nonnull ITagCollection<T> collection,
                                                           @Nonnull ResourceLocation resourceLocationKey) {
        Map<ResourceLocation, ITag<T>> tagMap = new HashMap<>(collection.getAllTags());

        for (ResourceLocation toAdd : unfoundedTags.get(resourceLocationKey)) {
            tagMap.put(toAdd, new EmptyTag<>());
        }

        return ITagCollection.of(tagMap);
    }
}
