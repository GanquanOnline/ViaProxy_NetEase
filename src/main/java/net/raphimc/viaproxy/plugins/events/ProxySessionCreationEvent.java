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
package net.raphimc.viaproxy.plugins.events;

import net.raphimc.netminecraft.netty.connection.NetClient;

public class ProxySessionCreationEvent<T extends NetClient> {

    /** 用于连接服务端并存储连接相关数据的代理会话。 */
    private T proxySession;

    /** 该代理会话是否为旧版直通处理器。 */
    private final boolean isLegacyPassthrough;

    public ProxySessionCreationEvent(final T proxySession, final boolean isLegacyPassthrough) {
        this.proxySession = proxySession;
        this.isLegacyPassthrough = isLegacyPassthrough;
    }

    public T getProxySession() {
        return this.proxySession;
    }

    public void setProxySession(final T proxySession) {
        this.proxySession = proxySession;
    }

    public boolean isLegacyPassthrough() {
        return this.isLegacyPassthrough;
    }

}
