package br.com.gabrieldani.maps.model;

public class RestrictedRegion extends Region{
    private Region mainRegion;
    private boolean restricted;

    public RestrictedRegion(Region mainRegion, double latitude, double longitude) {
        super("RestrictedRegion",latitude, longitude);
        this.mainRegion = mainRegion;
        this.restricted = true;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public Region getMainRegion() {
        return mainRegion;
    }

    public void setMainRegion(Region mainRegion) { this.mainRegion = mainRegion; }
}
