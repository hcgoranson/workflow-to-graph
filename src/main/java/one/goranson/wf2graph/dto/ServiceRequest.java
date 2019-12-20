package one.goranson.wf2graph.dto;

import com.opencsv.bean.CsvBindByName;

public class ServiceRequest {
    @CsvBindByName(column = "Client")
    private String client;
    @CsvBindByName(column = "Server")
    private String server;
    @CsvBindByName(column = "Method")
    private String method;
    @CsvBindByName(column = "Endpoint")
    private String endpoint;

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "client='" + client + '\'' +
                ", server='" + server + '\'' +
                ", method='" + method + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }

    public ServiceRequest() {
    }

    public ServiceRequest(String client, String server, String method, String endpoint) {
        this.client = client;
        this.server = server;
        this.method = method;
        this.endpoint = endpoint;
    }

    public String getClient() {
        return clean(client);
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getServer() {
        return clean(server);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getMethod() {
        return clean(method);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return clean(endpoint);
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequest() {
        return getMethod().concat("__").concat(getEndpoint());
    }

    public String getEndpointName() {
        return getMethod().concat(" ").concat(endpoint);
    }

    private String clean(String name) {
        return name
                .replace("-", "_")
                .replace(" ", "_")
                .replace("%s", "_X_")
                .replace("/", "_");
    }
}
