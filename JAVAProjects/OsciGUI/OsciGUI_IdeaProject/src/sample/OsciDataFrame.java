package sample;

import sample.constants.GlobalConstants;

public class OsciDataFrame {
    byte [] opcode;
    byte [] data;
    OsciDataFrame(){
        opcode = new byte[2];
        data = new byte[GlobalConstants.NUM_DATA_BYTES];
    }
}
