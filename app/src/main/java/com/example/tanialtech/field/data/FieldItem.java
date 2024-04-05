package com.example.tanialtech.field.data;

public class FieldItem {

    private int imageResource;
    private String namaLadang;
    private String kodeLadang;

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
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

    public FieldItem(int imageResource, String namaLadang, String kodeLadang, String luasLadang, String perkiraanMasaTanam) {
        this.imageResource = imageResource;
        this.namaLadang = namaLadang;
        this.kodeLadang = kodeLadang;
        this.luasLadang = luasLadang;
        this.perkiraanMasaTanam = perkiraanMasaTanam;
    }
}
