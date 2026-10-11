package mod.gottsch.neo.magic_treasures.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 *   <li>full-combination art: {@code <material ns>:item/jewelry/[size_]material_[gem_]type}, which keeps every
 *       hand-drawn 1.x texture</li>
 *   <li>layers: base {@code <material ns>:item/jewelry/<type>/<material>[_size]} and gem
 *       {@code <gem ns or magictreasures>:item/jewelry/<type>/gem/<gem>}</li>
 *   <li>fallback, per layer: MT's {@code <type>/_base} and {@code <type>/gem/_overlay}, tinted with the
 *       material's and gem's colors (see {@link JewelryColors})</li>
 * </ol>
 * Textures under {@code textures/item/jewelry/} of any namespace are stitched by the directory atlas source in
 * {@code assets/minecraft/atlases/blocks.json}, so a resource pack needs no model files.
 */
public class JewelryModel implements IUnbakedGeometry<JewelryModel> {
    public static final ResourceLocation LOADER_ID = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "jewelry");

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
                           ModelState modelState, ItemOverrides overrides) {
        Baker jewelryBaker = new Baker(type, context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
                context.getTransforms(), modelState);
        // a stack without an identity (e.g. /give with no components) shows the untinted base
        TextureAtlasSprite base = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, Baker.fallbackBase(type)));
        return jewelryBaker.bake(List.of(new Layer(base, TINT_MATERIAL)), new Overrides(jewelryBaker));
    }

    /** one sprite and the tint index its quads carry */
    record Layer(TextureAtlasSprite sprite, int tintIndex) {}

    /** bakes layer lists into item models with this model file's display settings */
    static final class Baker {
        private static final RenderTypeGroup RENDER_TYPES = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
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
                        UnbakedGeometryHelper.createUnbakedItemElements(layer.tintIndex(), layer.sprite()), $ -> layer.sprite(), modelState);
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
            String size = identity.size();
            boolean regular = JewelryIdentity.REGULAR.equals(size);

            // 1. preset art
            if (identity.preset().isPresent()) {
                TextureAtlasSprite sprite = find.apply(jewelryTexture(identity.preset().get().getNamespace(), identity.preset().get().getPath()));
                if (sprite != null) {
                    return List.of(new Layer(sprite, LAYER_BASE));
                }
            }

            // 2. full-combination art, as named by the 1.x JewelryBuilder
            String combo = (regular ? "" : size + "_") + material.getPath() + "_"
                    + identity.gem().map(gem -> gem.getPath() + "_").orElse("") + type;
            TextureAtlasSprite full = find.apply(jewelryTexture(material.getNamespace(), combo));
            if (full != null) {
                return List.of(new Layer(full, LAYER_BASE));
            }

            // 3./4. layers, each falling back to MT's tinted art on its own
            List<Layer> layers = new ArrayList<>(2);
            TextureAtlasSprite base = regular ? null : find.apply(jewelryTexture(material.getNamespace(), type + "/" + material.getPath() + "_" + size));
            if (base == null) {
                base = find.apply(jewelryTexture(material.getNamespace(), type + "/" + material.getPath()));
            }
            layers.add(base != null ? new Layer(base, LAYER_BASE) : new Layer(find.apply(fallbackBase(type)), TINT_MATERIAL));

            identity.gem().ifPresent(gem -> {
                TextureAtlasSprite gemSprite = find.apply(jewelryTexture(gem.getNamespace(), type + "/gem/" + gem.getPath()));
                if (gemSprite == null) {
                    // MT can ship gem layers for other namespaces' gems (vanilla, Treasure2)
                    gemSprite = find.apply(jewelryTexture(MagicTreasures.MOD_ID, type + "/gem/" + gem.getPath()));
                }
                layers.add(gemSprite != null ? new Layer(gemSprite, LAYER_GEM)
                        : new Layer(find.apply(jewelryTexture(MagicTreasures.MOD_ID, type + "/gem/_overlay")), TINT_GEM));
            });
            return layers;
        }

        static ResourceLocation fallbackBase(String type) {
            return jewelryTexture(MagicTreasures.MOD_ID, type + "/_base");
        }

        private static ResourceLocation jewelryTexture(String namespace, String path) {
            return ResourceLocation.fromNamespaceAndPath(namespace, "item/jewelry/" + path);
        }
    }

    /** picks the stack's model from its identity; one cache per baked model, so a resource reload clears it */
    static final class Overrides extends ItemOverrides {
        private final Baker baker;
        private final Map<JewelryIdentity, BakedModel> cache = new ConcurrentHashMap<>();

        Overrides(Baker baker) {
            this.baker = baker;
        }

        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
            if (identity == null) {
                return model;
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
