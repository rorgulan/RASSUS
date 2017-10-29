/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Objects;

/**
 *
 * @author renato
 */
public class BlockchainData {
    
    private long timeAdd;
    private Measurement measurement;
    
    public BlockchainData(long timeAdd, Measurement measurement){
        this.timeAdd=timeAdd;
        this.measurement=measurement;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.timeAdd ^ (this.timeAdd >>> 32));
        hash = 97 * hash + Objects.hashCode(this.measurement);
        return hash;
    }

    public long getTimeAdd() {
        return timeAdd;
    }

    public Measurement getMeasurement() {
        return measurement;
    }
    
    
}
