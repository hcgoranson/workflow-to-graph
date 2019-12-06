package one.goranson.wf2graph.persistence;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class Neo4JConnector {

    @Value("${NEO4J_HOST}")
    private String neo4jHost;

    @Value("${NEO4J_USER}")
    private String neo4jUsername;

    @Value("${NEO4J_PSW}")
    private String neo4jPassword;

    public void execute(List<String> queries) {

        Driver driver = null;
        Session session = null;
        try {
            driver = GraphDatabase.driver(String.format("bolt://%s:7687", neo4jHost),
                    AuthTokens.basic(neo4jUsername, neo4jPassword));
            session = driver.session();
            queries.stream().forEach(session::run);

        } finally {
            Optional.ofNullable(session).ifPresent(s -> s.close());
            Optional.ofNullable(driver).ifPresent(d -> d.close());
        }
    }

}
