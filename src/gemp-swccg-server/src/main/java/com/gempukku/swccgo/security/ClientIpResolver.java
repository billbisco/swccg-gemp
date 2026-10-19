package com.gempukku.swccgo.security;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Works out the real client IP behind nginx.
 *
 * <p>{@code X-Forwarded-For} is only believed when the TCP peer is a configured trusted proxy.
 * The header is then read right to left, skipping trusted hops, and the first untrusted entry is
 * the client. nginx's {@code $proxy_add_x_forwarded_for} appends the address it saw, so a value the
 * client invented sits further left and is ignored. Anything that is not a literal IPv4/IPv6
 * address is rejected (no DNS lookups), so the stored IP can never carry HTML or SQL.</p>
 */
public final class ClientIpResolver {
    /** Loopback plus the private ranges Docker uses for the host-to-container hop. */
    public static final String DEFAULT_TRUSTED = "127.0.0.0/8,::1/128,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16";

    private final List<Cidr> _trusted;

    public ClientIpResolver(String trustedProxies) {
        List<Cidr> parsed;
        try {
            parsed = parseList(trustedProxies == null || trustedProxies.isBlank() ? DEFAULT_TRUSTED : trustedProxies);
        } catch (RuntimeException exp) {
            // A typo in config must not take the server down; fall back to the safe default.
            parsed = parseList(DEFAULT_TRUSTED);
        }
        _trusted = parsed;
    }

    private static List<Cidr> parseList(String spec) {
        List<Cidr> list = new ArrayList<>();
        for (String part : spec.split(",")) {
            String p = part.trim();
            if (!p.isEmpty())
                list.add(Cidr.parse(p));
        }
        return list;
    }

    /**
     * @param peerAddress the socket peer address (always a literal IP)
     * @param forwardedFor the raw X-Forwarded-For header, or null
     * @return a validated, normalized IP literal
     */
    public String resolve(String peerAddress, String forwardedFor) {
        String peer = normalize(peerAddress);
        if (peer == null)
            return "unknown";
        if (forwardedFor == null || !isTrusted(peer))
            return peer;
        String[] hops = forwardedFor.split(",");
        for (int i = hops.length - 1; i >= 0; i--) {
            String hop = normalize(hops[i].trim());
            if (hop == null)
                return peer; // garbage in the chain: fall back to what we can see
            if (!isTrusted(hop))
                return hop;
        }
        return peer;
    }

    public boolean isTrusted(String ip) {
        String n = normalize(ip);
        if (n == null)
            return false;
        InetAddress addr = InetAddresses.forString(n);
        for (Cidr c : _trusted) {
            if (c.contains(addr))
                return true;
        }
        return false;
    }

    /** Returns the canonical text form of a literal IP, or null if it is not one. Max 45 chars (DB column). */
    public static String normalize(String candidate) {
        if (candidate == null)
            return null;
        String c = candidate.trim();
        if (c.startsWith("[") && c.endsWith("]"))
            c = c.substring(1, c.length() - 1);
        if (c.isEmpty() || c.length() > 45 || !InetAddresses.isInetAddress(c))
            return null;
        InetAddress addr = InetAddresses.forString(c);
        // Report IPv4-mapped IPv6 (::ffff:1.2.3.4) as plain IPv4 so bans and limits match.
        if (InetAddresses.isMappedIPv4Address(c))
            addr = InetAddresses.getCoercedIPv4Address(addr);
        return InetAddresses.toAddrString(addr);
    }

    private static final class Cidr {
        final byte[] network;
        final int prefix;

        private Cidr(byte[] network, int prefix) {
            this.network = network;
            this.prefix = prefix;
        }

        static Cidr parse(String spec) {
            String[] parts = spec.split("/");
            String ip = normalize(parts[0]);
            if (ip == null)
                throw new IllegalArgumentException("Bad trusted proxy entry: " + spec);
            byte[] bytes = InetAddresses.forString(ip).getAddress();
            int prefix = parts.length > 1 ? Integer.parseInt(parts[1]) : bytes.length * 8;
            return new Cidr(bytes, prefix);
        }

        boolean contains(InetAddress addr) {
            byte[] a = addr.getAddress();
            if (a.length != network.length)
                return false;
            int full = prefix / 8, rem = prefix % 8;
            for (int i = 0; i < full; i++)
                if (a[i] != network[i])
                    return false;
            if (rem == 0)
                return true;
            int mask = (0xFF << (8 - rem)) & 0xFF;
            return (a[full] & mask) == (network[full] & mask);
        }
    }
}
