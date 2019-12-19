package com.mybus.dao;

import com.mybus.model.Person;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;

/**
 * Created by skandula on 1/4/16.
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonDAOTest {

    @Before
    public void setUp() throws Exception {
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        cleanup();
    }

    private void cleanup() {
        personDAO.deleteAll();
    }
    @Autowired
    private PersonDAO personDAO;

    @Test
    public void testCreatePerson() {
        Person person = new Person();
        person.setAge(45);
        person.setName("Joe");
        person.setPhone(7433454);
        person = personDAO.save(person);
        Assert.assertTrue(person.getId() != null);
        Person loadedPerson = personDAO.findById(person.getId()).get();
        Assert.assertEquals(person.getName(), loadedPerson.getName());
        Assert.assertEquals(person.getAge(), loadedPerson.getAge());
        Assert.assertEquals(person.getPhone(), loadedPerson.getPhone());
    }
    @Test
    public void testUpdate() {
        Person p = new Person();
        p.setName("Joe");
        personDAO.save(p);
        Assert.assertNotNull(p.getId());
        p.setName("John");
        personDAO.save(p);
        //find all persons from the database
        Iterable<Person> persons = personDAO.findAll();
        Iterator<Person> itr = persons.iterator();
        //check if the collection has only Person object
        int i =0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(1, i);
        //check the name of the person is John
        Assert.assertEquals("John", persons.iterator().next().getName());

    }

    @Test
    public void testDeletePerson() {
        Person p = new Person();
        p.setName("Joe");
        personDAO.save(p);

        //delete person with id
        personDAO.deleteById("123");
        Iterable<Person> persons = personDAO.findAll();
        Iterator<Person> itr = persons.iterator();
        int i =0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(1, i);

        //try with existing id
        personDAO.deleteById(p.getId());
        persons = personDAO.findAll();
        itr = persons.iterator();
        i = 0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(0, i);
    }

    @Test
    public void testFindByPhone() {
        Person p = new Person();
        p.setName("Joe");
        p.setPhone(7324444);
        personDAO.save(p);
        Iterable<Person> persons = personDAO.findByPhone(7324444);
        Iterator<Person> itr = persons.iterator();
        int i =0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(1, i);

        //negetive test
        persons = personDAO.findByPhone(1234);
        i = 0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(0, i);
    }

    @Test
    public void testFindByPhoneAndName() {
        Person p = new Person();
        p.setName("Joe");
        p.setPhone(7324444);
        personDAO.save(p);
        Iterable<Person> persons = personDAO.findByPhoneAndName(7324444, "Joe");
        Iterator<Person> itr = persons.iterator();
        int i =0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(1, i);

        //negetive test
        persons = personDAO.findByPhoneAndName(7324444, "John");
        i = 0;
        while(itr.hasNext()) {
            itr.next();
            i++;
        }
        Assert.assertEquals(0, i);
    }

    @Test
    public void testUpdatePhone() {
        Person p = new Person();
        p.setName("Joe");
        p.setPhone(7324444);
        personDAO.save(p);
        Iterable<Person> persons = personDAO.findByName("Joe");
        Iterator<Person> itr = persons.iterator();
        int i =0;
        while(itr.hasNext()) {
            Person person = itr.next();
            person.setPhone(1234);
            personDAO.save(person);
            i++;
        }
        Assert.assertEquals(1, i);

        //update person set phone = 1234 where name = "Joe"

    }
}