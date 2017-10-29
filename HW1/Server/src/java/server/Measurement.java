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
public class Measurement implements Serializable{
    
    private String username;
    private String parameter;
    private float averageValue;
    
    public Measurement(String username, String parameter, float averageValue){
        this.username=username;
        this.parameter=parameter;
        this.averageValue=averageValue;
    }    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(float averageValue) {
        this.averageValue = averageValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.username);
        hash = 37 * hash + Objects.hashCode(this.parameter);
        hash = 37 * hash + Float.floatToIntBits(this.averageValue);
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
        final Measurement other = (Measurement) obj;
        if (Float.floatToIntBits(this.averageValue) != Float.floatToIntBits(other.averageValue)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.parameter, other.parameter)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
