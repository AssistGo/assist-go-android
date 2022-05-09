package com.example.assistgoandroid.Call.videoCallHelpers.notify.api;

import static com.example.assistgoandroid.Call.VideoCall.TWILIO_SDK_STARTER_SERVER_URL;

import com.example.assistgoandroid.Call.videoCallHelpers.notify.api.model.Binding;
import com.example.assistgoandroid.Call.videoCallHelpers.notify.api.model.Identity;
import com.example.assistgoandroid.Call.videoCallHelpers.notify.api.model.Notification;
import com.example.assistgoandroid.Call.videoCallHelpers.notify.api.model.Token;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class TwilioSDKStarterAPI {

    /**
     * Resources defined in the sdk-starter projects available in C#, Java, Node, PHP, Python, or
     * Ruby.
     *
     * <p>https://github.com/TwilioDevEd?q=sdk-starter
     */

    interface SDKStarterService {
        // Fetch an access token
        @GET("/token")
        Call<Token> fetchToken();
        // Fetch an access token with a specific identity
        @Headers("Content-Type: application/json")
        @POST("/token")
        Call<Token> fetchToken(@Body Identity identity);
        // Register this binding with Twilio Notify
        @Headers("Content-Type: application/json")
        @POST("/register")
        Call<Void> register(@Body Binding binding);
        // Send notifications to Twilio Notify registrants
        @Headers("Content-Type: application/json")
        @POST("/send-notification")
        Call<Void> sendNotification(@Body Notification notification);
    }

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .addInterceptor(logging.setLevel(HttpLoggingInterceptor.Level.BODY));

    private static SDKStarterService sdkStarterService =
            new Retrofit.Builder()
                    .baseUrl(TWILIO_SDK_STARTER_SERVER_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
                    .create(SDKStarterService.class);

    // Fetch a token with a specific identity
    public static Call<Token> fetchToken(final String identity) {
        if (identity == null) {
            return sdkStarterService.fetchToken();
        } else {
            return sdkStarterService.fetchToken(new Identity(identity));
        }
    }

    public static Call<Void> registerBinding(final Binding binding) {
        return sdkStarterService.register(binding);
    }

    public static Call<Void> notify(Notification notification) {
        return sdkStarterService.sendNotification(notification);
    }
}
