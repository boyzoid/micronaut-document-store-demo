package com.boyzoid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.*;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ScoreService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientFactory clientFactory = new ClientFactory();
    private String url;
    private String schema;
    private String collection;
    private Client cli;

    public ScoreService(@Value("${demo.user}") String user,
                        @Value("${demo.password}") String password,
                        @Value("${demo.host}") String host,
                        @Value("${demo.port}") String port,
                        @Value("${demo.schema}") String schema,
                        @Value("${demo.collection}") String collection) {
        this.url = "mysqlx://" + user + ":" + password + "@" + host + ":" + port + "/";
        this.schema = schema;
        this.collection = collection;
        this.cli = clientFactory.getClient(this.url, "{\"pooling\":{\"enabled\":true, \"maxSize\":8,\"maxIdleTime\":30000, \"queueTimeout\":10000} }");
    }

    public ArrayList<Object> getScores(Integer limit) throws JsonProcessingException {
       Session session = getSession();
       Schema schema = session.getSchema(this.schema);
       Collection col = schema.getCollection(this.collection);
       DocResult result = col.find().limit(limit).execute();
       ArrayList<Object> docs = cleanResults(result.fetchAll());
       session.close();
       return docs;
    }

    private Session getSession(){
        return cli.getSession();
    }

    private ArrayList<Object> cleanResults(List<DbDoc> docs) throws JsonProcessingException {
        ArrayList<Object> cleaned = new ArrayList<>();
        for( DbDoc doc : docs){
            cleaned.add( objectMapper.readTree(doc.toString()));
        }
        return cleaned;
    }

}
