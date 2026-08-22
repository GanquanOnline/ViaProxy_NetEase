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
package net.raphimc.viaproxy.proxy.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;
import net.raphimc.viaproxy.util.logging.Logger;

import java.nio.channels.ClosedChannelException;

public class ExceptionUtil {

    public static void handleNettyException(ChannelHandlerContext ctx, Throwable cause, ProxyConnection proxyConnection, boolean client2Proxy) {
        if (!ctx.channel().isOpen()) return;
        if (cause instanceof ClosedChannelException) return;
        if (isIgnorableUdpDatagramError(cause)) {
            Logger.LOGGER.warn("Ignored oversized UDP datagram: {}", cause.toString());
            return;
        }
        if (cause instanceof CloseAndReturn) {
            ctx.channel().close();
            return;
        }
        if (!client2Proxy || !ViaProxy.getConfig().shouldSuppressClientProtocolErrors()) {
            Logger.LOGGER.error("Caught unhandled netty exception", cause);
            try {
                if (proxyConnection != null) {
                    proxyConnection.kickClient("§cAn unhandled error occurred in your connection and it has been closed.\n§aError details for report:§f" + ExceptionUtil.prettyPrint(cause));
                }
            } catch (Throwable ignored) {
            }
        }
        ctx.channel().close();
    }

    private static boolean isIgnorableUdpDatagramError(final Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            final String message = String.valueOf(cause.getMessage()).toLowerCase();
            if (message.contains("message too long") || message.contains("emsgsize") || message.contains("error(-90)")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public static String prettyPrint(Throwable t) {
        final StringBuilder msg = new StringBuilder();
        if (t instanceof EncoderException && t.getCause() != null) t = t.getCause();
        if (t instanceof DecoderException && t.getCause() != null) t = t.getCause();
        while (t != null) {
            msg.append("\n");
            msg.append("§c").append(t.getClass().getSimpleName()).append("§7: §f").append(t.getMessage());
            t = t.getCause();
            if (t != null) {
                msg.append(" §9Caused by");
            }
        }
        return msg.toString();
    }

}
