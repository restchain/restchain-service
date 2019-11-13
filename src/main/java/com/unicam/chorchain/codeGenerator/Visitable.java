package com.unicam.chorchain.codeGenerator;

public interface Visitable {
    String accept(Visitor visitor);
}
