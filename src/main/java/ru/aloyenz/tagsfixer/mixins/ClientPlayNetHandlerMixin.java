package ru.aloyenz.tagsfixer.mixins;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aloyenz.tagsfixer.FixedTagCollectionSupplier;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {

    @Shadow @Final private NetworkManager connection;

    @Shadow private ITagCollectionSupplier tags;
    @Shadow private Minecraft minecraft;


    @Unique
    private STagsListPacket tagsFixer$tagsListPacket;

    @Inject(method = "handleUpdateTags", at = @At("HEAD"))
    public void getTagListInstance(STagsListPacket tagList, CallbackInfo ci) {
        this.tagsFixer$tagsListPacket = tagList;
    }

    @WrapWithCondition(method = "handleUpdateTags",
            at = @At(value = "INVOKE",
                    target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V")
    )
    public boolean sendCustomWarn(Logger logger, String oldString, Object missingTagList) {
        logger.warn("Incomplete server tags, disconnecting. Missing: {}. However, we don't give a damn about it.",
                missingTagList);

        return false;
    }

    @WrapWithCondition(method = "handleUpdateTags",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkManager;disconnect(Lnet/minecraft/util/text/ITextComponent;)V")
    )
    public boolean fixDisconnection(NetworkManager instance, ITextComponent p_150718_1_) {
        // Before check tags
        boolean isVanillaConnection = NetworkHooks.isVanillaConnection(this.connection);

        ITagCollectionSupplier tagCollectionSupplier = tagsFixer$tagsListPacket.getTags();

        Multimap<ResourceLocation, ResourceLocation> unfoundedTags = isVanillaConnection
                ? TagRegistryManager.getAllMissingTags(ForgeTagHandler.withNoCustom(tagCollectionSupplier))
                : TagRegistryManager.validateVanillaTags(tagCollectionSupplier);

        // After check tags
        // Fixing unfounded tags
        tagCollectionSupplier = new FixedTagCollectionSupplier(tagCollectionSupplier, unfoundedTags);

        ForgeTagHandler.resetCachedTagCollections(true, isVanillaConnection);
        tagCollectionSupplier =
                ITagCollectionSupplier.reinjectOptionalTags(tagCollectionSupplier);

        this.tags = tagCollectionSupplier;

        if (!this.connection.isMemoryConnection()) {
            tagCollectionSupplier.bindToGlobal();
        }

        this.minecraft.getSearchTree(SearchTreeManager.CREATIVE_TAGS).refresh();

        return false;
    }
}
