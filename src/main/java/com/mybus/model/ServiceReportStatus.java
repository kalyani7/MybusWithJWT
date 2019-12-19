package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceReportStatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceReportStatus extends AbstractDocument {
    @Indexed(unique = true)
    private Date reportDate;
    private ReportDownloadStatus status;
}
