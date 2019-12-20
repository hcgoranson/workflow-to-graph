package one.goranson.wf2graph.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import one.goranson.wf2graph.dto.ServiceRequest;
import one.goranson.wf2graph.persistence.Neo4JConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.*;

@Service
@Slf4j
public class CsvParserService {

    @Autowired
    private Neo4JConnector neo4JConnector;

    public void generateGraph(Reader reader) {
        log.info("1. Create Java objects from JSON string");
        List<ServiceRequest> relations = parseCsvFile(reader);

        log.info("2. Create a distinct list of service");
        Set<String> services = createDistinctServiceSet(relations);
        services.stream().forEach(s -> log.info(s));

        log.info("3. Create a script for creating the nodes");
        List<String> createScript = createCreateScript(services);

        log.info("4. Create a script for relationships between the nodes");
        List<String> relationship = createRelationshipScript(relations);

        log.info("5. Remove all existing nodes in graph db");
        neo4JConnector.execute(Collections.singletonList("MATCH (n) DETACH DELETE n"));

        log.info("6. Create new nodes");
        neo4JConnector.execute(createScript);

        log.info("7. Create new relationships between nodes");
        neo4JConnector.execute(relationship);

        relations.stream().forEach(item -> log.info(item.toString()));

    }

    private List<String> createRelationshipScript(List<ServiceRequest> relations) {
        final List<String> sequenceFlowScript = new ArrayList<>();
        String pattern = "MATCH (a:%s), (b:%s)\n" +
                "CREATE (a)-[:%s { name: '%s' }]->(b);\n";

        relations.stream().forEach(relation -> {
            sequenceFlowScript.add(String.format(pattern, relation.getClient(), relation.getServer(), relation.getRequest(), relation.getEndpointName()));
        });


        return sequenceFlowScript;
    }

    private static List<String> createCreateScript(Set<String> services) {
        final List<String> createScript = new ArrayList<>();
        services.stream().forEach(service -> {
            createScript.add(String.format(
                    "create (:%s {name: '%s'})\n", service, service));
        });
        return createScript;
    }

    private Set<String> createDistinctServiceSet(List<ServiceRequest> relations) {
        final Set<String> services = new HashSet<>();
        relations.stream().forEach(serviceRequest -> {
            services.add(serviceRequest.getClient());
            services.add(serviceRequest.getServer());
        });
        return services;
    }



    public List<ServiceRequest> parseCsvFile(Reader reader) {

        final CsvToBean<ServiceRequest> csvToBean = new CsvToBeanBuilder(reader)
                .withType(ServiceRequest.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }

}
