package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebRequest;
import com.ojt.mockproject.dto.FeedbackWeb.FeedbackWebResponse;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.FeedbackWeb;
import com.ojt.mockproject.exceptionhandler.BadRequest;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.repository.FeedbackWebRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackWebService {
    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private UploadFileUtils uploadFileUtils;

    @Autowired
    private FeedbackWebRepository feedbackWebRepository;

    public FeedbackWebResponse addFeedbackWeb(FeedbackWebRequest feedbackWebRequest){
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }
        if(feedbackWebRequest.getDescription().isEmpty()){
            throw new BadRequest("Description is required!");
        }
        FeedbackWeb feedbackWeb = new FeedbackWeb();
        feedbackWeb.setAccount(account);
        feedbackWeb.setCreateAt(LocalDateTime.now());
        feedbackWeb.setEmail(account.getEmail());
        feedbackWeb.setDescription(feedbackWebRequest.getDescription());
        feedbackWeb.setImage(feedbackWebRequest.getScreenshot());
        try{
            feedbackWebRepository.save(feedbackWeb);
            return new FeedbackWebResponse(feedbackWeb.getId(), feedbackWeb.getDescription(), feedbackWeb.getImage());
        }catch (Exception ex){
            throw new BadRequest("Can not feedback!");
        }
    }


    public FeedbackWebResponse getFeedbackWeb() {
        //get current user
        Account account = null;
        String imgUrl;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }
        FeedbackWeb feedbackWeb = feedbackWebRepository.findFeedbackWebByAccount(account);

        try{
            imgUrl = uploadFileUtils.getSignedImageUrl(feedbackWeb.getImage());
        }catch (Exception ex){
            throw  new RuntimeException("There is no image!");
        }

        return new FeedbackWebResponse(feedbackWeb.getId(), feedbackWeb.getDescription(), imgUrl);

    }


}
