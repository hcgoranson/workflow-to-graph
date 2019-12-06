# Workflow-to-graph 
A project to parse a JSON config file and create Cypher statements in order to visualize a workflow
represented in JSON in a Neo4j graph database 

## How to use:
1. git clone the project 
2. Execute `docker-compose up`, this will create the neo4j database and build and create the app docker image
3. Open localhost: http://localhost:8077/
4. Select the JSON config file
5. Click on _Generate graph_
6. Open Neo4j at http://localhost:7474/browser/ 
7. Log in with username **neo4j** and password **abc** 
8. Run the query `match(n) return n`
