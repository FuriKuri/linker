package io.github.furikuri.linker;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Linker implements ServletContextListener {
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        for (String line : readLines(servletContextEvent)) {
            final LinkInfo linkInfo = parseLine(line);
            if (!Files.exists(linkInfo.newLink)) {
                try {
                    Files.createSymbolicLink(linkInfo.newLink, linkInfo.target);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        for (String line : readLines(servletContextEvent)) {
            final LinkInfo linkInfo = parseLine(line);
            if (Files.isSymbolicLink(linkInfo.newLink)) {
                try {
                    Files.delete(linkInfo.newLink);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private LinkInfo parseLine(final String line) {
        final String[] lineConfig = line.split("->");
        final Path newLink = Paths.get(lineConfig[0]);
        final Path target = Paths.get(lineConfig[1]);
        return new LinkInfo(newLink, target);
    }

    private List<String> readLines(final ServletContextEvent servletContextEvent) {
        String value = servletContextEvent.getServletContext().getInitParameter("linkConfig");

        final String path = value.replace("classpath:", "");
        final InputStream resourceAsStream = Linker.class.getResourceAsStream(path);
        try {
            return IOUtils.readLines(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
