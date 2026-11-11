package com.gempukku.swccgo.async.oidc;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Java's HttpClient refuses a URL whose host has an underscore ("unsupported URI"), and Docker container names
 * such as the sign-in service's have one. Resolve the host here and call it by address; the services check the
 * signed path, not the Host header.
 */
final class InternalUrl {
    private InternalUrl() {
    }

    static URI reachable(URI uri) throws UnknownHostException {
        if (uri.getHost() != null)
            return uri;
        String authority = uri.getRawAuthority();
        if (authority == null || authority.contains("@"))
            throw new IllegalArgumentException("unsupported URI");
        int colon = authority.lastIndexOf(':');
        String host = colon < 0 ? authority : authority.substring(0, colon);
        String port = colon < 0 ? "" : authority.substring(colon);
        String address = InetAddress.getByName(host).getHostAddress();
        if (address.contains(":"))
            address = "[" + address + "]";
        String rest = uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
        return URI.create(uri.getScheme() + "://" + address + port + rest);
    }
}
