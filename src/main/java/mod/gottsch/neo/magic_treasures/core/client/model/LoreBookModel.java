/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.magic_treasures.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.item.LoreBook;
import mod.gottsch.neo.magic_treasures.core.lore.LoreBookDef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The lore book's item model ({@code "loader": "magictreasures:lore_book"}): each book shows its own icon, picked from
 * the stack's book id, first match wins:
 * <ol>
 *   <li>the book's {@code texture} field</li>
 *   <li>{@code <namespace>:item/lore_book/<path>}, named after the book</li>
 *   <li>the shared default, {@code magictreasures:item/lore_book/_default}</li>
 * </ol>
 * Built like {@link JewelryModel}: baked on first use, cached per texture. Textures under
 * {@code textures/item/lore_book/} of any namespace are stitched by the directory source in
 * {@code assets/minecraft/atlases/blocks.json}, so a pack's book needs no model file.
 */
public class LoreBookModel implements IUnbakedGeometry<LoreBookModel> {
    public static final ResourceLocation LOADER_ID = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "lore_book");
    static final ResourceLocation DEFAULT = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "item/lore_book/_default");

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides) {
        JewelryModel.Baker bookBaker = new JewelryModel.Baker("lore_book", context.useAmbientOcclusion(), context.isGui3d(),
                context.useBlockLight(), context.getTransforms(), modelState);
        // a stack with no book id (a bare /give) shows the default
        TextureAtlasSprite fallback = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, DEFAULT));
        return bookBaker.bake(List.of(new JewelryModel.Layer(fallback, 0)), new Overrides(bookBaker));
    }

    /** picks the stack's model from its book; one cache per baked model, so a resource reload clears it */
    static final class Overrides extends ItemOverrides {
        private final JewelryModel.Baker baker;
        private final Map<ResourceLocation, BakedModel> cache = new ConcurrentHashMap<>();

        Overrides(JewelryModel.Baker baker) {
            this.baker = baker;
        }

        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            Optional<ResourceLocation> id = LoreBook.getId(stack);
            ClientLevel world = level != null ? level : Minecraft.getInstance().level;
            if (id.isEmpty() || world == null) {
                return model;
            }
            TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            Function<ResourceLocation, TextureAtlasSprite> find = location -> {
                TextureAtlasSprite sprite = atlas.getSprite(location);
                return MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name()) ? null : sprite;
            };
            // 1. its texture field, 2. art named after the book; else the default (the model as baked)
            ResourceLocation named = ResourceLocation.fromNamespaceAndPath(id.get().getNamespace(), "item/lore_book/" + id.get().getPath());
            Optional<ResourceLocation> texture = LoreBook.getDef(stack, world.registryAccess()).flatMap(LoreBookDef::texture)
                    .filter(location -> find.apply(location) != null)
                    .or(() -> find.apply(named) != null ? Optional.of(named) : Optional.empty());
            if (texture.isEmpty()) {
                return model;
            }
            return cache.computeIfAbsent(texture.get(),
                    location -> baker.bake(List.of(new JewelryModel.Layer(find.apply(location), 0)), ItemOverrides.EMPTY));
        }
    }

    public static final class Loader implements IGeometryLoader<LoreBookModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public LoreBookModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
            return new LoreBookModel();
        }
    }
}
