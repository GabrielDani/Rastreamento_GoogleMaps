package br.com.gabrieldani.maps.model;

public class SubRegion extends Region {
    private Region mainRegion;

    public SubRegion(Region mainRegion, double latitude, double longitude) {
        super("SubRegion", latitude, longitude);
        this.mainRegion = mainRegion;
    }

    public Region getMainRegion() {
        return mainRegion;
    }

    public void setMainRegion(Region mainRegion) {
        this.mainRegion = mainRegion;
    }
}

