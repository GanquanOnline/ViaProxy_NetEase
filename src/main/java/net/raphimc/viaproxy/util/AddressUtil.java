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
package net.raphimc.viaproxy.util;

import com.google.common.net.HostAndPort;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.unix.DomainSocketAddress;
import net.lenni0451.reflect.stream.RStream;
import net.raphimc.netminecraft.util.MinecraftServerAddress;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.viaproxy.util.address.NetherNetLanAddress;
import net.raphimc.viaproxy.util.address.NetherNetXboxAddress;
import net.raphimc.viaproxy.util.address.NetherNetXboxJsonRpcAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.regex.Pattern;

public class AddressUtil {

    private static final Pattern NETHERNET_NETWORK_ID_PATTERN = Pattern.compile("^(?>[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}|\\d+)$", Pattern.CASE_INSENSITIVE);

    public static SocketAddress parse(final String serverAddress, final ProtocolVersion version) {
        if (serverAddress.startsWith("file://") || serverAddress.startsWith("unix://")) { // Unix 套接字
            return new DomainSocketAddress(serverAddress.substring(serverAddress.indexOf("://") + 3));
        } else if (serverAddress.startsWith("nethernet-lan://")) { // NetherNet LAN 地址
            final HostAndPort hostAndPort = HostAndPort.fromString(serverAddress.substring(serverAddress.indexOf("://") + 3));
            if (hostAndPort.getHost().isBlank()) {
                throw new IllegalArgumentException("Server address cannot be blank");
            }
            return new NetherNetLanAddress(hostAndPort.getHost(), hostAndPort.getPortOrDefault(ProtocolConstants.BEDROCK_NETHERNET_DEFAULT_PORT));
        } else if (serverAddress.startsWith("nethernet-xbox://")) { // NetherNet Xbox 地址
            final String addressPart = serverAddress.substring(serverAddress.indexOf("://") + 3);
            if (!NETHERNET_NETWORK_ID_PATTERN.matcher(addressPart).matches()) {
                throw new IllegalArgumentException("Invalid NetherNet Xbox address");
            }
            return new NetherNetXboxAddress(addressPart);
        } else if (serverAddress.startsWith("nethernet-xbox-json-rpc://")) { // NetherNet Xbox JSON-RPC 地址
            final String addressPart = serverAddress.substring(serverAddress.indexOf("://") + 3);
            if (!NETHERNET_NETWORK_ID_PATTERN.matcher(addressPart).matches()) {
                throw new IllegalArgumentException("Invalid NetherNet Xbox JSON-RPC address");
            }
            return new NetherNetXboxJsonRpcAddress(addressPart);
        } else { // IP 地址
            final HostAndPort hostAndPort = HostAndPort.fromString(serverAddress);
            if (hostAndPort.getHost().isBlank()) {
                throw new IllegalArgumentException("Server address cannot be blank");
            }

            final int port;
            if (version != null) {
                port = hostAndPort.getPortOrDefault(BedrockProtocolVersion.isBedrock(version) ? ProtocolConstants.BEDROCK_RAKNET_DEFAULT_PORT : 25565);
            } else {
                port = hostAndPort.getPort();
            }

            if (version == null || version.olderThan(LegacyProtocolVersion.r1_3_1tor1_3_2) || BedrockProtocolVersion.isBedrock(version)) {
                return new InetSocketAddress(hostAndPort.getHost(), port);
            } else {
                return MinecraftServerAddress.ofResolved(hostAndPort.getHost(), port);
            }
        }
    }

    public static String toString(final SocketAddress address) {
        if (address instanceof DomainSocketAddress domainSocketAddress) {
            return "unix://" + domainSocketAddress.path();
        } else if (address instanceof NetherNetLanAddress netherNetAddress) {
            return "nethernet-lan://" + netherNetAddress.getHostString() + ":" + netherNetAddress.getPort();
        } else if (address instanceof NetherNetXboxAddress netherNetAddress) {
            return "nethernet-xbox://" + netherNetAddress;
        } else if (address instanceof NetherNetXboxJsonRpcAddress netherNetAddress) {
            return "nethernet-xbox-json-rpc://" + netherNetAddress;
        } else if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
        } else {
            return address.toString();
        }
    }

    @Deprecated(forRemoval = true)
    public static int getDefaultPort(final ProtocolVersion version) {
        if (BedrockProtocolVersion.isBedrock(version)) {
            return ProtocolConstants.BEDROCK_RAKNET_DEFAULT_PORT;
        }

        return 25565;
    }

    /**
     * 解析 Java 传送目标。MOT/Waterdog 可能下发不在 DNS 里的下游主机名，
     * 这里保留主机，让 ViaProxy 仍能把传送包改写回自己。
     */
    public static InetSocketAddress resolveTransferTarget(final String host, final int port) {
        try {
            return MinecraftServerAddress.ofResolved(host, port);
        } catch (final Exception ignored) {
            return InetSocketAddress.createUnresolved(host, port);
        }
    }

    /**
     * 尽可能把 netty 域套接字地址转换为 Java 16 的 unix 域套接字地址
     *
     * @param address netty 域套接字地址
     *
     * @return Java 16 的 unix 域套接字地址；若不是域套接字地址则返回原地址
     */
    public static SocketAddress toJ16UnixSocketAddress(final SocketAddress address) {
        try {
            if (address instanceof DomainSocketAddress domainSocketAddress) {
                return RStream.of("java.net.UnixDomainSocketAddress").methods().by("of", String.class).invokeArgs(domainSocketAddress.path());
            }
        } catch (Throwable ignored) {
        }

        return address;
    }

}
