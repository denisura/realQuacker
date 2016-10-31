package com.github.denisura.realquacker.data.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class AccountModel {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("id_str")
    @Expose
    public String idStr;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("screen_name")
    @Expose
    public String screenName;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("protected")
    @Expose
    public Boolean _protected;
    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;
    @SerializedName("friends_count")
    @Expose
    public Integer friendsCount;
    @SerializedName("listed_count")
    @Expose
    public Integer listedCount;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("favourites_count")
    @Expose
    public Integer favouritesCount;
    @SerializedName("geo_enabled")
    @Expose
    public Boolean geoEnabled;
    @SerializedName("verified")
    @Expose
    public Boolean verified;
    @SerializedName("statuses_count")
    @Expose
    public Integer statusesCount;
    @SerializedName("lang")
    @Expose
    public String lang;
    @SerializedName("contributors_enabled")
    @Expose
    public Boolean contributorsEnabled;
    @SerializedName("is_translator")
    @Expose
    public Boolean isTranslator;
    @SerializedName("is_translation_enabled")
    @Expose
    public Boolean isTranslationEnabled;
    @SerializedName("profile_background_color")
    @Expose
    public String profileBackgroundColor;
    @SerializedName("profile_background_image_url")
    @Expose
    public String profileBackgroundImageUrl;
    @SerializedName("profile_background_image_url_https")
    @Expose
    public String profileBackgroundImageUrlHttps;
    @SerializedName("profile_background_tile")
    @Expose
    public Boolean profileBackgroundTile;
    @SerializedName("profile_image_url")
    @Expose
    public String profileImageUrl;
    @SerializedName("profile_image_url_https")
    @Expose
    public String profileImageUrlHttps;
    @SerializedName("profile_link_color")
    @Expose
    public String profileLinkColor;
    @SerializedName("profile_sidebar_border_color")
    @Expose
    public String profileSidebarBorderColor;
    @SerializedName("profile_sidebar_fill_color")
    @Expose
    public String profileSidebarFillColor;
    @SerializedName("profile_text_color")
    @Expose
    public String profileTextColor;
    @SerializedName("profile_use_background_image")
    @Expose
    public Boolean profileUseBackgroundImage;
    @SerializedName("has_extended_profile")
    @Expose
    public Boolean hasExtendedProfile;
    @SerializedName("default_profile")
    @Expose
    public Boolean defaultProfile;
    @SerializedName("default_profile_image")
    @Expose
    public Boolean defaultProfileImage;
    @SerializedName("following")
    @Expose
    public Boolean following;
    @SerializedName("follow_request_sent")
    @Expose
    public Boolean followRequestSent;
    @SerializedName("notifications")
    @Expose
    public Boolean notifications;
    @SerializedName("translator_type")
    @Expose
    public String translatorType;

    public String token;
    public String tokenSecret;
}