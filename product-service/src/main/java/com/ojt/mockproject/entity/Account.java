package com.ojt.mockproject.entity;

import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import com.ojt.mockproject.entity.Enum.AccountProviderEnum;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.entity.certificate_quiz.TookQuizResult;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum gender;

    @Column(nullable = true)
    private String avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRoleEnum role;

    @Column(nullable = true)
    private String wishlist;

    @Column(name = "purchased_course", nullable = true)
    private String purchasedCourse;

    @Column(name = "subscribe", nullable = true)
    private String subscribe;

    @Column(name = "subscribers", nullable = true)
    private String subscribers;

    @Column(name = "headline", nullable = true)
    private String headline;

    @Column(name = "aboutMe", nullable = true)
    private String aboutMe;

    @Column(name = "personal_site", nullable = true)
    private String personalSiteLink;

    @Column(name = "facebook", nullable = true)
    private String facebookLink;

    @Column(name = "twitter", nullable = true)
    private String twitterLink;

    @Column(name = "linkedin", nullable = true)
    private String linkedinLink;

    @Column(name = "youtube", nullable = true)
    private String youtubeLink;

    @Column(name = "ownedCertificate", nullable = true)
    private String ownedCertificate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountProviderEnum provider;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "is_instructor_verify", nullable = false)
    private Boolean isInstructorVerify;

    @OneToMany(mappedBy = "account")
    private List<TookQuizResult> testResults;

    @Transient
    private String tokens;

    @Transient
    private String refreshToken;

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role.name()));
        return authorities;
    }

    @Transient
    @Override
    public String getUsername() {
        return this.email;
    }

    @Transient
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Transient
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Transient
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @OneToMany(mappedBy = "account")
    @Transient
    private List<Orderr> orders;

    @OneToMany(mappedBy = "account")
    @Transient
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "account")
    @Transient
    private List<Report> reports;

    @OneToOne(mappedBy = "account")
    @Transient
    private Wallet wallets;

    @OneToMany(mappedBy = "account")
    @Transient
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account")
    private List<ApiRequestLog> apiRequestLogs;

    @OneToMany(mappedBy = "account")
    @Transient
    private List<FeedbackWeb> feebbackWebs;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> sentMessages;

    @OneToMany(mappedBy = "recipient")
    private List<ChatMessage> receivedMessages;

    public Account(String name, String email, AccountRoleEnum role, AccountStatusEnum status, boolean isInstructorVerify) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.isDeleted = false;
        this.createAt = LocalDateTime.now();
        this.isInstructorVerify = isInstructorVerify;
    }
}