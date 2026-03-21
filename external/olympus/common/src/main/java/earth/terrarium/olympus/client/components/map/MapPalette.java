package earth.terrarium.olympus.client.components.map;

import net.minecraft.util.ARGB;
import net.minecraft.world.level.material.MapColor;

public class MapPalette {

    public static final int[][] COLORS = new int[][]{
        {0, 0, 0, 0}, // None
        {0x9AB633, 0x799F32, 0x678E32, 0x517A30}, // Grass
        {0xF7E9A3, 0xE0CA92, 0xCCB785, 0x9D8B67}, // Sand
        {0xC6C7C7, 0xA7AAAB, 0x909396, 0x6A6C6F}, // Wool
        {0xFF4000, 0xDC2A06, 0xB11709, 0x810B0B}, // Fire
        {0xB5DBFF, 0x8AABDC, 0x707AB4, 0x545487}, // Ice
        {0xA4ACAD, 0x95999A, 0x757677, 0x535556}, // Metal
        {0x368318, 0x227413, 0x16601B, 0x144C1C}, // Plant
        {0xFFFFFF, 0xD8DDDD, 0xB1B1B5, 0x848388}, // Snow
        {0xA4ABB8, 0x8D919E, 0x737681, 0x565661}, // Clay
        {0x7C623F, 0x6C5035, 0x5D402B, 0x492D20}, // Dirt
        {0x6B6E72, 0x585A5E, 0x47484D, 0x3A3A40}, // Stone
        {0x60ACFE, 0x4F8DEB, 0x3C68CC, 0x314DB2}, // Water
        {0x8F7748, 0x7B663E, 0x645432, 0x4B3F26}, // Wood
        {0xFFFFF0, 0xDCDAD3, 0xB7B4AF, 0x908D8B}, // Quartz
        {0xD58727, 0xC46E26, 0xAD5D26, 0x954C24}, // Color Orange
        {0xD76AE2, 0xB761CF, 0xA05ABD, 0x7A4A9E}, // Color Magenta
        {0x60A0CD, 0x5988B8, 0x4C6D96, 0x3E4D73}, // Color Light Blue
        {0xE5E528, 0xC5BD2C, 0xA19A31, 0x787129}, // Color Yellow
        {0x90C118, 0x74AE17, 0x588C15, 0x396A15}, // Color Light Green
        {0xEC78AB, 0xCE6E93, 0xA95D75, 0x834C56}, // Color Pink
        {0x484848, 0x414141, 0x363636, 0x2B2B2B}, // Color Gray
        {0x909090, 0x848484, 0x6D6D6D, 0x575757}, // Color Light Gray
        {0x488290, 0x426F83, 0x38596A, 0x2F4153}, // Color Cyan
        {0x683CA7, 0x673797, 0x59307B, 0x4D275E}, // Color Purple
        {0x305AA7, 0x2D4697, 0x27387B, 0x23265E}, // Color Blue
        {0x605630, 0x57472C, 0x473525, 0x37261F}, // Color Brown
        {0x6B7830, 0x5A6C2D, 0x475725, 0x334420}, // Color Green
        {0x904D30, 0x833B2D, 0x6A2727, 0x502026}, // Color Red
        {0x191818, 0x171515, 0x121010, 0x0C0909}, // Color Black
        {0xB4A734, 0xA69237, 0x978137, 0x846A33}, // Gold
        {0x57D0B7, 0x50BAAF, 0x449894, 0x3A6D74}, // Diamond
        {0x4692FF, 0x4176DA, 0x3A5DB1, 0x323D86}, // Lapis
        {0x03CF29, 0x02B829, 0x06942C, 0x0A6E34}, // Emerald
        {0x7A672E, 0x6E552B, 0x593D24, 0x452B1E}, // Podzol
        {0x6A0E00, 0x5E0901, 0x4C0403, 0x39040B}, // Nether
        {0xC6B498, 0xB39E8A, 0x937E73, 0x72615C}, // Terracotta White
        {0x966422, 0x875020, 0x6D341C, 0x54251A}, // Terracotta Orange
        {0x8C5254, 0x7F4B54, 0x673F4D, 0x4F3441}, // Terracotta Magenta
        {0x666682, 0x5F5D76, 0x504D60, 0x433D4C}, // Terracotta Light Blue
        {0xC89732, 0xAA7E29, 0x825C18, 0x5D410F}, // Terracotta Yellow
        {0x6A6F32, 0x5A632E, 0x475026, 0x333D20}, // Terracotta Light Green
        {0x975F49, 0x894E43, 0x6E393A, 0x552F35}, // Terracotta Pink
        {0x362D21, 0x30261E, 0x271C18, 0x1E1413}, // Terracotta Gray
        {0x806F5D, 0x736154, 0x5E4C46, 0x493A38}, // Terracotta Light Gray
        {0x525756, 0x4B4F4F, 0x3E4141, 0x323334}, // Terracotta Cyan
        {0x734545, 0x683E44, 0x55353F, 0x422934}, // Terracotta Purple
        {0x503B57, 0x44354E, 0x352C3F, 0x282232}, // Terracotta Blue
        {0x483B21, 0x40301E, 0x342319, 0x271814}, // Terracotta Brown
        {0x4D4D28, 0x424524, 0x34381E, 0x252B18}, // Terracotta Green
        {0x86542B, 0x794128, 0x622D23, 0x4A1D1D}, // Terracotta Red
        {0x231B0F, 0x1F150D, 0x190F0B, 0x120B08}, // Terracotta Black
        {0xB2552D, 0xA13E2A, 0x822526, 0x632129}, // Crimson Nylium
        {0x8C3C44, 0x7E3747, 0x662F45, 0x4E263B}, // Crimson Stem
        {0x5C191D, 0x4F1519, 0x401114, 0x300D0F}, // Crimson Hyphae
        {0x157F76, 0x136F71, 0x12555B, 0x103945}, // Warped Nylium
        {0x378678, 0x337973, 0x2B6260, 0x23444B}, // Warped Stem
        {0x512A40, 0x492536, 0x3B202C, 0x2E191E}, // Warped Hyphae
        {0x13A967, 0x139969, 0x127C5A, 0x125D51}, // Warped Wart Block
        {0x695E5E, 0x5C5454, 0x4D4949, 0x423E3E}, // Deepslate
        {0xCDB88B, 0xB9A07F, 0x977C69, 0x755D55}, // Raw Iron
        {0x789D88, 0x6D8F7E, 0x5B7469, 0x495B56}, // Glow Lichen
    };

    // Add alpha to all the colors after
    static {
        for (int i = 0; i < COLORS.length; i++) {
            for (int j = 0; j < COLORS[i].length; j++) {
                int color = COLORS[i][j];
                COLORS[i][j] = ARGB.color(0xFF, color);
            }
        }
    }

    public static int getColor(int index, MapColor.Brightness brightness) {
        if (index >= COLORS.length) {
            return MapColor.getColorFromPackedId(MapColor.byId(index).getPackedId(brightness));
        }

        return COLORS[index][switch (brightness) {
            case LOWEST -> 0;
            case LOW -> 1;
            case NORMAL -> 2;
            case HIGH -> 3;
        }];
    }
}
