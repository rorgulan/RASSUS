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

public class UserAddress implements Serializable {
    
    private String IPaddress;
    private int port;
    
    public UserAddress(){}
    
    public UserAddress(String IPaddress, int port){
        this.IPaddress=IPaddress;
        this.port=port;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.IPaddress);
        hash = 67 * hash + this.port;
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
        final UserAddress other = (UserAddress) obj;
        if (this.port != other.port) {
            return false;
        }
        if (!Objects.equals(this.IPaddress, other.IPaddress)) {
            return false;
        }
        return true;
    }
    
    public String getIPaddress() {
        return IPaddress;
    }

    public void setIPaddress(String IPaddress) {
        this.IPaddress = IPaddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
