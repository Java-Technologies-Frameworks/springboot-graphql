package com.jtf.graphql.api.dao;

import com.jtf.graphql.api.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person,Integer> {

    List<Person> findByEmail(String email);
}
