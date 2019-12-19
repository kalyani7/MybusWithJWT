package com.mybus.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentUpload extends AbstractDocument {

    private String fileName;
    private String description;
    private String filePath;
    private String displayName;
}
