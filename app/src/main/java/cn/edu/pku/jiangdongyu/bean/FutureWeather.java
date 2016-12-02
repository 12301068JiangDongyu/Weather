package cn.edu.pku.jiangdongyu.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jiangdongyu on 2016/11/30.
 */
public class FutureWeather implements Serializable{
    private String fengli;
    private String date;
    private String wendu;
    private String type;

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public  String toString(){
        return "FutureWeather{"+
                "wendu='" + wendu + '\'' +
                ",fengli='" + fengli + '\'' +
                ",date='" + date + '\'' +
                ",type='" + type + '\'' +
                '}';
    }
}
