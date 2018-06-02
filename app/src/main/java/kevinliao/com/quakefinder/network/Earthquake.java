package kevinliao.com.quakefinder.network;

import java.util.Objects;

public class Earthquake {

    String id;
    String place;
    long time;
    String url;
    double latitude;
    double longitude;

    public Earthquake(String id, String place, long time, String url, double latitude, double longitude) {
        this.id = id;
        this.place = place;
        this.time = time;
        this.url = url;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getTimeStamp() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Earthquake that = (Earthquake) o;
        return time == that.time &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(place, that.place) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, place, time, url, latitude, longitude);
    }

    public static class Builder {
        String id;
        String place;
        long time;
        String url;
        double latitude;
        double longitude;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPlace(String place) {
            this.place = place;
            return this;
        }

        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Earthquake create(){
            return new Earthquake(id, place, time, url, latitude, longitude);
        }
    }
}
