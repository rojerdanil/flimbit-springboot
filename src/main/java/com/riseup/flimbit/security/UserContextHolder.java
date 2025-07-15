package com.riseup.flimbit.security;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> context = new ThreadLocal<>();

    public static void setContext(UserContext userContext) {
        context.set(userContext);
    }

    public static UserContext getContext() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
