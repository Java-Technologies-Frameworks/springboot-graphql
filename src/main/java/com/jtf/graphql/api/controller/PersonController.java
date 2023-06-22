package com.jtf.graphql.api.controller;

import com.jtf.graphql.api.dao.PersonRepository;
import com.jtf.graphql.api.entity.Person;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Value("classpath:person.graphqls")
    private Resource schemeResource;

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemeResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {

        DataFetcher<List<Person>> fetcher1 = data -> {
            return (List<Person>) personRepository.findAll();
        };
        DataFetcher<Person> fetcher2 = data -> {
            return (Person) personRepository.findByEmail(data.getArgument("email"));
        };
        return RuntimeWiring.newRuntimeWiring().type("Query", typeWriting ->
                        typeWriting.dataFetcher("getAllPerson", fetcher1)
                                .dataFetcher("findPerson", fetcher2))
                .build();
    }


    @PostMapping("/addPerson")
    public String addPerson(@RequestBody List<Person> personList) {
        personRepository.saveAll(personList);
        return "Record inserted : " + personList.size();
    }

    @GetMapping("/getAllPerson")
    public List<Person> getPersons() {
        return (List<Person>) personRepository.findAll();

    }



    @PostMapping("/getPersonByEmail")
    public ResponseEntity<Object> getPersonByEmail(@RequestBody String query) {
        ExecutionResult executionResult = graphQL.execute(query);
        return new ResponseEntity<Object>(executionResult, HttpStatus.OK);
    }

    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll(@RequestBody String query) {
        ExecutionResult executionResult = graphQL.execute(query);
        return new ResponseEntity<Object>(executionResult, HttpStatus.OK);


    }
}
