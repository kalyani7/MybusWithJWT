package com.mybus.dao.impl;

import com.mybus.configuration.ApplicationDataTestConfig;
import com.mybus.configuration.core.CoreAppConfig;
import com.mybus.dao.PersonDAO;
import com.mybus.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Iterator;

/**
 * Created by skandula on 1/6/16.
 */

@ActiveProfiles("test")
@ContextConfiguration(classes = { CoreAppConfig.class, ApplicationDataTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class PersonMongoDAOTest {

    @Autowired
    private PersonMongoDAO personMongoDAO;

    @Autowired
    private PersonDAO personDAO;

    @Test
    public void testUpdatePhone() throws Exception {

        Person p = new Person();
        p.setName("Joe");
        p.setPhone(7324444);
        personDAO.save(p);
        personMongoDAO.updatePhone("Joe", 1234);
        Iterable<Person> persons = personDAO.findByName("Joe");
        Iterator<Person> itr = persons.iterator();
        while (itr.hasNext()) {
            Person person = itr.next();
            //Assert.assertEquals(1234, p.getPhone());
        }

    }
}