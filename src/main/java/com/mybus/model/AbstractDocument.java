package com.mybus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybus.dao.RequiredFieldValidator;
import com.mybus.exception.BadRequestException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;


@EqualsAndHashCode(of = { "_id" })
@ApiModel(value = "AbstractDocument")
@Getter
@Setter
public abstract class AbstractDocument {

    public static final String KEY_ID = "_id";

    public static final String KEY_ATTRIBUTES = "attrs";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    @Id
    @Field(KEY_ID)
    @ApiModelProperty(value = "ID of the Object")
    private String id;

    @ApiModelProperty(value = "OperatorId")
    private String operatorId;

    @CreatedDate
    @Field(KEY_CREATED_AT)
    @ApiModelProperty(value = "Time at which Object Was Created")
    private DateTime createdAt;

    @Version
    private Long version;

    @LastModifiedDate
    @Field(KEY_UPDATED_AT)
    @ApiModelProperty(value = "Time at which Object Was Last Updated")
    private DateTime updatedAt;

    @CreatedBy
    @ApiModelProperty(value = "User who created the object")
    private String createdBy;

    @LastModifiedBy
    @ApiModelProperty(value = "User who updated the object")
    private String updatedBy;


    @ApiModelProperty(value = "Additional Fields on the Object")
    @JsonProperty(KEY_ATTRIBUTES)
    @Field(KEY_ATTRIBUTES)
    private Map<String, String> attributes = new HashMap<>();

    public void merge(final Object copy) throws Exception {
        BeanUtils.copyProperties(copy, this, getNullPropertyNames(copy));
    }

    public void merge(final Object copy, boolean ignoreNullValues) throws Exception {
        if(ignoreNullValues) {
            BeanUtils.copyProperties(copy, this, getNullPropertyNames(copy));
        } else {
            BeanUtils.copyProperties(copy, this);
        }
    }

    private String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * Method for running required validations on model objects
     */
    public void validate(){
        List<String> errors = RequiredFieldValidator.validateModel(this, this.getClass());
        if(!errors.isEmpty()) {
            throw new BadRequestException(String.join("; ", errors));
        }
    }
}
