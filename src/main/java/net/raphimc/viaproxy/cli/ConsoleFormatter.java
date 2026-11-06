/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2021-2026 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viaproxy.cli;

public class ConsoleFormatter {

    private static final String PREFIX = "\033[";
    private static final String SUFFIX = "m";

    public static String convert(final String s) {
        StringBuilder out = new StringBuilder();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char next = i + 1 < chars.length ? chars[i + 1] : '\0';
            if (c == '§') {
                if (next != '\0') {
                    if (isColor(next)) out.append(convertToAnsi('r'));
                    out.append(convertToAnsi(next));
                    i++;
                }
            } else {
                out.append(c);
            }
        }
        return out + convertToAnsi('r');
    }

    // ANSI 码只是近似。使用真彩色以获得精确颜色。
    private static String convertToAnsi(final char color) {
        switch (Character.toLowerCase(color)) {
            case '0': // 黑色
                return PREFIX + getColor("30", 0x00_00_00) + SUFFIX;
            case '1': // 深蓝色
                return PREFIX + getColor("34", 0x00_00_AA) + SUFFIX;
            case '2': // 深绿色
                return PREFIX + getColor("32", 0x00_AA_00) + SUFFIX;
            case '3': // 深青色
                return PREFIX + getColor("36", 0x00_AA_AA) + SUFFIX;
            case '4': // 深红色
                return PREFIX + getColor("31", 0xAA_00_00) + SUFFIX;
            case '5': // 深紫色
                return PREFIX + getColor("35", 0xAA_00_AA) + SUFFIX;
            case '6': // 金色
                return PREFIX + getColor("33", 0xFF_AA_00) + SUFFIX;
            case '7': // 灰色
                return PREFIX + getColor("37", 0xAA_AA_AA) + SUFFIX;
            case '8': // 深灰色
                return PREFIX + getColor("90", 0x55_55_55) + SUFFIX;
            case '9': // 蓝色
                return PREFIX + getColor("94", 0x55_55_FF) + SUFFIX;
            case 'a': // 绿色
                return PREFIX + getColor("92", 0x55_FF_55) + SUFFIX;
            case 'b': // 青色
                return PREFIX + getColor("96", 0x55_FF_FF) + SUFFIX;
            case 'c': // 红色
                return PREFIX + getColor("91", 0xFF_55_55) + SUFFIX;
            case 'd': // 浅紫色
                return PREFIX + getColor("95", 0xFF_55_FF) + SUFFIX;
            case 'e': // 黄色
                return PREFIX + getColor("93", 0xFF_FF_55) + SUFFIX;
            case 'f': // 白色
                return PREFIX + getColor("97", 0xFF_FF_FF) + SUFFIX;
            case 'k': // 混淆
                return ""; // 终端不支持
            case 'l': // 粗体
                return PREFIX + "1" + SUFFIX;
            case 'm': // 删除线
                return PREFIX + "9" + SUFFIX;
            case 'n': // 下划线
                return PREFIX + "4" + SUFFIX;
            case 'o': // 斜体
                return PREFIX + "3" + SUFFIX;
            case 'r': // 重置
            default:
                return PREFIX + 0 + SUFFIX;
        }
    }

    private static boolean isColor(char color) {
        color = Character.toLowerCase(color);
        return color >= '0' && color <= '9' || color >= 'a' && color <= 'f';
    }

    private static String getColor(final String ansi, final int rgb) {
        return String.format("38;2;%d;%d;%d", (rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
    }

}
