package com.ojt.mockproject.dto.Account.Requests;

import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import com.ojt.mockproject.entity.Enum.AccountProviderEnum;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.entity.certificate_quiz.TookQuizResult;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDTO {
    private String name;

    private String headline;

    private String aboutMe;

    private String personalSiteLink;

    private String facebookLink;

    private String twitterLink;

    private String linkedinLink;

    private String youtubeLink;
}
