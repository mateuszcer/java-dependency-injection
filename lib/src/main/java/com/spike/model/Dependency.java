package com.spike.model;

public record Dependency(Class<?> client, Class<?> service, Class<?> implementation) {
}
