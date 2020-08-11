package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;

import com.android.celltechmobileservicesapp.Constants;
import com.android.celltechmobileservicesapp.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FillOne extends AsyncTask {
    String TAG = "FillOneAlg";
    //    public Process process;
    public int phaseNumber;
    public boolean success;
    public String message = "";

    public FillOne(int nr) {
        this.phaseNumber = nr;
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            byte[] objFileBytes = new byte[Constants.ddBS];
            for (int i = 0; i < Constants.ddBS; i++) {
                objFileBytes[i] = (byte) 0xFF;
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path + "/one");
            FileOutputStream fos = new FileOutputStream(file.getPath());

            while (Utils.getCurrentAvailableSpace() > 0) {
                try {
                    fos.write(objFileBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            fos.close();
            success = true;
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
            message = e.getMessage();
        } catch (Exception e) {
            success = false;
            message = e.getMessage();
            e.printStackTrace();
        }

        return null;
    }

}


//        bs= sets the blocksize, for example bs=1M would be 1MiB blocksize.
//
//        count= copies only this number of blocks (the default is for dd to keep going forever or until the input runs out). Ideally blocks are of bs= size but there may be incomplete reads, so if you use count= in order to copy a specific amount of data (count*bs), you should also supply iflag=fullblock.
//
//        seek= seeks this number of blocks in the output, instead of writing to the very beginning of the output device.
//
//        So, for example, this copies 1MiB worth of y\n to position 8MiB of the outputfile. So the total filesize will be 9MiB.
//
//        $ yes | dd bs=1M count=1 seek=8 iflag=fullblock of=outputfile
//        $ ls -alh outputfile
//        9.0M Jun  3 21:02 outputfile
//        $ hexdump -C outputfile
//        00000000  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
//        *
//        00800000  79 0a 79 0a 79 0a 79 0a  79 0a 79 0a 79 0a 79 0a  |y.y.y.y.y.y.y.y.|
//        *
//        00900000
//        Since you mention /dev/random and overwriting partitions... it will take forever since /dev/random (as well as /dev/urandom) is just too slow. You could just use shred -v -n 1 instead, that's fast and usually available anywhere.