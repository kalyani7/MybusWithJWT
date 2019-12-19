package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 *
 */
@ToString
@ApiModel(value = EmailNotificationEvent.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class EmailNotificationEvent extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="notificationEvent";

    private String content;
    private boolean sent;

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }


}
