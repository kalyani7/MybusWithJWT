package com.mybus.message.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpForm {
    @NotBlank
    @Size(min = 3, max = 50)
    private String fullName;

    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    @NotBlank
    @Size(max = 10)
    private String phoneNumber;

    @NotBlank
    @Size(min=3, max = 60)
    private String userName;
    

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;


}