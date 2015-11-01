package io.github.furikuri.linker;

import java.nio.file.Path;

public final class LinkInfo {
    protected final Path newLink;
    protected final Path target;

    public LinkInfo(final Path newLink, final Path target) {
        this.newLink = newLink;
        this.target = target;
    }
}
