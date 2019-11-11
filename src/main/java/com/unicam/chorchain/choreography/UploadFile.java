package com.unicam.chorchain.choreography;

import lombok.Data;

@Data
public class UploadFile {
    private String data;
    private String name;
    private String description;
    private String extension;

    public String getFilename(){
        return this.name.concat(this.extension);
    }
}

