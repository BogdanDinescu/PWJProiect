package com.bogdan.pharmacy.utils;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class MyUtils{
    public static <T> Collection<T> iterableToCollections(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
}
