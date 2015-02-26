package fr.bludwarf.gangstasocca.outlook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;

public class MsgWriter
{
	public static void main (String ... args) throws Exception {
        String msgfile = "D:\\Desktop\\Soccer.msg";
        MAPIMessage msg = new MAPIMessage(msgfile);
        AttachmentChunks attachments[] = msg.getAttachmentFiles();
        if(attachments.length > 0) {
            for (AttachmentChunks a  : attachments) {
                System.out.println(a.attachLongFileName);
                // extract attachment
                ByteArrayInputStream fileIn = new ByteArrayInputStream(a.attachData.getValue());
                File f = new File("c:/temp", a.attachLongFileName.toString()); // output
                OutputStream fileOut = null;
                try {
                    fileOut = new FileOutputStream(f);
                    byte[] buffer = new byte[2048];
                    int bNum = fileIn.read(buffer);
                    while(bNum > 0) {
                        fileOut.write(buffer);
                        bNum = fileIn.read(buffer);
                    }
                }
                finally {
                    try {
                        if(fileIn != null) {
                            fileIn.close();
                        }
                    }
                    finally {
                        if(fileOut != null) {
                            fileOut.close();
                        }
                    }
                }
            }
        }
        else {

            System.out.println("No attachment");
        }
        
        // MLV
        System.out.println(msg.getTextBody());
    }
}
