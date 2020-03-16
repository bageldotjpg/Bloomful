package com.pugz.bloomful.common.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.pugz.bloomful.core.util.BiomeFeatures;
import com.pugz.bloomful.core.util.WisteriaColor;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.pugz.bloomful.core.util.WisteriaTreeUtils.*;

public class WisteriaTreeFeature extends AbstractTreeFeature<TreeFeatureConfig> {
	private Supplier<BlockState>  VINE_UPPER;
	private Supplier<BlockState>  VINE_LOWER;
	
	   public WisteriaTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> config) {
		      super(config);
		   }

    @Override
    public boolean func_225557_a_(IWorldGenerationReader world, Random random, BlockPos pos, Set<BlockPos> changedBlocks, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBox, TreeFeatureConfig config) {
    	if (config.leavesProvider.getBlockState(random, pos) == BiomeFeatures.BLUE_WISTERIA_LEAVES) { 
    		VINE_UPPER = () -> BiomeFeatures.BLUE_HANGING_WISTERIA_LEAVES_TOP;
    		VINE_LOWER = () -> BiomeFeatures.BLUE_HANGING_WISTERIA_LEAVES_BOTTOM;
    	}
    	if (config.leavesProvider.getBlockState(random, pos) == BiomeFeatures.PINK_WISTERIA_LEAVES) { 
    		VINE_UPPER = () -> BiomeFeatures.PINK_HANGING_WISTERIA_LEAVES_TOP;
    		VINE_LOWER = () -> BiomeFeatures.PINK_HANGING_WISTERIA_LEAVES_BOTTOM;
    	}
    	if (config.leavesProvider.getBlockState(random, pos) == BiomeFeatures.PURPLE_WISTERIA_LEAVES) { 
    		VINE_UPPER = () -> BiomeFeatures.PURPLE_HANGING_WISTERIA_LEAVES_TOP;
    		VINE_LOWER = () -> BiomeFeatures.PURPLE_HANGING_WISTERIA_LEAVES_BOTTOM;
    	}
    	if (config.leavesProvider.getBlockState(random, pos) == BiomeFeatures.WHITE_WISTERIA_LEAVES) { 
    		VINE_UPPER = () -> BiomeFeatures.WHITE_HANGING_WISTERIA_LEAVES_TOP;
    		VINE_LOWER = () -> BiomeFeatures.WHITE_HANGING_WISTERIA_LEAVES_BOTTOM;
    	}
    	
        int height = random.nextInt(7) + 5;
        boolean flag = true;
        if (pos.getY() >= 1 && pos.getY() + height + 1 <= world.getMaxHeight()) {
            for(int j = pos.getY(); j <= pos.getY() + 1 + height; ++j) {
                int k = 1;
                if (j == pos.getY()) {
                    k = 0;
                }
                if (j >= pos.getY() + 1 + height - 2) {
                    k = 2;
                }
                BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
                for(int l = pos.getX() - k; l <= pos.getX() + k && flag; ++l) {
                    for(int i1 = pos.getZ() - k; i1 <= pos.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < world.getMaxHeight()) {
                            if (!func_214587_a(world, blockpos$mutableblockpos.setPos(l, j, i1))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }
            if (!flag) {
                return false;
            } else if (isSoil(world, pos.down(),config.getSapling()) && pos.getY() < world.getMaxHeight() - height - 1) {
                setDirtAt(world, pos.down(), pos);
                for (int y = 4; y > -4; --y) {
                    for (int x = 4; x > -4; --x) {
                        for (int z = 4; z > -4; --z) {
                            if (Math.sqrt((x * x) + (y > 0 ? (y * y) : 0) + (z * z)) <= 4) {
                                BlockPos leafPos = pos.add(x, y + height, z);
                                boolean place = true;
                                if (y < 0) {
                                    place = world.hasBlockState(leafPos.add(0, 1, 0), (state) -> {
                                        return state.isIn(BlockTags.LEAVES);
                                    });
                                    if (place && random.nextInt(Math.abs(y) + 1) != 0) {
                                        place = false;
                                        if (random.nextInt(4) == 0 && !isLog(world, leafPos)) {
                                            placeVines(changedBlocks, world, random, leafPos, config.leavesProvider.getBlockState(random, pos), VINE_LOWER.get(), VINE_UPPER.get(), boundingBox, config);
                                        }
                                    }
                                }
                                if (place) {
                                    placeLeafAt(changedBlocks, world, leafPos, boundingBox, config, random);
                                }
                            }
                        }
                    }
                }
                for(int i2 = 0; i2 < height; ++i2) {
                    if (isAirOrLeavesOrVines(world, pos.up(i2))) {
                        setLogState(changedBlocks, world, pos.up(i2), config.trunkProvider.getBlockState(random, pos), boundingBox);
                    }
                }
                placeBranch(changedBlocks, world, random, pos.down(), pos.up(height).getY(), boundingBox, config);
                if (random.nextInt(4) == 0) placeBranch(changedBlocks, world, random, pos.down(), pos.up(height).getY(), boundingBox, config);
                
                BlockPos startPos = pos.up(height);

                for(BlockPos blockpos : BlockPos.getAllInBoxMutable(startPos.getX() - 10, startPos.getY() - 10, startPos.getZ() - 10, startPos.getX() + 10, startPos.getY() + 10, startPos.getZ() + 10)) {
                    if(isAir(world, blockpos) && isLeaves(world, blockpos.up(), config, random) && random.nextInt(4) == 0) {
                    	if (isAir(world, blockpos)) setForcedState(changedBlocks, world, blockpos, VINE_UPPER.get(), boundingBox);
                        if (isAir(world, blockpos.down()) && random.nextInt(2) == 0) setForcedState(changedBlocks, world, blockpos.down(), VINE_LOWER.get(), boundingBox);
                    }
                }
                
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void placeBranch(Set<BlockPos> changedBlocks, IWorldGenerationReader world, Random random, BlockPos pos, int treeHeight, MutableBoundingBox boundingBox, TreeFeatureConfig config) {
        int heightOffset = random.nextInt(3);
        BlockPos[] startPositions = new BlockPos[] {
                new BlockPos(pos.getX() - 1, treeHeight - heightOffset, pos.getZ()),
                new BlockPos(pos.getX() + 1, treeHeight - heightOffset, pos.getZ()),
                new BlockPos(pos.getX(), treeHeight - heightOffset, pos.getZ() - 1),
                new BlockPos(pos.getX(), treeHeight - heightOffset, pos.getZ() + 1),
                new BlockPos(pos.getX() - 1, treeHeight - heightOffset, pos.getZ() - 1),
                new BlockPos(pos.getX() + 1, treeHeight - heightOffset, pos.getZ() - 1),
                new BlockPos(pos.getX() - 1, treeHeight - heightOffset, pos.getZ() + 1),
                new BlockPos(pos.getX() + 1, treeHeight - heightOffset, pos.getZ() + 1)
        };
        BlockPos startPos = startPositions[random.nextInt(8)];
        if (isAirOrLeavesOrVines(world, startPos)) {
            boolean vines = random.nextInt(6) != 5;
            BlockPos placePos = startPos;
            for (int y = (treeHeight - heightOffset); y <= treeHeight; ++y) {
                placePos = new BlockPos(startPos.getX(), y, startPos.getZ());
                if (isAirOrLeavesOrVines(world, placePos)) setLogState(changedBlocks, world, placePos, config.trunkProvider.getBlockState(random, pos), boundingBox);
            }
            placeLeafAt(changedBlocks, world, placePos.up(), boundingBox, config, random);
            if (vines) placeVines(changedBlocks, world, random, startPos.down(), config.leavesProvider.getBlockState(random, pos), VINE_LOWER.get(), VINE_UPPER.get(), boundingBox, config);
        }
    }

    private void placeVines(Set<BlockPos> changedBlocks, IWorldGenerationReader world, Random random, BlockPos pos, BlockState leaf, BlockState vineLower, BlockState vineUpper, MutableBoundingBox boundingBox, TreeFeatureConfig config) {
        int length = getLengthByNeighbors(world, random, pos);
        if (random.nextInt(6) != 5 && isAir(world, pos) && !isLog(world, pos)) {
            switch (length) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    if (isAir(world, pos)) placeLeafAt(changedBlocks, world, pos, boundingBox, config, random);
                    break;
                case 4:
                    if (isAir(world, pos)) placeLeafAt(changedBlocks, world, pos, boundingBox, config, random);
                    if (isAir(world, pos.down())) placeLeafAt(changedBlocks, world, pos.down(), boundingBox, config, random);
                    break;
                case 5:
                    if (isAir(world, pos)) placeLeafAt(changedBlocks, world, pos, boundingBox, config, random);
                    if (isAir(world, pos.down())) placeLeafAt(changedBlocks, world, pos.down(), boundingBox, config, random);
                    if (isAir(world, pos.down(2))) placeLeafAt(changedBlocks, world, pos.down(2), boundingBox, config, random);
                    break;
            }
        }
    }

    private void placeLeafAt(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, BlockPos pos, MutableBoundingBox boundingBox, TreeFeatureConfig config, Random random) {
        if (isAirOrLeavesOrVines(worldIn, pos)) {
            setLogState(changedBlocks, worldIn, pos, config.leavesProvider.getBlockState(random, pos), boundingBox);
        }
    }
    
    protected final void setLogState(Set<BlockPos> changedBlocks, IWorldWriter worldIn, BlockPos pos, BlockState p_208520_4_, MutableBoundingBox p_208520_5_) {
	      this.func_208521_b(worldIn, pos, p_208520_4_);
	      p_208520_5_.expandTo(new MutableBoundingBox(pos, pos));
	      if (BlockTags.LOGS.contains(p_208520_4_.getBlock())) {
	         changedBlocks.add(pos.toImmutable());
	      }
	   }
    
    private void func_208521_b(IWorldWriter p_208521_1_, BlockPos p_208521_2_, BlockState p_208521_3_) {
		p_208521_1_.setBlockState(p_208521_2_, p_208521_3_, 18);
	   }

    private void setForcedState(Set<BlockPos> changedBlocks, IWorldWriter worldIn, BlockPos pos, BlockState state, MutableBoundingBox boundingBox) {
    	worldIn.setBlockState(pos, state, 18);
        boundingBox.expandTo(new MutableBoundingBox(pos, pos));
        changedBlocks.add(pos.toImmutable());
    }

    public static boolean isLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos, TreeFeatureConfig config, Random random) {
        if (worldIn instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
           return worldIn.hasBlockState(pos, state -> state == config.leavesProvider.getBlockState(random, pos));
        return worldIn.hasBlockState(pos, (p_227223_0_) -> { return config.leavesProvider.getBlockState(random, pos) == p_227223_0_;
        });
     }
    
    public static void addFeature() {
        ForgeRegistries.BIOMES.getValues().forEach(WisteriaTreeFeature::generate);
    }

    public static void generate(Biome biome) {
        if (biome.getCategory() == Biome.Category.JUNGLE) {
            BiomeFeatures.addWisteriaTree(biome, WisteriaColor.PINK, 0, 0.25F);
        }
        else if (biome.getCategory() == Biome.Category.SWAMP) {
            BiomeFeatures.addWisteriaTree(biome, WisteriaColor.BLUE, 0, 0.025F);
        }
        else if (biome.getCategory() == Biome.Category.PLAINS) {
            BiomeFeatures.addWisteriaTrees(biome,0, 0.01F);
        }
        else if (biome.getCategory() == Biome.Category.FOREST) {
            if (biome == Biomes.FLOWER_FOREST || biome == Biomes.DARK_FOREST || biome == Biomes.DARK_FOREST_HILLS) BiomeFeatures.addWisteriaTrees(biome,0, 0.1F);
            else BiomeFeatures.addWisteriaTrees(biome,0, 0.025F);
        }
    }
}