package com.example.assistgoandroid.emergency;

/**
 * Le Jie Bennett
 * https://github.com/Abror96/CustomPhoneDialer
 */
public class Constants {

    public static String asString(int data) {
        String value;
        switch (data) {
            case 0:
                value = "NEW";
                break;
            case 1:
                value = "DIALING";
                break;
            case 2:
                value = "RINGING";
                break;
            case 3:
                value = "HOLDING";
                break;
            case 4:
                value = "ACTIVE";
                break;
            case 7:
                value = "DISCONNECTED";
                break;
            case 8:
                value = "SELECT_PHONE_ACCOUNT";
                break;
            case 9:
                value = "CONNECTING";
                break;
            case 10:
                value = "DISCONNECTING";
                break;
            default:
                value = "UNKNOWN";
                break;
        }
        return value;
    }

}
