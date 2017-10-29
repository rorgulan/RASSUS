/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;

/**
 *
 * @author renato
 */
public class Blockchain {

    private final ArrayList<Block> listBlocks;

    public Blockchain() {
        listBlocks = new ArrayList<>();
    }

    public Block PeekLast() {
        return listBlocks.get(listBlocks.size() - 1);
    }

    public Block GetBlock(int i) {
        return listBlocks.get(i);
    }

    public boolean append(String username, String parameter, float averageValue) {
        Measurement measurement = new Measurement(username, parameter, averageValue);
        BlockchainData data = new BlockchainData(System.currentTimeMillis(), measurement);
        int prevBlockHash = 0;
        if (listBlocks.size() > 0) {
            Block lastBlock = listBlocks.get(listBlocks.size() - 1);
            prevBlockHash = lastBlock.getData().hashCode();
        }
        listBlocks.add(new Block(data, prevBlockHash));
        return true;

    }

}
