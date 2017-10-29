/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author renato
 */

public class SensorRegistrationInfo implements Serializable{
    
    private static final int R = 6371;
    private UserAddress userAddress;
    private double latitude;
    private double longitude;
    
    public SensorRegistrationInfo(UserAddress userAddress,double latitude,double longitude){
        this.userAddress=userAddress;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    
    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
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
    
    /* This method is Math calculation of Haversin formula for distance between two sensors */
    public double sensorDistance(SensorRegistrationInfo sensor){

        if(sensor == null){
            sensor = new SensorRegistrationInfo(null, 0, 0);
        }
        double lon, lat, a, c, d, lonSin, latSin;
        lon = sensor.longitude - this.longitude;
        lat = sensor.latitude - this.latitude;
        lonSin = Math.sin(lon/2);
        latSin = Math.sin(lat/2);
        a = Math.pow(latSin, 2) + Math.cos(this.latitude) * Math.cos(sensor.latitude) * Math.pow(lonSin,2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt((1-a)));
        d = R * c;
        return d;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.userAddress);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SensorRegistrationInfo other = (SensorRegistrationInfo) obj;
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude)) {
            return false;
        }
        if (!Objects.equals(this.userAddress, other.userAddress)) {
            return false;
        }
        return true;
    }
}
