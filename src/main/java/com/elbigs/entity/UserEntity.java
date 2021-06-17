package com.elbigs.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity extends BaseEntity implements UserDetails {
    private long id;
    private String userId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String email;
    private String phone;
    private String tel;
    private String cityCode;
    private boolean masterYn;
    private String code;
    private String companyName;
    private String serviceKey;
    private long areaX;
    private long areaY;
    private String mapClientId;
    private String mapClientSecret;
    private int defaultZoom;
    private int maxZoom;
    private int minZoom;
    private int poiLevelH;
    private int poiLevelM;
    private int poiLevelL;

    private String rememberToken;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime deletedAt;

    private int kakaoZoomLevel;
    private String companyDomain;
    private boolean isAd;
    protected int naviSpeed;

    private List<FloorEntity> floors;
    private boolean userIdConfirm;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.userId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
