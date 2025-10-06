package com.pm.bankcards.util;

import org.springframework.data.jpa.domain.Specification;

public final class Specs {

    private Specs() {}

    public static <T>Specification<T> all() {
        return (r, q, cb) ->
                cb.conjunction();
    }

    public static <T> Specification<T> compose(Specification<T>... parts) {
        Specification<T> spec = all();
        for (Specification<T> p : parts)
            if (p != null) spec = spec.and(p);

        return spec;
    }
}
