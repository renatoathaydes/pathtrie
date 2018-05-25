package com.athaydes.pathtrie;

import com.athaydes.pathtrie.functions.Fun;
import java.util.function.Function;

abstract class Box<E> {

    private Box() {
        // hide
    }

    public <T> T use(Function<SimpleBox<E>, T> useSimpleBox,
                     Function<FunBox<E>, T> usesFunBox) {
        if (this instanceof SimpleBox) {
            return useSimpleBox.apply((SimpleBox<E>) this);
        } else {
            return usesFunBox.apply((FunBox<E>) this);
        }
    }

    static final class SimpleBox<E> extends Box<E> {
        final E element;

        SimpleBox(E element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return element.toString();
        }
    }

    static final class FunBox<E> extends Box<E> {
        final Fun<E> fun;

        public FunBox(Fun<E> fun) {
            this.fun = fun;
        }
    }

}
