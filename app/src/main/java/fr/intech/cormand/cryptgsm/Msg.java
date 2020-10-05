package fr.intech.cormand.cryptgsm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

public class Msg implements Parcelable, Serializable {
    private String address;
    private String body;
    private String date_sent;
    private Boolean isUser;

    public Msg () {
        isUser = false;
    }

    public Msg (String address, String body, String date_sent) {
        this.address = address;
        this.body = body;
        this.date_sent = date_sent;
        this.isUser = false;
    }

    protected Msg(Parcel in) {
        address = in.readString();
        body = in.readString();
        date_sent = in.readString();
        byte tmpIsUser = in.readByte();
        isUser = tmpIsUser == 0 ? null : tmpIsUser == 1;
        Log.i("RETRIEVE",  "isUser: " + tmpIsUser + " | addr: " + address + " | date_sent: " + date_sent + " | body: " + body);
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean user) {
        isUser = user;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public String getDate_sent() {
        return date_sent;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate_sent(String date_sent) {
        this.date_sent = date_sent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(body);
        dest.writeString(date_sent);
        dest.writeBoolean(isUser);
    }

}
