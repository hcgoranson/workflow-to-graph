package one.goranson.wf2graph.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import one.goranson.wf2graph.dto.Element;
import one.goranson.wf2graph.dto.Elements;
import one.goranson.wf2graph.exceptions.ValidationException;
import one.goranson.wf2graph.persistence.Neo4JConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class ParserService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Neo4JConnector neo4JConnector;

    @Autowired
    private ResourceLoader resourceLoader;

    public void generateGraph(String configAsString) throws IOException {

        log.info("1. Create Java objects from JSON string");
        Elements elements = convertStringToElements(configAsString);

        log.info("2.Create a script for creating the nodes");
        List<String> createScript = createCreateScript(elements);

        log.info("3. Create a script for relationships between the nodes");
        List<String> relationship = createRelationshipScript(elements);

        log.info("4. Remove all existing nodes in graph db");
        neo4JConnector.execute(Collections.singletonList("MATCH (n) DETACH DELETE n"));

        log.info("5. Create new nodes");
        neo4JConnector.execute(createScript);

        log.info("6. Create new relationships between nodes");
        neo4JConnector.execute(relationship);

    }

    private List<String> createRelationshipScript(Elements elements) {
        final List<String> sequenceFlowScript = new ArrayList<>();

        elements.getGroupedElement().entrySet().stream()
                .filter(entry -> Arrays.asList("SequenceFlow").contains(entry.getKey()))
                .forEach(entry -> {

                    log.info(entry.getKey() + ": " + entry.getValue().toString());
                    // Example:
                    // MATCH (u:state1), (r:state2})
                    // CREATE (u)-[:REJECTED]->(r)
                    String pattern = "MATCH (a:%s), (b:%s)\n" +
                            "CREATE (a)-[:%s]->(b);\n";

                    // For each sequence
                    entry.getValue().stream().forEach(element -> {
                        log.info(element.toString());
                        String id = element.getId().replace(".", "_");
                        if (id.startsWith("from") && id.contains("To")) {
                            String[] states = id.substring(4).split("To");
                            String from = states[0];
                            String to = states[1];
                            String name = element.getName();
                            sequenceFlowScript.add(String.format(pattern, from, to, name));
                        }
                    });
                });

        return sequenceFlowScript;
    }

    private static List<String> createCreateScript(Elements elements) {
        final List<String> createScript = new ArrayList<>();

        log.info("=======================");
        log.info("   Nodes");
        log.info("=======================");
        final List<String> nodeFilter = new ArrayList<>();
        Arrays.asList("Start", "End", "V4App").stream().forEach(nodeFilter::add);

        // First, filter om the supported Node types
        // Then, create a 'create' script line and add it tp the output list
        elements.getGroupedElement().entrySet().stream()
                .peek(entry -> log.info(entry.getKey()))
                .filter(entry -> nodeFilter.contains(entry.getKey()))
                .forEach(entry -> {
                    // Example: create (:Start {name: 'Start'})
                    entry.getValue().stream().forEach(element -> {
                        String id = element.getId().replace(".", "_");
                        createScript.add(String.format(
                                "create (:%s {name: '%s', method: '%s', description: '%s', appId: '%s'})\n",
                                id, id, element.getMethod(), element.getName(), element.getAppId()));

                    });
                });
        return createScript;
    }

    private Elements convertStringToElements(String configAsString) throws IOException {
        try {
            List<Element> elements =
                    objectMapper.readValue(configAsString, new TypeReference<List<Element>>(){});

            log.info(elements.toString());

            Map<String, List<Element>> groupedElement =
                    elements.stream().collect(groupingBy(Element::getType));

            return new Elements(groupedElement);
        } catch (JsonParseException jpe) {
            throw new ValidationException("Failed to parse the file. Is it a JSON file?", jpe);
        }
    }

}
