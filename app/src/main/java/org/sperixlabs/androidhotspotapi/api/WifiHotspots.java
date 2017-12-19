package org.sperixlabs.androidhotspotapi.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay Lux Ferro on 12/19/17.
 */

public class WifiHotspots {
    WifiManager mWifiManager;
    WifiInfo mWifiInfo ;
    Context mContext;
    List<ScanResult> mResults;

    public static boolean isConnectToHotSpotRunning=false;

    public  WifiHotspots(Context c) {
        mContext = c;
        mWifiManager=(WifiManager)  mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * Method for Connecting  to WiFi Network (hotspot)
     *
     * @param netSSID of WiFi Network (hotspot)
     * @param netPass  put password or  "" for open network
     *
     * return true if connected to hotspot successfully
     */
    public boolean connectToHotspot(String netSSID, String netPass) {

        isConnectToHotSpotRunning= true;
        WifiConfiguration wifiConf = new WifiConfiguration();
        List<ScanResult> scanResultList=mWifiManager.getScanResults();

        if(mWifiManager.isWifiEnabled()){

            for (ScanResult result : scanResultList) {

                if (result.SSID.equals(netSSID)) {

                    removeWifiNetwork(result.SSID);
                    String mode = getSecurityMode(result);

                    if (mode.equalsIgnoreCase("OPEN")) {

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        int res = mWifiManager.addNetwork(wifiConf);
                        mWifiManager.disconnect();
                        mWifiManager.enableNetwork(res, true);
                        mWifiManager.reconnect();
                        mWifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    } else if (mode.equalsIgnoreCase("WEP")) {

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.wepKeys[0] = "\"" + netPass + "\"";
                        wifiConf.wepTxKeyIndex = 0;
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        int res = mWifiManager.addNetwork(wifiConf);
                        mWifiManager.disconnect();
                        mWifiManager.enableNetwork(res, true);
                        mWifiManager.reconnect();
                        mWifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    }else{

                        wifiConf.SSID = "\"" + netSSID + "\"";
                        wifiConf.preSharedKey = "\"" + netPass + "\"";
                        wifiConf.hiddenSSID = true;
                        wifiConf.status = WifiConfiguration.Status.ENABLED;
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        int res = mWifiManager.addNetwork(wifiConf);
                        mWifiManager.disconnect();
                        mWifiManager.enableNetwork(res, true);
                        mWifiManager.reconnect();
                        mWifiManager.saveConfiguration();
                        mWifiManager.setWifiEnabled(true);
                        isConnectToHotSpotRunning=false;
                        return true;

                    }
                }
            }
        }
        isConnectToHotSpotRunning=false;
        return false;
    }
    /**
     * Check if The Device Is Connected to Hotspot using wifi
     *
     * @return true if device connect to Hotspot
     */
    public boolean  isConnectedToAP(){
        ConnectivityManager connectivity = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Method to Get hotspot Max Level of all Hotspots Around you
     *
     * @return a highest level hotspot
     */
    public ScanResult getHotspotMaxLevel(){
        List<ScanResult> hotspotList=mWifiManager.getScanResults();
        if (hotspotList != null) {
            final int size = hotspotList.size();
            if (size == 0){
                return null;
            } else {
                ScanResult maxLevel = hotspotList.get(0);
                for (ScanResult result : hotspotList) {
                    if (WifiManager.compareSignalLevel(maxLevel.level,
                            result.level) < 0) {
                        maxLevel = result;
                    }
                }
                return maxLevel;
            }
        }else{
            return null;
        }
    }
    /**
     * Method to Get hotspot Max Level of all Hotspots in hotspotList list
     *
     * @param  hotspotList list of Hotspots
     * @return a highest level hotspot
     */
    public ScanResult getHotspotMaxLevel(List<ScanResult> hotspotList){

        if (hotspotList != null) {
            final int size = hotspotList.size();
            if (size == 0){
                return null;
            } else {
                ScanResult maxSignal = hotspotList.get(0);

                for (ScanResult result : hotspotList) {
                    if (WifiManager.compareSignalLevel(maxSignal.level,
                            result.level) < 0) {
                        maxSignal = result;
                    }
                }
                return maxSignal;
            }
        }else{
            return null;
        }

    }
    /**
     * sort All  Hotspots Around you By Level
     *
     * @return sorted hotspots List
     */
    public List<ScanResult> sortHotspotsByLevel(){
        List<ScanResult> hotspotList=mWifiManager.getScanResults();
        List<ScanResult> sorthotspotsList=new ArrayList<ScanResult>();
        ScanResult result;
        while(!hotspotList.isEmpty()){
            result=getHotspotMaxLevel(hotspotList);
            sorthotspotsList.add(result);
            hotspotList.remove(result);
        }

        return sorthotspotsList;
    }
    /**
     * sort Hotspots in hotspotList By Level
     *
     * @return sorted hotspots List
     */
    public List<ScanResult> sortHotspotsByLevel(List<ScanResult> hotspotList){
        List<ScanResult> hotspotList2=hotspotList;
        List<ScanResult> sorthotspotsList=new ArrayList<ScanResult>();
        ScanResult result;
        while(!hotspotList2.isEmpty()){
            result=getHotspotMaxLevel(hotspotList2);
            sorthotspotsList.add(result);
            hotspotList2.remove(result);
        }
        return sorthotspotsList;
    }
    /**
     * Method to Get  List of  WIFI Networks (hotspots) Around you
     *
     * @return List  of networks (hotspots)
     */
    public List<ScanResult> getHotspotsList(){

        if(mWifiManager.isWifiEnabled()) {

            if(mWifiManager.startScan()){
                return mWifiManager.getScanResults();
            }

        }
        return null;
    }


    public void scanNetworks() {
        boolean scan = mWifiManager.startScan();

        if(scan) {
            mResults = mWifiManager.getScanResults();

        } else
            switch(mWifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_DISABLING:
                    Toast.makeText(mContext,"wifi disabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(mContext, "wifi disabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Toast.makeText(mContext, "wifi enabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(mContext, "wifi enabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Toast.makeText(mContext,"wifi unknown state", Toast.LENGTH_LONG).show();
                    break;
            }

    }

    /**
     *
     */


    /**
     * Get current Access Point status
     */

    public boolean getHotspotStatus(){
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for (Method mMethod : mMethods) {
            if (mMethod.getName().equals("setWifiApEnabled")) {
                try {
                   int appState = (Integer) mMethod.invoke(mWifiManager);
                   if(appState == 13){
                       return true;
                   }else {
                       return false;
                   }
                } catch (Exception ex) {
                }
                break;
            }
        }
        return false;
    }
    /**
     * Method to turn ON/OFF a  Access Point
     *
     * @param enable Put true if you want to start  Access Point
     * @return true if AP is started
     */
    public boolean startHotSpot(boolean enable) {
        mWifiManager.setWifiEnabled(false);
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for (Method mMethod : mMethods) {
            if (mMethod.getName().equals("setWifiApEnabled")) {
                try {
                    mMethod.invoke(mWifiManager, null, enable);
                    return true;
                } catch (Exception ex) {
                }
                break;
            }
        }
        return false;
    }
    /**
     * Method to Change SSID and Password of Device Access Point and Start HotSpot
     *
     * SSID a new SSID of your Access Point
     *  password a new password you want for your Access Point
     **/
    public boolean setAndStartHotSpot(boolean enable, String SSID)
    {
        //For simple implementation I am creating the open hotspot.
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for(Method mMethod: mMethods){
            {
                if(mMethod.getName().equals("setWifiApEnabled")) {
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = SSID;
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    try{
                        mMethod.invoke(mWifiManager, netConfig,true);
                    }catch(Exception e)
                    {
                        return false;
                    }
                    startHotSpot(enable);
                }
            }
        }
        return enable;
    }
    /**
     * Method to Change SSID and Password of Device Access Point
     *
     * @param SSID a new SSID of your Access Point
     * @param passWord a new password you want for your Access Point
     */
    public boolean setHotSpot(String SSID,String passWord){

        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();

        for(Method mMethod: mMethods){

            if(mMethod.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                if(passWord==""){
                    netConfig.SSID = SSID;
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }else{
                    netConfig.SSID = SSID ;
                    netConfig.preSharedKey = passWord;
                    netConfig.hiddenSSID = true;
                    netConfig.status = WifiConfiguration.Status.ENABLED;
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                }
                try {
                    mMethod.invoke(mWifiManager, netConfig,true);
                    mWifiManager.saveConfiguration();
                    return true;

                } catch (Exception e) {

                }
            }
        }
        return false;
    }
    /**
     * @return true if Wifi Access Point Enabled
     */
    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean)method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * shred all  Configured wifi Networks
     */
    public boolean shredAllWifi(){
        Context context =  mContext.getApplicationContext();
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if( mWifiInfo != null ){
            for(WifiConfiguration conn:  mWifiManager.getConfiguredNetworks()){
                mWifiManager.removeNetwork(conn.networkId);
            }

            mWifiManager.saveConfiguration();
            return true;
        }
        return false;
    }
    /**
     * This gets a list of the wifi profiles from the system and returns them.
     * @return List<WifiConfigurationg> : a list of all the profile names.
     */
    public ArrayList<WifiConfiguration> getProfiles(){
        ArrayList<WifiConfiguration> profileList =new ArrayList<WifiConfiguration>();
        if( mWifiInfo != null ){
            for(WifiConfiguration conn: mWifiManager.getConfiguredNetworks()){
                profileList.add(conn);
            }
        }
        return profileList;
    }

    /**
     *shred  Configured wifi Network By SSID
     * @param ssid of wifi Network
     */
    public void removeWifiNetwork(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (config.SSID.contains(ssid)) {
                    mWifiManager.disableNetwork(config.networkId);
                    mWifiManager.removeNetwork(config.networkId);
                }
            }
        }
        mWifiManager.saveConfiguration();
    }
    /**
     * get Connection Info
     * @return WifiInfo
     */
    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * Method to Get Ap Capabilities
     *
     * SSID Name of HotSpot
     *  String contain Ap Capabilities
     */
    public String getApCapabilities(String mSSID){
        scanNetworks();
        for (ScanResult r : mResults) {
            if(r.SSID.equals(mSSID)){
                return r.capabilities;
            }
        }

        return null;
    }
    /**
     * Method to Get Ap frequency
     *
     * SSID Name of HotSpot
     *  int contain Link Speed
     */
    public int getApfrequency(String mSSID){
        scanNetworks();
        for (ScanResult r : mResults) {
            if(r.SSID.equals(mSSID)){
                return r.frequency;
            }
        }

        return 0;
    }


    /**
     * Method to Get Ap Signal Level
     *
     * SSID Name of HotSpot
     *  int contain Link Speed
     */
    public int getApSignalLevel(String mSSID){
        scanNetworks();
        for (ScanResult r : mResults) {
            if(r.SSID.equals(mSSID)){
                return r.level;
            }
        }

        return 0;
    }
    /**
     * Method to Get Security Mode By Network SSID
     *
     * @param SSID Name of HotSpot
     * @return OPEN PSK EAP OR WEP
     */
    public  String getSecurityModeBySSID(String SSID){

        List<ScanResult> scanResultList=mWifiManager.getScanResults();

        if(mWifiManager.isWifiEnabled()){

            for (ScanResult result : scanResultList) {

                if (result.SSID.equals(SSID)) {
                    return getSecurityMode(result);

                }
            }

        }
        return null;
    }
    /**
     * Method to Get Network Security Mode
     *
     * @param scanResult
     * @return OPEN PSK EAP OR WEP
     */
    public String getSecurityMode(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] modes = {"WPA", "EAP","WEP" };
        for (int i = modes.length - 1; i >= 0; i--) {
            if (cap.contains(modes[i])) {
                return modes[i];
            }
        }
        return "OPEN";
    }
    ScanTimer twoSecondTimer;
    public void startScan(long interval, long duration){
        twoSecondTimer = new ScanTimerSimple(interval, duration,mContext);
        //Start the timer.
        twoSecondTimer.start();
    }
    public void stopScan(){
        twoSecondTimer.cancel();
    }

}
