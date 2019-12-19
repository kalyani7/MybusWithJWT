package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.dao.PersonDAO;
import com.mybus.dao.impl.PersonMongoDAO;
import com.mybus.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by skandula on 1/7/16.
 */

@RestController
@RequestMapping(value = "/api/v1/")
public class PersonController extends MyBusBaseController {

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private PersonMongoDAO personMongoDAO;


    @RequestMapping(value = "persons", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public Iterable<Person> getPersons() {
        Iterable<Person> persons = personDAO.findAll();
        return persons;
    }




     @RequestMapping(value = "person", method = RequestMethod.POST, consumes =  MediaType.APPLICATION_JSON_VALUE,
            produces = ControllerUtils.JSON_UTF8
    )
    @ResponseBody
    public Person savePersons(@RequestBody final Person person ){
        return personDAO.save(person);

    }
    @RequestMapping(value = "person/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public Person getPerson(@PathVariable final String id) {
        Person person = personDAO.findById(id).get();
        return person;
    }

    @RequestMapping(value="person/{id}",method= RequestMethod.DELETE)
    @ResponseBody
    public void deletePerson(@PathVariable final String id){
        personDAO.deleteById(id);

    }
    @RequestMapping(value = "person/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ResponseBody
    public Iterable<Person> updatePerson(@RequestBody final Person person) {
        personMongoDAO.updatePerson(person);
        return personDAO.findAll();
    }
  }



