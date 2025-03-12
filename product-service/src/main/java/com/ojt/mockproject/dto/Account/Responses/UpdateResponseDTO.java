package com.ojt.mockproject.dto.Account.Responses;

import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateResponseDTO {
    private String name;

    private String headline;

    private String aboutMe;

    private String personalSiteLink;

    private String facebookLink;

    private String twitterLink;

    private String linkedinLink;

    private String youtubeLink;
}
