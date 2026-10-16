package mod.gottsch.forge.magic_treasures.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryStyleDef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The composed item model of generic jewelry (2.0). One model file per jewelry type
 * ({@code "loader": "magictreasures:jewelry", "jewelry_type": "ring"}); the model for a stack is picked from its
 * {@link JewelryIdentity} and baked on first use, then cached until the next resource reload.
 * <p>
 * Sprite lookup, first match wins:
 * <ol>
 *   <li>preset art: {@code <preset ns>:item/jewelry/<preset path>}</li>
 *   <li>full-combination art: {@code <material ns>:item/jewelry/[affix_]material_[gem_]type}, which keeps every
 *       hand-drawn 1.x texture</li>
 *   <li>layers: base {@code <material ns>:item/jewelry/<type>/<material>[_affix]} and gem
 *       {@code <gem ns or magictreasures>:item/jewelry/<type>/gem/<gem>[_affix]}</li>
 *   <li>fallback, per layer: {@code <type>/_base[_affix]} and {@code <type>/gem/_overlay[_affix]} (the style's
 *       namespace, then MT's), tinted with the material's and gem's colors (see {@link JewelryColors})</li>
 * </ol>
 * The affix comes from the jewelry_style ({@code great}; none for regular). Layer and fallback lookups walk the
 * style's {@code texture_fallback} chain (great, then regular), real art before tinted art. A style with its own
 * shape has no fallback, so its gem never lands on another style's band. The last resort is MT's plain
 * {@code _base} and {@code _overlay}.
 * Textures under {@code textures/item/jewelry/} of any namespace are stitched by the directory atlas source in
 * {@code assets/minecraft/atlases/blocks.json}, so a resource pack needs no model files.
 */
public class JewelryModel implements IUnbakedGeometry<JewelryModel> {
    public static final ResourceLocation LOADER_ID = new ResourceLocation(MagicTreasures.MOD_ID, "jewelry");

    /** tint indexes: real art is never tinted; the fallback layers are */
    public static final int TINT_MATERIAL = 2;
    public static final int TINT_GEM = 3;
    private static final int LAYER_BASE = 0;
    private static final int LAYER_GEM = 1;

    private final String type;

    public JewelryModel(String type) {
        this.type = type;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        Baker jewelryBaker = new Baker(type, context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
                context.getTransforms(), modelState);
        // a stack without an identity (e.g. /give with no components) shows the untinted base
        TextureAtlasSprite base = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, Baker.fallbackBase(type)));
        return jewelryBaker.bake(List.of(new Layer(base, TINT_MATERIAL)), new Overrides(jewelryBaker));
    }

    /** one sprite and the tint index its quads carry */
    record Layer(TextureAtlasSprite sprite, int tintIndex) {}

    /** where a style's art lives: the namespace of its fallback art, and the affix of its texture names */
    record StyleArt(String namespace, String affix) {
        String suffix() {
            return affix.isEmpty() ? "" : "_" + affix;
        }
    }

    private static final int MAX_STYLE_CHAIN = 4;

    /** bakes layer lists into item models with this model file's display settings */
    static final class Baker {
        private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        private final String type;
        private final boolean ambientOcclusion, gui3d, blockLight;
        private final ItemTransforms transforms;
        private final ModelState modelState;

        Baker(String type, boolean ambientOcclusion, boolean gui3d, boolean blockLight, ItemTransforms transforms, ModelState modelState) {
            this.type = type;
            this.ambientOcclusion = ambientOcclusion;
            this.gui3d = gui3d;
            this.blockLight = blockLight;
            this.transforms = transforms;
            this.modelState = modelState;
        }

        BakedModel bake(List<Layer> layers, ItemOverrides overrides) {
            CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(ambientOcclusion, gui3d, blockLight,
                    layers.get(0).sprite(), overrides, transforms);
            for (Layer layer : layers) {
                // the element generator uses its layer index as the quads' tint index
                List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(
                        UnbakedGeometryHelper.createUnbakedItemElements(layer.tintIndex(), layer.sprite().contents()), $ -> layer.sprite(),
                        modelState, LOADER_ID);
                builder.addQuads(RENDER_TYPES, quads);
            }
            return builder.build();
        }

        BakedModel bake(JewelryIdentity identity) {
            TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            Function<ResourceLocation, TextureAtlasSprite> find = location -> {
                TextureAtlasSprite sprite = atlas.getSprite(location);
                return MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name()) ? null : sprite;
            };
            return bake(layers(identity, find), ItemOverrides.EMPTY);
        }

        List<Layer> layers(JewelryIdentity identity, Function<ResourceLocation, TextureAtlasSprite> find) {
            ResourceLocation material = identity.material();
            List<StyleArt> chain = styleChain(identity.style());

            // 1. preset art: its texture, else <ns>:item/jewelry/<path>. A preset with a base_name (castle_ring)
            // has art per gem, which rule 2 finds; its path alone would show the gemless art.
            if (identity.preset().isPresent()) {
                ResourceLocation preset = identity.preset().get();
                Optional<JewelryPresetDef> def = JewelryDataIndex.preset(preset).map(Holder::value);
                Optional<ResourceLocation> texture = def.flatMap(JewelryPresetDef::texture).or(() ->
                        def.flatMap(JewelryPresetDef::baseName).isPresent() ? Optional.empty()
                                : Optional.of(jewelryTexture(preset.getNamespace(), preset.getPath())));
                TextureAtlasSprite sprite = texture.map(find).orElse(null);
                if (sprite != null) {
                    return List.of(new Layer(sprite, LAYER_BASE));
                }
            }

            // 2. full-combination art, named as the 1.x combination items were
            String affix = chain.get(0).affix();
            String combo = (affix.isEmpty() ? "" : affix + "_") + material.getPath() + "_"
                    + identity.gem().map(gem -> gem.getPath() + "_").orElse("") + GenericJewelry.baseName(identity);
            TextureAtlasSprite full = find.apply(jewelryTexture(material.getNamespace(), combo));
            if (full != null) {
                return List.of(new Layer(full, LAYER_BASE));
            }

            // 3./4. layers, each falling back to tinted art on its own
            List<Layer> layers = new ArrayList<>(2);
            TextureAtlasSprite base = first(chain, art -> find.apply(jewelryTexture(material.getNamespace(),
                    type + "/" + material.getPath() + art.suffix())));
            layers.add(base != null ? new Layer(base, LAYER_BASE) : new Layer(fallback(chain, "/_base", find), TINT_MATERIAL));

            identity.gem().ifPresent(gem -> {
                TextureAtlasSprite gemSprite = first(chain, art -> {
                    String path = type + "/gem/" + gem.getPath() + art.suffix();
                    TextureAtlasSprite sprite = find.apply(jewelryTexture(gem.getNamespace(), path));
                    // MT can ship gem layers for other namespaces' gems (vanilla, Treasure2)
                    return sprite != null ? sprite : find.apply(jewelryTexture(MagicTreasures.MOD_ID, path));
                });
                layers.add(gemSprite != null ? new Layer(gemSprite, LAYER_GEM) : new Layer(fallback(chain, "/gem/_overlay", find), TINT_GEM));
            });
            return layers;
        }

        /** the tinted art of the first style in the chain that has it; MT's plain one otherwise */
        private TextureAtlasSprite fallback(List<StyleArt> chain, String name, Function<ResourceLocation, TextureAtlasSprite> find) {
            TextureAtlasSprite sprite = first(chain, art -> {
                String path = type + name + art.suffix();
                TextureAtlasSprite found = find.apply(jewelryTexture(art.namespace(), path));
                return found != null ? found : find.apply(jewelryTexture(MagicTreasures.MOD_ID, path));
            });
            return sprite != null ? sprite : find.apply(jewelryTexture(MagicTreasures.MOD_ID, type + name));
        }

        private static TextureAtlasSprite first(List<StyleArt> chain, Function<StyleArt, TextureAtlasSprite> lookup) {
            for (StyleArt art : chain) {
                TextureAtlasSprite sprite = lookup.apply(art);
                if (sprite != null) {
                    return sprite;
                }
            }
            return null;
        }

        /**
         * The style followed by its texture_fallback chain. An unknown style (missing pack, disabled) uses its id
         * path as the affix and falls back to regular art.
         */
        static List<StyleArt> styleChain(ResourceLocation style) {
            List<StyleArt> chain = new ArrayList<>(3);
            Set<ResourceLocation> seen = new HashSet<>();
            ResourceLocation next = style;
            while (next != null && seen.add(next) && chain.size() < MAX_STYLE_CHAIN) {
                Optional<Holder.Reference<JewelryStyleDef>> def = JewelryDataIndex.style(next);
                if (def.isPresent()) {
                    chain.add(new StyleArt(next.getNamespace(), def.get().value().affix(next)));
                    next = def.get().value().textureFallback().orElse(null);
                } else if (chain.isEmpty()) {
                    chain.add(new StyleArt(next.getNamespace(), JewelryRules.REGULAR.equals(next) ? "" : next.getPath()));
                    next = JewelryRules.REGULAR;
                } else {
                    next = null;
                }
            }
            return chain;
        }

        static ResourceLocation fallbackBase(String type) {
            return jewelryTexture(MagicTreasures.MOD_ID, type + "/_base");
        }

        private static ResourceLocation jewelryTexture(String namespace, String path) {
            return new ResourceLocation(namespace, "item/jewelry/" + path);
        }
    }

    /**
     * picks the stack's model from its identity; one cache per baked model, so a resource reload clears it. The
     * styles' art settings come from the datapack registries, so a change of data (another server) clears it too.
     */
    static final class Overrides extends ItemOverrides {
        private final Baker baker;
        private final Map<JewelryIdentity, BakedModel> cache = new ConcurrentHashMap<>();
        private volatile int generation = JewelryDataIndex.generation();

        Overrides(Baker baker) {
            this.baker = baker;
        }

        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            JewelryIdentity identity = StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY);
            if (identity == null) {
                return model;
            }
            if (generation != JewelryDataIndex.generation()) {
                cache.clear();
                generation = JewelryDataIndex.generation();
            }
            return cache.computeIfAbsent(identity, baker::bake);
        }
    }

    public static final class Loader implements IGeometryLoader<JewelryModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public JewelryModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
            if (!json.has("jewelry_type")) {
                throw new JsonParseException("a magictreasures:jewelry model needs \"jewelry_type\"");
            }
            return new JewelryModel(json.get("jewelry_type").getAsString());
        }
    }
}
