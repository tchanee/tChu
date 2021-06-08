package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This generic interface represents a serializer-deserializer which will be used in the client/server communication
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public interface Serde<T> {

    /**
     * Method that will allow the serialization of the object passed as argument
     *
     * @param t the object to be serialized
     * @return a string that corresponds to the serialization of the object
     */
    public abstract String serialize(T t);

    /**
     * Method will allow the deserialization of the String (message) passed as argument
     *
     * @param message the message to be deserialized
     * @return the object corresponding to the deserialization of the message
     */
    public abstract T deserialize(String message);

    /**
     * Generic method that takes two functions as argument: a serializer and a deserializer. It returns an anonymous class of Serde where the serialize and deserialize methods are implemented using the two parameters.
     *
     * @param serializer   the serializer that we wish to use
     * @param deserializer the deserializer that we wish to use
     * @param <T>          Generic type of the method
     * @return a Serde<T> whose two abstract methods are implemented
     */
    public static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<T>() {

            @Override
            public String serialize(T t) {
                return serializer.apply(t);
            }

            @Override
            public T deserialize(String message) {
                return deserializer.apply(message);
            }
        };
    }

    /**
     * Generic method that takes a list containing all the enumerated values of a set as an argument.
     *
     * @param list the list of all values in a set that we will be using in the creation of our Serde
     * @param <T>  Generic type of the method
     * @return a Serde<T> whose two methods (serialize and deserialize) are implemented.
     */
    public static <T> Serde<T> oneOf(List<T> list) {
        return new Serde<T>() {

            @Override
            public String serialize(T t) {
                if (t != null) {
                    int index = list.indexOf(t);
                    return Integer.toString(index);
                }
                return "";
            }

            @Override
            public T deserialize(String message) {
                if (!message.equals("")) {
                    int index = Integer.parseInt(message);
                    return list.get(index);
                }
                return null;
            }
        };
    }

    /**
     * Generic method that takes two arguments, a Serde that will be used to serialize/deserialize elements in a list
     * as well as the separator to be used in the deserializer
     *
     * @param serde     the Serde (serializer/deserializer) that we wish to use
     * @param separator the separator used in the Serde's deserializer
     * @param <T>       the generic type of the method
     * @return a Serde<List<T>> capable of serializing/deserializing objects of type List<T>
     */
    public static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {

        return new Serde<List<T>>() {
            @Override
            public String serialize(List<T> t) {
                final String delimiter = ",";
                return t.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(delimiter));
            }


            @Override
            public List<T> deserialize(String message) {
                String[] array = message.split(Pattern.quote(separator), -1);
                if (message.equals("")) {
                    return List.of();
                }
                return Arrays.stream(array)
                        .map(serde::deserialize)
                        .collect(Collectors.toList());
            }
        };
    }

    /**
     * Generic method that takes two arguments, a Serde that will be used to serialize/deserialize elements in a SortedBag
     * as well as the separator to be used in the deserializer
     *
     * @param serde     the Serde (serializer/deserializer) that we wish to use
     * @param separator the separator used in the Serde's deserializer
     * @param <T>       the generic type of the method
     * @return a Serde<SortedBag<T>> capable of serializing/deserializing objects of type SortedBag<T>
     */
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        return new Serde<SortedBag<T>>() {

            @Override
            public String serialize(SortedBag<T> t) {
                final String delimiter = ",";
                return t.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(delimiter));
            }

            @Override
            public SortedBag<T> deserialize(String message) {
                String[] array = message.split(Pattern.quote(separator), -1);
                return SortedBag.of(Arrays.stream(array)
                        .map(serde::deserialize)
                        .collect(Collectors.toList()));
            }
        };
    }

}
