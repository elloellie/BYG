package corgiaoc.byg.common.world.feature.end.islands;

import com.mojang.serialization.Codec;
import corgiaoc.byg.common.world.feature.config.FloatingIslandConfig;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.noise.module.source.Perlin;

import java.util.Random;

public class FloatingIslands2 extends Feature<FloatingIslandConfig> {

    Perlin perlin = null;

    public FloatingIslands2(Codec<FloatingIslandConfig> codec) {
        super(codec);
    }

    @Override
    public boolean func_241855_a(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, FloatingIslandConfig config) {
        setSeed(world.getSeed());

        double radius = rand.nextInt(config.getMaxPossibleRadius()) + config.getMinRadius();
        double halfRadius = radius / 2;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()) > 4)
            return false;


        for (double x = -halfRadius - 2; x <= halfRadius + 8; x++) {
            for (double y = -radius - 2; y <= radius + 8; y++) {
                for (double z = -halfRadius - 2; z <= halfRadius + 8; z++) {
                    double squareNoise1 = perlin.getValue(x, y, z) * 12 - 6;
                    double distanceSqt1 = x * x + y * y + z * z + squareNoise1 * squareNoise1;
                    if (distanceSqt1 <= halfRadius * radius) {
                        mutable.setPos(pos).move((int) x, (int) y, (int) z);
                        if (world.getBlockState(mutable).getMaterial() == Material.AIR) {
                            if (y >= 1)
                                world.setBlockState(mutable, config.getBlockProvider().getBlockState(rand, mutable), 2);
                            if (y >= 2)
                                world.setBlockState(mutable, config.getTopBlockProvider().getBlockState(rand, mutable), 2);
                        }
                    }
                }
            }
        }

        for (double x = -radius - 2; x <= radius + 2; x++) {
            for (double y = -radius - 2; y <= radius + 11; y++) {
                for (double z = -radius - 2; z <= radius + 2; z++) {
                    double noise = perlin.getValue(x, y, z) * 12;
                    double scaledNoise = (noise / 11) * ((y * 3) / ((x * x) + (z * z)));
                    if (scaledNoise >= 0.5) {
                        if (y >= 1) {
                            world.setBlockState(pos.add(x, y - 22, z), config.getBlockProvider().getBlockState(rand, mutable), 2);
                            if (y >= 20) {
                                world.setBlockState(pos.add(x, y - 22, z), config.getBlockProvider().getBlockState(rand, mutable), 2);
                                if (y >= 22) {
                                    world.setBlockState(pos.add(x, y - 22, z), config.getBlockProvider().getBlockState(rand, mutable), 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (double z = -halfRadius; z <= halfRadius - 7; z++) {
            double x = 0;
            double y = 23;
            double noise = perlin.getValue(x, y, z) * 12;
            double scaledNoise = noise + x + y + z;
            if (scaledNoise >= 0.5) {
                world.setBlockState(pos.add(x, y - 22, z), config.getBlockProvider().getBlockState(rand, mutable), 2);
            }
        }

        return false;
    }


    public void setSeed(long seed) {
        if (perlin == null) {
            perlin = new Perlin();
            perlin.setSeed((int) seed);
            perlin.setFrequency(0.2);
        }
    }
}