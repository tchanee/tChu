package ch.epfl.tchu.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;


/**
 * A class containing all possible types of Serde (serializer/deserializer) that will be used to encode/decode information
 * during a game of tChu
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class ShowMyIpAddress {

    /**
     * Prints the local IP address
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        NetworkInterface.networkInterfaces()
                .filter(i -> {
                    try {
                        return i.isUp() && !i.isLoopback();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(NetworkInterface::inetAddresses)
                .filter(a -> a instanceof Inet4Address)
                .map(InetAddress::getCanonicalHostName)
                .forEachOrdered(System.out::println);
    }
}