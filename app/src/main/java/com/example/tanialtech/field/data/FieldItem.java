package com.example.tanialtech.field.data;

public class FieldItem {

    private String imageResource;
    private String namaLadang;
    private String kodeLadang;

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



    private String luasLadang;

    public String getPerkiraanMasaTanam() {
        return perkiraanMasaTanam;
    }

    public void setPerkiraanMasaTanam(String perkiraanMasaTanam) {
        this.perkiraanMasaTanam = perkiraanMasaTanam;
    }

    private String perkiraanMasaTanam;

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

    public FieldItem(String imageResource, String namaLadang, String kodeLadang, String luasLadang, String perkiraanMasaTanam) {
        this.imageResource = imageResource;
        this.namaLadang = namaLadang;
        this.kodeLadang = kodeLadang;
        this.luasLadang = luasLadang;
        this.perkiraanMasaTanam = perkiraanMasaTanam;
    }
}
