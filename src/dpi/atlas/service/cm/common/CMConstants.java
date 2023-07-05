package dpi.atlas.service.cm.common;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Sep 2, 2007
 * Time: 6:59:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMConstants {
    public static final String TYPE_OF_SPARROW_CHANGE = "typeOfChange";
    public static final String SET_STATE = "1";
    public static final String CLEAR_STATE = "0";


    // *** Internal socket status code
    // success statuses
    public static final int INTERNAL_SERVERSOCKET_INIT = 0;
    public static final int INTERNAL_SERVERSOCKET_READY = 2;
    public static final int INTERNAL_SERVERSOCKET_CREATION_OK = 4;
    public static final int INTERNAL_SERVERSOCKET_CONNECTION_OK = 6;
    //
    public static final int INTERNAL_SERVERSOCKET_WAITING_FOR_SOCKET_ESTABLISHMENT = 8;
    public static final int INTERNAL_SERVERSOCKET_WAITING_FOR_REQUEST = 10;
    public static final int INTERNAL_SERVERSOCKET_RECEIVING_OK = 12;
    public static final int INTERNAL_SERVERSOCKET_SENDING_OK = 14;

    // failed statuses
    public static final int INTERNAL_SERVERSOCKET_CREATION_ERROR = 1;
    public static final int INTERNAL_SERVERSOCKET_ACCEPTING_CONNECTION_ERROR = 3;
    public static final int INTERNAL_SERVERSOCKET_CONNECTION_BROKEN = 5;
    public static final int INTERNAL_SERVERSOCKET_RECEIVING_ERROR = 7;
    public static final int INTERNAL_SERVERSOCKET_SENDING_ERROR = 9;
    public static final int INTERNAL_NULL_CHANNEL_ERROR = 11;

    //  *** External socket status code
    public static final int EXTERNAL_SERVERSOCKET_INIT = 100; // Socket is going to be established( in middle of getting ready for accepting request), still it's not ready for accepting request 
    public static final int EXTERNAL_SERVERSOCKET_OK = 200;  //  Socket'status is O.K. . Last RECEIVING was achieved successfully, It's NOT in mode of waiting and connection is OK, everything is OK!
    public static final int EXTERNAL_SERVERSOCKET_WAITING_FOR_SOCKET_ESTABLISHMENT = 300; //Socket on supposed port was established but it's waiting for client to establish a connetion
    public static final int EXTERNAL_SERVERSOCKET_WAITING_FOR_REQUEST = 400;  // Socket on supposed port, also the connetion between server and client were established but no request has been sent from client.

    public static final int EXTERNAL_SERVERSOCKET_CREATION_ERROR = 500; // Creation of Socket is failed
    public static final int EXTERNAL_SERVERSOCKET_ACCEPTING_CONNECTION_ERROR = 600; // Creation of Socket is OK but it's failed in accepting connection from client
    public static final int EXTERNAL_SERVERSOCKET_NOT_CONNECTED_ERROR = 700; // Socket is not connected anymore.
    public static final int EXTERNAL_SERVERSOCKET_SENDING_RECEIVING_ERROR = 800; // Socket'connetion is OK but because of some reasons sending or receiving of data has failed
    public static final int EXTERNAL_NULL_CHANNEL_ERROR = 900; // Iniitialization of channel has failed
    public static final int EXTERNAL_UKNOWN_ERROR = 2000; // Iniitialization of channel has failed

    // Note: at raising any kind of errors, a recovery process will be started automatically to restore the system to a stable state , there is no need to do any action. 

    public static final String HOST_FLOW_ID = "HostFlowID";
    public static final String LORO_ACCOUNT_UNDEFINED = "0000001919881";
    public static final String LORO_DEVICE_UNDEFINED = "****";
    public static final String BRANCH_ID_UNDEFINED = "*****";
    public static final String SGB_TX_CODE_UNDEFINED = "000";

    public static final String TAMINEJTEMAEE_ACCOUNT_UNDEFINED_RECIVED = "0000000008150";
    public static final String TAMINEJTEMAEE_ACCOUNT_DEFINED = "0000057162007";
    public static final String ATABATALIYAT_ACCOUNT_UNDEFINED_RECIVED = "0000000004040";
    public static final String ATABATALIYAT_ACCOUNT_DEFINED = "0000038323822";
    public static final String HELALAHMAR_ACCOUNT_UNDEFINED_RECIVED = "0000000099999";
    public static final String HELALAHMAR_ACCOUNT_DEFINED ="0000395091034";
    public static final String REFAHKODAK_ACCOUNT_UNDEFINED_RECIVED = "0000000055555";
    public static final String REFAHKODAK_ACCOUNT_DEFINED ="0002500145695";

}
