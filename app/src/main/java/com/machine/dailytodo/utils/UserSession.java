package com.machine.dailytodo.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class UserSession implements Parcelable {
    public static UserSession ourInstance = new UserSession();
    private String amount;
    private String payId;
    private String txnId;
    private String mode;
    private String GST;
    private String page;
    private String name;
    private String rebateAmount;
    private String actualAmount;


    private UserSession() {

    }

    protected UserSession(Parcel in) {
        amount = in.readString();
        payId = in.readString();
        txnId = in.readString();
        mode = in.readString();
        GST = in.readString();
        page = in.readString();
        name = in.readString();
        rebateAmount = in.readString();
        actualAmount = in.readString();




    }

    public static final Creator<UserSession> CREATOR = new Creator<UserSession>() {
        @Override
        public UserSession createFromParcel(Parcel in) {
            return new UserSession(in);
        }

        @Override
        public UserSession[] newArray(int size) {
            return new UserSession[size];
        }
    };

    public static UserSession getInstance() {
        return ourInstance;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(payId);
        dest.writeString(txnId);
        dest.writeString(mode);
        dest.writeString(GST);
        dest.writeString(page);
        dest.writeString(name);
        dest.writeString(rebateAmount);
        dest.writeString(actualAmount);

    }
}
