package com.mybus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybus.annotations.RequiresValue;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@ApiModel(value = CargoBooking.COLLECTION_NAME)
@NoArgsConstructor
@Getter
@Setter
public class CargoBooking extends AbstractDocument implements AttributesDocument {
    public static final String COLLECTION_NAME="cargoBooking";
    public static final String DISPATCH_DATE="dispatchDate";
    public static final String SHIPMENT_NUMBER="shipmentNumber";
    public static final String DELIVERED_BY = "deliveredBy";
    public static final String DELIVERED_BY_USERID = "deliveredByUserId";
    public static final String DELIVERED_ON = "deliveredOn";

    private String forUser;

    private boolean due;

    @RequiresValue
    @Field(SHIPMENT_NUMBER)
    @Indexed(unique = true)
    private String shipmentNumber;

    @RequiresValue
    @Indexed(unique = true)
    private String fromBranchId;

    @RequiresValue
    @Indexed(unique = true)
    private String toBranchId;

    private boolean copySenderDetails;

    @RequiresValue
    private CargoTransitStatus cargoTransitStatus = CargoTransitStatus.READYFORSHIPMENT;

    @RequiresValue
    private String paymentType;

    private String fromEmail;

    @Indexed
    @RequiresValue
    private Long fromContact;
    private String fromName;

    private String wayBillNo;
    private String tinNumber;
    private String toEmail;

    @Indexed
    @RequiresValue
    private Long toContact;
    private String toName;

    private long loadingCharge = 0;
    private long unloadingCharge = 0;
    private long otherCharge = 0;


    @RequiresValue
    private long totalCharge;

    @Field(value = DISPATCH_DATE)
    @RequiresValue
    private Date dispatchDate;

    private String loadedBy;

    private List<CargoBookingItem> items;

    private int totalArticles;
    @JsonIgnore
    public String getDescription() {
        if(items == null || items.size() == 0){
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            items.stream().forEach(i -> {
                builder.append(i.getDescription());
            });
            return builder.toString();
        }
    }

    private String remarks;

    private String supplierId;

    private Date paidOn;
    private String  paidBy;
    private boolean canceled;
    private Date canceledOn;
    private String canceldBy;
    private String cancelledBy;

    private String vehicleId;

    private Date deliveredOn;
    private String deliveredBy;
    private String deliveredByUserId;
    private String deliveryNotes;
    private String reviewComment;
    private List<String> messages = new ArrayList<>();
    private String cancellationReason;

    public String getRecentMessage(){
        if(messages.size() > 0) {
            return messages.get(messages.size()-1);
        }
        return null;
    }

    @Override
    public boolean containsKey(String attributeName) {
        return false;
    }
}

