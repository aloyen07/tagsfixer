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

import java.util.Map;

public class FixedTagCollectionSupplier implements ITagCollectionSupplier {

    private final ITagCollectionSupplier oldTags;
    private final Multimap<ResourceLocation, ResourceLocation> unfoundedTags;

    public FixedTagCollectionSupplier(ITagCollectionSupplier oldTags,
                                      Multimap<ResourceLocation, ResourceLocation> unfoundedTags) {
        this.oldTags = oldTags;
        this.unfoundedTags = unfoundedTags;
    }

    @Override
    public ITagCollection<Block> getBlocks() {
        return getModifiedTags(oldTags.getBlocks(), new ResourceLocation("minecraft", "block"));
    }

    @Override
    public ITagCollection<Item> getItems() {
        return getModifiedTags(oldTags.getItems(), new ResourceLocation("minecraft", "item"));
    }

    @Override
    public ITagCollection<Fluid> getFluids() {
        return getModifiedTags(oldTags.getFluids(), new ResourceLocation("minecraft", "fluid"));
    }

    @Override
    public ITagCollection<EntityType<?>> getEntityTypes() {
        return getModifiedTags(oldTags.getEntityTypes(), new ResourceLocation("minecraft", "entity"));
    }

    private <T> ITagCollection<T> getModifiedTags(ITagCollection<T> collection,
                                                               ResourceLocation resourceLocationKey) {
        Map<ResourceLocation, ITag<T>> tagMap = collection.getAllTags();

        for (ResourceLocation toAdd : unfoundedTags.get(resourceLocationKey)) {
            tagMap.put(toAdd, null);
        }

        return ITagCollection.of(tagMap);
    }
}
