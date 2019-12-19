package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

@NoArgsConstructor
@ToString
@ApiModel(value = "FileUpload")
@Getter
@Setter
@EqualsAndHashCode(of={"id"})
public class FileUpload extends AbstractDocument {
    private String refId;
    private String refType;
    private String bucketName;
    private String fileName;
    private String displayName;

    public FileUpload(String id, String refType, String bucketName, String fileName,
                      String displayName){
        this.refId = id;
        this.refType = refType;
        this.bucketName = bucketName;
        this.fileName = fileName;
        this.displayName = displayName;
    }

}
