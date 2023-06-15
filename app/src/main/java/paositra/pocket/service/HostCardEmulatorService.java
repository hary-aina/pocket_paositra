package paositra.pocket.service;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import paositra.pocket.utils.NfcUtils;
public class HostCardEmulatorService extends HostApduService {

    public static final NfcUtils Utils = new NfcUtils();
    public static final String TAG = "Host Card Emulator";
    public static final String STATUS_SUCCESS = "9000";
    public static final String STATUS_FAILED = "6F00";
    public static final String CLA_NOT_SUPPORTED = "6E00";
    public static final String INS_NOT_SUPPORTED = "6D00";
    public static final String AID = "A0000002471001";
    public static final String SELECT_INS = "A4";
    public static final String DEFAULT_CLA = "00";
    public static final int MIN_APDU_LENGTH = 12;

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle bundle) {
        //Process APDU command...

        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }

        String hexCommandApdu = Utils.toHex(commandApdu);
        if (hexCommandApdu.toCharArray().length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED);
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED);
        }

        if (hexCommandApdu.substring(10, 24) == AID)  {
            return Utils.hexStringToByteArray(STATUS_SUCCESS);
        } else {
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }

        //Return response and status words
        //return new byte[0];

        /*return new byte[]{
                (byte) 0x00, // ANSWER
                (byte) 0x90, // SW1
                (byte) 0x00}; // SW2
        };*/
    }

    @Override
    public void onDeactivated(int reason) {
        // Initialiser les données et vérifier que tout est fermé
        // ...

        System.out.println(TAG + " Deactivated: " + reason);
    }
}
