package com.mybus.model;

import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = User.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class User extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME = "user";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String CONTACT = "contact";
    public static final String USER_NAME = "userName";
    public static final String BRANCH_USER = "branchUser";
    public static final String BRANCH_OFFICE_ID = "branchOfficeId";

    public static final String AMOUNT_TO_BE_PAID = "amountToBePaid";
    public static final String ROLE = "role";

    @Field(USER_NAME)
    @RequiresValue
    private String userName;
    @Field(PASSWORD)
    @RequiresValue
    private String password;
    @Field(FIRST_NAME)
    @RequiresValue
    private String firstName;
    @Field(LAST_NAME)
    @RequiresValue
    private String lastName;
    private boolean canVerifyRates;
    private boolean admin;
    private boolean superAdmin;
    private boolean active;
    @RequiresValue
    private String email;
    @RequiresValue
    private long contact;
    private long secondaryContact;
    private String address1;
    private String address2;
    private String state;
    @RequiresValue
    private String role;
    private String planType;
    @Indexed
    private String branchOfficeId;

    private boolean accountNonLocked;
    private boolean enabled;
    private boolean accountNonExpired;

    @Field(AMOUNT_TO_BE_PAID)
    private double amountToBePaid;
    private double amountToBeCollected;

    private Set<String> accessibleModules;

    private boolean cancelCargoBookings;

    public User(String userName) {
        this.setUserName(userName);
        this.firstName = userName;
        this.lastName = userName;
        this.setActive(true);
    }

    public User(JSONObject json){
        if(json.containsKey("id")) {
            this.setId(json.get("id").toString());
        }
        if(json.containsKey("userName")) {
            this.userName = json.get("userName").toString();
        }
        if(json.containsKey("firstName")) {
            this.firstName = json.get("firstName").toString();
        }
        if(json.containsKey("active")) {
            this.active = Boolean.valueOf(json.get("active").toString());
        }
        if(json.containsKey("email")) {
            this.email = json.get("email").toString();
        }
        if(json.containsKey("lastName")) {
            this.lastName = json.get("lastName").toString();
        }
        if(json.containsKey("password")) {
            this.password = json.get("password").toString();
        }
        if(json.containsKey("contact")) {
            this.contact = Long.parseLong(json.get("contact").toString());
        }
        if(json.containsKey("secondaryContact") && json.get("secondaryContact") != null) {
            this.secondaryContact = Long.parseLong(json.get("secondaryContact").toString());
        }
        if(json.containsKey("address1") && json.get("address1") != null) {
            this.address1 = json.get("address1").toString();
        }
        if(json.containsKey("state") && json.get("state") != null ) {
            this.state = json.get("state").toString();
        }
        if(json.containsKey("role") && json.get("role") != null){
            this.role = json.get("role").toString();
        }
        if(json.containsKey("planType") && json.get("planType") != null){
            this.planType = json.get("planType").toString();
        }
        if(json.containsKey("branchOfficeId")){
            this.branchOfficeId = json.get("branchOfficeId").toString();
        }
        if(json.containsKey("accessibleModules")){
            this.accessibleModules = (Set<String>)json.get("accessibleModules");
        }
        if(json.containsKey("cancelCargoBookings")){
            this.cancelCargoBookings = (boolean) json.get("cancelCargoBookings");
        }
    }

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }

    public User(final String firstName, final String lastName, final String userName, final String password,
                boolean active, String role){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.active = active;
        this.role = role;
    }

    public User(final String firstName, final String lastName, final String userName, final String password,
                boolean active, boolean admin){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.active = active;
        this.admin = admin;
    }
    public User(final String firstName, final String lastName, final String userName, final String password,
                final boolean active,
               final boolean admin,
                final String email,
                final long contact,
                final String address1,
                final String address2,
                final String city,
                final String state,
                final String role,
                final String planType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.active = active;
        this.admin = admin;
        this.email = email;
        this.contact = contact;
        this.address1 = address1;
        this.address2 = address2;
        this.state = state;
        this.role = role;
        this.planType = planType;
    }
    public User(final String firstName, final String lastName, final String userName, final String password,
                final String email,
                final long contact,
                final String address1,
                final String address2,
                final String city,
                final String state,
                final String role,
                final String planType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.contact = contact;
        this.address1 = address1;
        this.address2 = address2;
        this.state = state;
        this.role = role;
        this.planType = planType;
    }
    public String getFullName(){
        return this.firstName + ", "+ this.lastName;
    }

    public boolean isBranchUser() {
       // if(getRole().equals(BranchU )
        return false;
    }
}
