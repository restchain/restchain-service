package com.unicam.chorchain.codeGenerator;

import com.unicam.chorchain.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SolidityGenerator {

    private Set<String> visited = new HashSet<>();
    private List<Visitable> items = new ArrayList<>();

    public void traverse(TreeNode node) {


        if (!visited.contains(node.getId())) {
            log.debug("id: {} - n°:{}", node.getId(), visited.size() + 1);
            items.add(node);
            visited.add(node.getId());

            node.getOutgoing().forEach(this::traverse);
            node.getIncoming().forEach(this::traverse);
        }
    }

    public void eleab() {
        CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
        items.forEach(v -> v.accept(codeGenVisitor));
    }

    public Path load(String filename) {
        Path rootLocation = Paths.get("bpmn/");
        return rootLocation.resolve(filename);
    }

    public org.springframework.core.io.Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
}
