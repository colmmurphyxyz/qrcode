package jp.sourceforge.qrcode.util;

public class ContentConverter {

    public static String convert(String targetString) {
        if (targetString == null) {
            return targetString;
        }
        if (targetString.contains("MEBKM:")) {
            targetString = convertBookmark(targetString);
        }
        else if (targetString.contains("MECARD:")) {
            targetString = convertAddressBook(targetString);
        }
        else if (targetString.contains("MATMSG:")) {
            targetString = convertMailto(targetString);
        }
        else if (targetString.contains("http://")) {
            targetString = targetString.replace("http://", "\nhttps://");
        }
        return targetString;
    }

    private static String convertBookmark(String targetString) {
        return targetString
                .replace("MEBKM:", "")
                .replace("TITLE:", "")
                .replace(";", "")
                .replace("URL:", "");
    }

    private static String convertAddressBook(String targetString) {
        return targetString
                .replace("MECARD:", "")
                .replace(";", "")
                .replace("N:", "NAME1:")
                .replace("SOUND:", "\nNAME2:")
                .replace("TEL:", "\nTEL1:")
                .replace("EMAIL:", "\nMAIL1:") + "\n";
    }

    private static String convertMailto(String s) {
        return s
                .replace("MATMSG:", "")
                .replace(";", "")
                .replace("TO:", "MAILTO:")
                .replace("SUB:", "\nSUBJECT:")
                .replace("BODY:", "\nBODY:") + "\n";
    }
}
