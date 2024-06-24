package com.example.tanialtech.field.data;
import android.os.Parcel;
import android.os.Parcelable;

public class FieldItem implements Parcelable {

    private int id;
    private String imageResource;
    private String namaLadang;
    private String kodeLadang;
    private String luasLadang;
    private String perkiraanMasaTanam;
    private String planting_period_convert;

    private boolean isSelected;


    public FieldItem(int id, String imageResource, String namaLadang, String kodeLadang, String luasLadang, String perkiraanMasaTanam, String plantingPeriodConvert, boolean isSelected) {
        this.id = id;
        this.imageResource = imageResource;
        this.namaLadang = namaLadang;
        this.kodeLadang = kodeLadang;
        this.luasLadang = luasLadang;
        this.perkiraanMasaTanam = perkiraanMasaTanam;
        this.planting_period_convert = plantingPeriodConvert;
        this.isSelected = isSelected;
    }

    protected FieldItem(Parcel in) {
        id = in.readInt();
        namaLadang = in.readString();
        kodeLadang = in.readString();
        luasLadang = in.readString();
        perkiraanMasaTanam = in.readString();
        imageResource = in.readString();
        planting_period_convert = in.readString();
    }

    public static final Creator<FieldItem> CREATOR = new Creator<FieldItem>() {
        @Override
        public FieldItem createFromParcel(Parcel in) {
            return new FieldItem(in);
        }

        @Override
        public FieldItem[] newArray(int size) {
            return new FieldItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getNamaLadang() {
        return namaLadang;
    }

    public void setNamaLadang(String namaLadang) {
        this.namaLadang = namaLadang;
    }

    public String getKodeLadang() {
        return kodeLadang;
    }

    public void setKodeLadang(String kodeLadang) {
        this.kodeLadang = kodeLadang;
    }

    public String getLuasLadang() {
        return luasLadang;
    }

    public void setLuasLadang(String luasLadang) {
        this.luasLadang = luasLadang;
    }

    public String getPerkiraanMasaTanam() {
        return perkiraanMasaTanam;
    }

    public void setPerkiraanMasaTanam(String perkiraanMasaTanam) {
        this.perkiraanMasaTanam = perkiraanMasaTanam;
    }

    public String getPlanting_period_convert() {
        return planting_period_convert;
    }

    public void setPlanting_period_convert(String planting_period_convert) {
        this.planting_period_convert = planting_period_convert;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(namaLadang);
        dest.writeString(kodeLadang);
        dest.writeString(luasLadang);
        dest.writeString(perkiraanMasaTanam);
        dest.writeString(imageResource);
        dest.writeString(planting_period_convert);
    }

    @Override
    public String toString() {
        return "FieldItem{" +
                "imageResource='" + imageResource + '\'' +
                ", namaLadang='" + namaLadang + '\'' +
                ", kodeLadang='" + kodeLadang + '\'' +
                ", luasLadang='" + luasLadang + '\'' +
                ", perkiraanMasaTanam='" + perkiraanMasaTanam + '\'' +
                '}';
    }


}
