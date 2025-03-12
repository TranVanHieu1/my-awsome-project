package com.ojt.mockproject.utils;

import com.ojt.mockproject.dto.Account.Requests.UpdateRequestDTO;
import com.ojt.mockproject.entity.Account;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UpdateUtils {

    public static Account updateAccount(UpdateRequestDTO updateRequestDTO, Account account) {
        if (StringUtils.hasText(updateRequestDTO.getName())) {
            account.setName(updateRequestDTO.getName());
        }
        if (StringUtils.hasText(updateRequestDTO.getHeadline())) {
            account.setHeadline(updateRequestDTO.getHeadline());
        }
        if (StringUtils.hasText(updateRequestDTO.getAboutMe())) {
            account.setAboutMe(updateRequestDTO.getAboutMe());
        }
        if (StringUtils.hasText(updateRequestDTO.getPersonalSiteLink())) {
            account.setPersonalSiteLink(updateRequestDTO.getPersonalSiteLink());
        }
        if (StringUtils.hasText(updateRequestDTO.getFacebookLink())) {
            account.setFacebookLink(updateRequestDTO.getFacebookLink());
        }
        if (StringUtils.hasText(updateRequestDTO.getTwitterLink())) {
            account.setTwitterLink(updateRequestDTO.getTwitterLink());
        }
        if (StringUtils.hasText(updateRequestDTO.getLinkedinLink())) {
            account.setLinkedinLink(updateRequestDTO.getLinkedinLink());
        }
        if (StringUtils.hasText(updateRequestDTO.getYoutubeLink())) {
            account.setYoutubeLink(updateRequestDTO.getYoutubeLink());
        }

        return account;
    }

}
