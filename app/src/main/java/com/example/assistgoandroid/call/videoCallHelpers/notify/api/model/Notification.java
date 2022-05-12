package com.example.assistgoandroid.call.videoCallHelpers.notify.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * The Notification model defined to send notifications with Twilio Notify
 *
 * <p>https://www.twilio.com/docs/api/notify/rest/notifications
 */
public class Notification {
    @SerializedName("Title")
    public final String title;

    @SerializedName("Body")
    public final String body;

    @SerializedName("Data")
    public final Map<String, String> data;

    @SerializedName("Tag")
    public final List<String> tag;

    public Notification(String title, String body, Map<String, String> data, List<String> tag) {
        this.title = title;
        this.body = body;
        this.data = data;
        this.tag = tag;
    }
}
