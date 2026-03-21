package earth.terrarium.olympus.client.components.map;

import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

public class MapTopologyAlgorithm {

    public static final int BRIGHTER_COLOR = 0xff030303;

    public static int[][] getColors(int minX, int minZ, int maxX, int maxZ, ClientLevel level, LocalPlayer player) {
        int[][] colors = new int[maxX - minX][maxZ - minZ];
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos pos2 = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; x++) {
            pos1.setX(x);
            double depth = 0.0;
            for (int z = minZ - 1; z < maxZ; z++) {
                double fluidDepth = 0;
                pos1.setZ(z);
                LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
                if (!chunk.isEmpty()) {
                    MapColor color = MapColor.NONE;
                    double yDiff = 0.0;
                    int y = level.dimensionType().hasCeiling() ?
                        findBlockWithAirAbove(level, new BlockPos(x, player.getBlockY(), z)) :
                        chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
                    BlockState state = null;
                    if (y > level.getMinY() + 1) {
                        do {
                            y--;
                            pos1.setY(y);
                            state = chunk.getBlockState(pos1);
                        } while (!shouldRender(state, level, pos1) && y > level.getMinY());

                        if (y > level.getMinY() && !state.getFluidState().isEmpty()) {
                            int y2 = y - 1;
                            pos2.set(pos1);

                            BlockState blockState2;
                            do {
                                pos2.setY(y2--);
                                blockState2 = chunk.getBlockState(pos2);
                                ++fluidDepth;
                            } while (y2 > level.getMinY() && !blockState2.getFluidState().isEmpty());

                            state = getCorrectStateForFluidBlock(level, state, pos1);
                        }

                        yDiff += y;
                        color = Optionull.mapOrDefault(state, b -> b.getMapColor(level, pos1), MapColor.NONE);
                    }

                    fluidDepth /= 2.5;

                    MapColor.Brightness brightness;
                    if (color == MapColor.WATER) {
                        double darkness = fluidDepth * 0.1 + (double) (x + z & 1) * 0.2;
                        if (darkness < 0.5) {
                            brightness = MapColor.Brightness.LOW;
                        } else if (darkness > 0.9) {
                            brightness = MapColor.Brightness.HIGH;
                        } else {
                            brightness = MapColor.Brightness.NORMAL;
                        }
                    } else {
                        double darkness = (yDiff - depth) * 4.0 / 5D + ((double) (x + z & 1) - 0.5) * 0.4;
                        if (darkness > 0.6) {
                            brightness = MapColor.Brightness.HIGH;
                        } else if (darkness < -0.6) {
                            brightness = MapColor.Brightness.LOW;
                        } else {
                            brightness = MapColor.Brightness.NORMAL;
                        }
                    }

                    depth = yDiff;
                    if (z != minZ - 1) {
                        colors[x - minX][z - minZ] = getTintShade(color, state, level, pos1, brightness);
                    }
                }
            }
        }
        return colors;
    }

    private static BlockState getCorrectStateForFluidBlock(Level level, BlockState blockState, BlockPos blockPos) {
        FluidState fluidState = blockState.getFluidState();
        return !fluidState.isEmpty() && !blockState.isFaceSturdy(level, blockPos, Direction.UP) ? fluidState.createLegacyBlock() : blockState;
    }

    public static boolean shouldRender(BlockState state, Level level, BlockPos pos) {
        if (state.getMapColor(level, pos) == MapColor.NONE) return false;
        if (state.getFluidState().isEmpty()) return state.is(Blocks.SNOW) || !state.canBeReplaced();
        return true;
    }

    public static int getTintShade(MapColor color, BlockState state, Level level, BlockPos pos, MapColor.Brightness brightness) {
        if (color == MapColor.WATER || color == MapColor.GRASS || color == MapColor.PLANT) {
            int tintColor = BiomeColors.getAverageGrassColor(level, pos);
            if (color == MapColor.WATER) tintColor = BiomeColors.getAverageWaterColor(level, pos);
            if (color == MapColor.PLANT) tintColor = BiomeColors.getAverageFoliageColor(level, pos);
            int intColor = rgb2abgr(tintColor);
            if (color == MapColor.WATER) {
                intColor = brighter(intColor);
            }
            return switch (brightness) {
                case LOWEST -> intColor;
                case LOW -> darker(intColor);
                case NORMAL -> darker(darker(intColor));
                case HIGH -> darker(darker(darker(intColor)));
            };
        } else if (state != null) {
            int tintColor = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
            if (tintColor == -1) {
                return MapPalette.getColor(color.id, brightness);
            }
            int intColor = rgb2abgr(Minecraft.getInstance().getBlockColors().getColor(state, level, pos));
            return switch (brightness) {
                case LOWEST -> intColor;
                case LOW -> darker(intColor);
                case NORMAL -> darker(darker(intColor));
                case HIGH -> darker(darker(darker(intColor)));
            };
        }
        return 0x00000000;
    }

    public static int rgb2abgr(int rgb) {
        return (rgb & 0xFF00FF00) | ((rgb & 0xFF) << 16) | ((rgb >> 16) & 0xFF) | 0xFF000000;
    }

    private static int darker(int abgr) {
        int red = (abgr >> 16) & 0xFF;
        int green = (abgr >> 8) & 0xFF;
        int blue = abgr & 0xFF;
        return (Math.max((int) (red * 0.7f), 0) << 16) | (Math.max((int) (green * 0.7f), 0) << 8) | Math.max((int) (blue * 0.7f), 0) | 0xff000000;
    }

    private static int brighter(int abgr) {
        int red = (abgr >> 16) & 0xFF;
        int green = (abgr >> 8) & 0xFF;
        int blue = abgr & 0xFF;
        if (red == 0 && green == 0 && blue == 0) {
            return BRIGHTER_COLOR;
        }
        if (red > 0 && red < 3) {
            red = 3;
        }
        if (green > 0 && green < 3) {
            green = 3;
        }
        if (blue > 0 && blue < 3) {
            blue = 3;
        }
        return (Math.min((int) (red * 1.1f), 255) << 16) | (Math.min((int) (green * 1.1f), 255) << 8) | Math.min((int) (blue * 1.1f), 255) | 0xff000000;
    }

    private static int findBlockWithAirAbove(ClientLevel level, BlockPos pos) {
        int offset = 0;
        int y = pos.getY();
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        int attempts = 0;

        while (!(!level.getBlockState(mutablePos).isAir() && (level.getBlockState(mutablePos.above()).isAir() || level.getBlockState(mutablePos.below()).isAir()))) {
            if (offset <= 0) {
                offset = -offset + 1;
            } else {
                offset = -offset;
            }
            mutablePos.setY(offset + y);
            attempts++;
            if (attempts > 500) {
                return y;
            }
        }
        return mutablePos.getY();
    }
}
