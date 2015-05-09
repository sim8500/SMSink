package com.sim8500.smsink;

import android.telephony.SmsMessage;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbernad on 10/01/15.
 */
public class SmsinkFileIOUtils {

    public static List<SmsMessage> readSinkFile(FileInputStream input) {

        //BufferedInputStream buffInput = new BufferedInputStream(input);
        List<SmsMessage> resList = new ArrayList<SmsMessage>();

        ByteBuffer buff = ByteBuffer.allocate(4);
        byte[] smsbuff;
        try {
            while (input.read(buff.array(), 0, 4) != -1) {

                int lenval = buff.getInt();
                smsbuff = new byte[lenval];
                input.read(smsbuff, 0, lenval);

                SmsMessage sms = SmsMessage.createFromPdu(smsbuff);

                resList.add(sms);
                buff.rewind();
            }
        }
        catch (Exception ex) {
            Log.d("Smsink_Reader", "exception during read procedure...");
        }

        return resList;
    }

    public static int writeSinkFile(FileOutputStream output, List<SmsMessage> msgs) {

        int bytesCount = 0;
        ByteBuffer buffInt = ByteBuffer.allocate(4);

        for(SmsMessage sms : msgs) {
            byte[] msgArray = null;
            try {
                msgArray = sms.getPdu();
            }
            catch(Exception ex)
            {

            }

            if(msgArray != null) {
                buffInt.rewind();
                buffInt.putInt(msgArray.length);
                try {
                    output.write(buffInt.array());
                    output.write(msgArray);

                    bytesCount += msgArray.length + 4;
                } catch (IOException ex) {
                    Log.d("Smsink_Reader", "exception during write procedure...");
                    return bytesCount;
                }
            }
        }

        try {
            output.flush();
        }
        catch(IOException ex)
        {  }
        return bytesCount;
    }

    public static int appendSinkFile(FileOutputStream output, SmsMessage msg) {
        int bytesCount = 0;
        ByteBuffer buffInt = ByteBuffer.allocate(4);

            byte[] msgArray = null;
            try {
                msgArray = msg.getPdu();
            }
            catch(Exception ex)
            {

            }

            if(msgArray != null) {
                buffInt.putInt(msgArray.length);
                try {
                    output.write(buffInt.array());
                    output.write(msgArray);

                    bytesCount += msgArray.length + 4;
                } catch (IOException ex) {
                    Log.d("Smsink_Reader", "exception during write procedure...");
                    return bytesCount;
                }
            }

        try {
            output.flush();
        }
        catch(IOException ex)
        {  }
        return bytesCount;
    }
}
