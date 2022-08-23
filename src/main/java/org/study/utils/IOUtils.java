package org.study.utils;

import java.util.Scanner;

public class IOUtils {
    public static String getNotBlankLineFromCmd() {
        return getNotBlankLineFromCmd("");
    }

    public static String getNotBlankLineFromCmd(String message) {
        Scanner in = new Scanner(System.in);
        String line = "";
        do {
            if (message != null && !message.isEmpty()) System.out.println(message);
            line = in.nextLine().trim();
        } while (line.isEmpty());
        return line;
    }
}
