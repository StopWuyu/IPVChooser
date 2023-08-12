package tech.egglink.iv;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Utils {
    public static int type = 0;
    public static InetSocketAddress resolveAddress(String hostname, int port, int type) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            if (addresses.length > 0) {
                if (type == 0) {
                    // Use IPv6 address if available
                    for (InetAddress address : addresses) {
                        if (address instanceof Inet6Address) {
                            return new InetSocketAddress(address, port);
                        }
                    }
                }
                // Use the first IPv4 address
                return new InetSocketAddress(addresses[0], port);
            }
        } catch (UnknownHostException e) {
            // Failed to resolve hostname
        }
        return null;
    }
}
