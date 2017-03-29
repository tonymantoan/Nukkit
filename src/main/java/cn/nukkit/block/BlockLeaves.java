package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayList;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparent {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BRICH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public BlockLeaves() {
        this(0);
    }

    public BlockLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LEAVES;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Leaves",
                "Spruce Leaves",
                "Birch Leaves",
                "Jungle Leaves"
        };
        return names[this.meta & 0x03];
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.meta |= 0x04;
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isShears()) {
            return new int[][]{
                    {Item.LEAVES, this.meta & 0x03, 1}
            };
        } else {
            if ((int) ((Math.random()) * 200) == 0 && (this.meta & 0x03) == OAK) {
                return new int[][]{
                        {Item.APPLE, 0, 1}
                };
            }
            if ((int) ((Math.random()) * 20) == 0) {
                return new int[][]{
                        {Item.SAPLING, this.meta & 0x03, 1}
                };
            }
        }
        return new int[0][0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && (meta & 0b00001100) == 0x00) {
            meta |= 0x08;
            getLevel().setBlock(this, this, false, false);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if ((meta & 0b00001100) == 0x08) {
                meta &= 0x03;
                ArrayList<String> visited = new ArrayList<>();
                int check = 0;

                LeavesDecayEvent ev = new LeavesDecayEvent(this);

                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled() || findLog(this, visited, 0, check)) {
                    getLevel().setBlock(this, this, false, false);
                } else {
                    getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    private Boolean findLog(Block pos, ArrayList<String> visited, Integer distance, Integer check) {
        return findLog(pos, visited, distance, check, null);
    }

    private Boolean findLog(Block pos, ArrayList<String> visited, Integer distance, Integer check, Integer fromSide) {
        ++check;
        String index = pos.x + "." + pos.y + "." + pos.z;
        if (visited.contains(index)) return false;
        if (pos.getId() == Block.WOOD) return true;
        if (pos.getId() == Block.LEAVES && distance < 4) {
            visited.add(index);
            Integer down = pos.getSide(0).getId();
            if (down == Item.WOOD) {
                return true;
            }
            if (fromSide == null) {
                //North, East, South, West
                for (Integer side = 2; side <= 5; ++side) {
                    if (this.findLog(pos.getSide(side), visited, distance + 1, check, side)) return true;
                }
            } else { //No more loops
                switch (fromSide) {
                    case Block.SIDE_NORTH:
                        if (this.findLog(pos.getSide(Block.SIDE_NORTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_WEST), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_EAST), visited, distance + 1, check, fromSide)) return true;
                        break;
                    case Block.SIDE_SOUTH:
                        if (this.findLog(pos.getSide(Block.SIDE_SOUTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_WEST), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_EAST), visited, distance + 1, check, fromSide)) return true;
                        break;
                    case Block.SIDE_WEST:
                        if (this.findLog(pos.getSide(Block.SIDE_NORTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_SOUTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_WEST), visited, distance + 1, check, fromSide)) return true;
                    case Block.SIDE_EAST:
                        if (this.findLog(pos.getSide(Block.SIDE_NORTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_SOUTH), visited, distance + 1, check, fromSide)) return true;
                        if (this.findLog(pos.getSide(Block.SIDE_EAST), visited, distance + 1, check, fromSide)) return true;
                        break;
                }
            }
        }
        return false;
    }


    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}