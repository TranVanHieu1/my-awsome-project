package com.ojt.mockproject.dto.Account.Responses;



import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class InstructorResponseDTO {

    private String name;
    private AccountRoleEnum role;
    private Integer numberCourse;
    private String avatar;
    private String aboutMe;

    public InstructorResponseDTO(String name, AccountRoleEnum role, Integer numberCourse, String avatar) {
        this.name = name;
        this.role = role;
        this.numberCourse = numberCourse;
        this.avatar = avatar;
    }

    public InstructorResponseDTO(String name, AccountRoleEnum role, Integer numberCourse, String avatar, String aboutMe) {
        this.name = name;
        this.role = role;
        this.numberCourse = numberCourse;
        this.avatar = avatar;
        this.aboutMe = aboutMe;
    }

    ////////////////////
}
