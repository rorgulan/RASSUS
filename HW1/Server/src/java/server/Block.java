/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Arrays;

/**
 *
 * @author renato
 */

public class Block {
    
    private BlockchainData data;
    private int prevBlockHash;
    
    public Block(BlockchainData data, int prevBlockHash){
        this.data=data;
        this.prevBlockHash=prevBlockHash;
    }

    public BlockchainData getData() {
        return data;
    }

    public void setData(BlockchainData data) {
        this.data = data;
    }

    public int getPrevBlockHash() {
        return prevBlockHash;
    }

    public void setPrevBlockHash(int prevBlockHash) {
        this.prevBlockHash = prevBlockHash;
    }

}


