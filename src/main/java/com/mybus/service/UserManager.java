package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.RoleDAO;
import com.mybus.dao.UserDAO;
import com.mybus.dao.impl.MongoQueryDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.model.BranchOffice;
import com.mybus.model.Role;
import com.mybus.model.User;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by skandula on 2/13/16.
 */
@Service
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(CityManager.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private MongoQueryDAO mongoQueryDAO;

    @Autowired
    private BranchOfficeManager branchOfficeManager;

    @Autowired
    private SessionManager sessionManager;

    public User findByUserName(String userName) {
        User user = userDAO.findOneByUserName(userName);
        if(user.getRole() != null){
            Role role = roleDAO.findById(user.getRole()).get();
            user.setAccessibleModules(role.getMenus());
        }
        return user;
    }
    public User findOne(String userId) {
        Optional<User> user = userDAO.findById(userId);
        if(user.isPresent()){
            return user.get();
        } else {
            return null;
        }
    }
    public User saveUser(User user){
        user.validate();
        User duplicateUser = userDAO.findOneByUserName(user.getUserName());
        if (duplicateUser != null && !duplicateUser.getId().equals(user.getId())) {
            throw new RuntimeException("A user already exists with username");
        }
        BranchOffice office = branchOfficeManager.findOne(user.getBranchOfficeId());
        user.getAttributes().put(BranchOffice.KEY_NAME, office.getName());
        user.setOperatorId(sessionManager.getOperatorId());
        //validateAgent(user);
        if(logger.isDebugEnabled()) {
            logger.debug("Saving user: [{}]", user);
        }
        return userDAO.save(user);
    }

    public User updateUser(User user) {
        Preconditions.checkNotNull(user, "The user can not be null");
        Preconditions.checkNotNull(user.getId(), "Unknown user for update");
        User loadedUser = userDAO.findById(user.getId()).get();
        loadedUser.setUserName(user.getUserName());
        loadedUser.setFirstName(user.getFirstName());
        loadedUser.setLastName(user.getLastName());
        loadedUser.setContact(user.getContact());
        loadedUser.setPassword(user.getPassword());
        loadedUser.setBranchOfficeId(user.getBranchOfficeId());
        loadedUser.setEmail(user.getEmail());
        loadedUser.setSecondaryContact(user.getSecondaryContact());
        loadedUser.setAddress1(user.getAddress1());
        loadedUser.setActive(user.isActive());
        loadedUser.setRole(user.getRole());
        loadedUser.setAccessibleModules(user.getAccessibleModules());
        loadedUser.setCancelCargoBookings(user.isCancelCargoBookings());
        return saveUser(loadedUser);
    }

    public boolean deleteUser(String userId){
        Preconditions.checkNotNull(userId, "The user id can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting user:[{}]" + userId);
        }
        if (userDAO.findById(userId).isPresent()) {
            userDAO.deleteById(userId);
        } else {
            throw new RuntimeException("Unknown user id");
        }
        return true;
    }

    public User getUser(String id){
        Preconditions.checkNotNull(id,"UserId cannot be Null");
        if(id.equalsIgnoreCase("anonymousUser")) {
            return userDAO.save(new User("anonymousUser"));
        }
        User user = userDAO.findById(id).get();
        if(user == null){
            throw new RuntimeException("User does not exist with that Id");
        }
        return user;
    }

    /**
     *  Get user names as a map
     * @param includeInactive
     * @return
     */
    public Map<String, String> getUserNames(boolean includeInactive) {
        List<User> users = getUserNamesAsUserList(includeInactive);
        Map<String, String> map = users.stream().collect(
                Collectors.toMap(User::getId, user -> user.getFullName()));
        return map;
    }

    /**
     * Get users as a list only include the name fields
     * @param includeInactive
     * @return
     */
    public List<User> getUserNamesAsUserList(boolean includeInactive) {
        String fields[] = {User.FIRST_NAME, User.LAST_NAME};
        JSONObject query = new JSONObject();
        if(!includeInactive) {
            query.put("active", true);
        }
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.ASC, User.FIRST_NAME);
        List<User> users = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(User.class, User.COLLECTION_NAME, fields, query, pageable).iterator());
        return users;
    }

    public List<User> findAll() {
        List<User> users = null;
        if(sessionManager.getOperatorId() != null){
            users = userDAO.findByOperatorId(sessionManager.getOperatorId());
        } else {
            users = IteratorUtils.toList(userDAO.findAll().iterator());
        }
        return users;
    }

    public List<User> getUserCashBalances() {
        String fields[] = {User.USER_NAME, User.FIRST_NAME, User.LAST_NAME, User.AMOUNT_TO_BE_PAID, User.CONTACT, User.ROLE, User.BRANCH_OFFICE_ID};
        JSONObject query = new JSONObject();
        List<User> users = IteratorUtils.toList(mongoQueryDAO
                .getDocuments(User.class, User.COLLECTION_NAME, fields, query, null).iterator());
        return users;
    }
/*
    private void validateAgent(User user){
        if (user.getUserType().equals(UserType.AGENT)) {
            Preconditions.checkNotNull(user.getPlanType(), "Agent planType is required");
            Preconditions.checkNotNull(user.getAddress1(),"Agent address required");
            Preconditions.checkNotNull(user.getContact(),"Agent contact number required");
            Preconditions.checkNotNull(user.getCity(),"Agent city required");
            Preconditions.checkNotNull(user.getState(),"Agent state required");
            if (!(user.getPlanType().equals(planTypeDAO.findOneById(user.getPlanType())))) {
                throw new RuntimeException("Plan Does not exist");
            }
        }
    }
    */


    public Map<String, List<User>> getUsersByBranchOffices() {
        return null;
    }
    public User updatePassword(JSONObject userJson) {
        User user = userDAO.findOneByUserName(userJson.get("userName").toString());
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if(!user.getPassword().equals(userJson.get("currentPassword").toString())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        if(!userJson.get("password").toString().equals(userJson.get("confirmPassword").toString())){
            throw new IllegalArgumentException("Passwords do not match");
        }
        if(userMongoDAO.updatePassword(user.getUserName(), userJson.get("password").toString())){
            user.setPassword(userJson.get("password").toString());
            return user;
        } else {
            throw new IllegalArgumentException("Failed to update password");
        }
    }

    public boolean isAgent(User currentUser) {
        if(currentUser != null && currentUser.getRole() != null) {
            Role userRole = roleDAO.findById(currentUser.getRole()).get();
            return userRole.getName().equalsIgnoreCase("agent");
        }
        return false;
    }
}
