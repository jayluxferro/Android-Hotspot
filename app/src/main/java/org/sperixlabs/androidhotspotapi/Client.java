package org.sperixlabs.androidhotspotapi;

/**
 * Created by Jay Lux Ferro on 12/19/17.
 */

public class Client {
    private String hostname, ip, mac;

    public Client(){
        //default constructor
    }

    public Client(String hostname, String ip, String mac){
        this.hostname = hostname;
        this.ip = ip;
        this.mac = mac;
    }

    public String getHostname(){
        return hostname;
    }

    public String getIp(){
        return ip;
    }

    public String getMac(){
        return mac;
    }

}
