/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author renato
 */
@WebService(serviceName = "Service")
public class Service {

    // ArrayList<Block> blockchain = new ArrayList<>();
    private final Map<String, SensorRegistrationInfo> mapOfRegisteredSensors = new HashMap<>();
    private static Blockchain blockChain = new Blockchain();

    
    
    /**
     * Web service operation
     * @param username
     * @param latitude
     * @param longitude
     * @param IPaddress
     * @param PORT
     * @return 
     */
    
    @WebMethod(operationName = "register")
    public boolean register(@WebParam(name = "username") String username, 
                            @WebParam(name = "latitude") double latitude, 
                            @WebParam(name = "longitude") double longitude, 
                            @WebParam(name = "IPaddress") String IPaddress, 
                            @WebParam(name = "PORT") int PORT) {
        
        if(mapOfRegisteredSensors.containsKey(username)){
            return false;
        }
        UserAddress sensorForRegistration = new UserAddress(IPaddress,PORT);
        SensorRegistrationInfo sensorInfo = new SensorRegistrationInfo(sensorForRegistration, latitude, longitude);
        mapOfRegisteredSensors.put(username, sensorInfo);
        return true;
    }

    /**
     * Web service operation
     * @param username
     * @return 
     */
    @WebMethod(operationName = "searchNeighbour")
    public UserAddress searchNeighbour(@WebParam(name = "username") String username) {
      
        
        if(username == null){
            return null;
        }
        
        SensorRegistrationInfo sensorInfo = mapOfRegisteredSensors.get(username);
       if(sensorInfo == null){
         //   return null;
        }

        SensorRegistrationInfo closest;
        closest = mapOfRegisteredSensors.values().parallelStream().unordered().filter(x -> x!=sensorInfo).min((x1,x2) -> Double.compare(sensorInfo.sensorDistance(x1), sensorInfo.sensorDistance(x2))).orElse(sensorInfo);
        
        if(sensorInfo == closest || closest == null){
            return null;
        }

        return closest.getUserAddress();
    }

    /**
     * Web service operation
     * @param username
     * @param parametar
     * @param averageValue
     * @return 
     */
    @WebMethod(operationName = "storeMeasurement")
    public boolean storeMeasurement(@WebParam(name = "username") String username,
                                    @WebParam(name = "parametar") String parametar, 
                                    @WebParam(name = "averageValue") float averageValue) {
               System.out.println(blockChain.GetBlock(0));
        return blockChain.append(username, parametar, averageValue);
    }
    
}
