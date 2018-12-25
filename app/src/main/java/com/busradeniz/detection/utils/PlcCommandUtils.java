package com.busradeniz.detection.utils;

public class PlcCommandUtils {

    private static final String TAG = "daixinhong";

    public static String plcCommand2String(String string) {
        return plcCommand2String(string, false, 0);
    }

    public static String plcCommand2String(String string,boolean isWrite) {
        return plcCommand2String(string, isWrite, 0);
    }

    public static String plcCommand2String(String string, boolean isWrite, int value) {

        StringBuffer result = new StringBuffer("01");
        String startStr = string.substring(0, 1);
        if (!startStr.matches("^[A-Za-z]+$")) {
            ToastUtils.showTextToast("非法字符串");
            return "";
        }

        String endSub = string.substring(1);
        if (startStr.equals("D") || startStr.equals("d")) {
            if (isWrite) {
                result.append("06");
            } else {
                result.append("03");
            }
        } else if (startStr.equals("X") || startStr.equals("x")) {
            result.append("01");
            endSub = Integer.valueOf(endSub) + 1200 + "";  //x的协议地址是1200

        } else if (startStr.equals("Y") || startStr.equals("y")) {
            result.append("01");
        } else if (startStr.equals("m") || startStr.equals("M")) {
            if (isWrite) {
                result.append("05");
            } else {
                result.append("01");
            }
            endSub = Integer.valueOf(endSub) + 2000 + "";  //y的协议地址是2000
        }

        String hexStr = str2HexStr(endSub);  //转16进制数
        switch (hexStr.length()) {
            case 1:
                result.append("000" + hexStr);
                break;
            case 2:
                result.append("00" + hexStr);
                break;
            case 3:
                result.append("0" + hexStr);
                break;
            case 4:
                result.append(hexStr);
                break;
            default:
                ToastUtils.showTextToast("非法指令");
                return "";
        }

        if (isWrite) {
            String str2HexStr = str2HexStr(value + "");
            if (startStr.equals("m")&&value==1) {
                result.append("ff00");
            } else {
                switch (str2HexStr.length()) {
                    case 1:
                        result.append("000" + str2HexStr);
                        break;
                    case 2:
                        result.append("00" + str2HexStr);
                        break;
                    case 3:
                        result.append("0" + str2HexStr);
                        break;
                    case 4:
                        result.append(str2HexStr);
                        break;
                }
            }

        } else {
            result.append("0001");
        }

        //计算CRC检验值
        String crc = getCRC(toBytes(result.toString()));
        String endSubs = crc.substring(2, crc.length());
        result.append(endSubs);
        String startSub = crc.substring(0, 2);
        result.append(startSub);

        return result.toString().toUpperCase();
    }


    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }


    /**
     * 10进制转16进制
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {

        return Integer.toHexString(Integer.parseInt(str));

    }


    /**
     * 计算CRC校验值
     *
     * @param bytes
     * @return
     */
    public static String getCRC(byte[] bytes) {

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);

    }


    public static String strToHexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

}
