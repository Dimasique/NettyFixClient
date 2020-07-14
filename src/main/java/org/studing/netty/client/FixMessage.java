package org.studing.netty.client;

import java.util.*;

public class FixMessage {
    private final Map<String, String> tags = new LinkedHashMap<>();
    private boolean canModify = true;
    private static int number = 1;

    public static final char SEPARATOR = (char)1;


    public FixMessage(String type) {

        tags.put("8", "=" + "FIX.4.4");
        tags.put("9", null);
        tags.put("35", "=" + type);
    }

    public FixMessage() {

    }

    public void setTag(String tag, String value) throws Exception {
        if (!canModify) {
            throw new Exception("Message cannot be modified");
        }
        tags.put(tag, "=" + value);
    }

    public void setMsgSeqNum() {
        tags.put("34", "=" + (number++));
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void prepareToSend() throws Exception{
        if (!canModify) {
            throw new Exception("Message is already prepared");
        }

        this.countBodyLength();
        this.countCheckSum();
        this.canModify = false;
    }

    private void countBodyLength() {
        int sum = 0;
        boolean need = false;

        for(String key : tags.keySet()) {
            if (key.equals("9")) {
                need = true;
            }
            else if (need) {
                sum += key.length() + tags.get(key).length() + 1;
            }
        }

        tags.put("9", "=" + sum);
    }

    private void countCheckSum() {
        int controlSum = 0;
        for(String tag : tags.keySet()) {
            String tmp = tag + tags.get(tag) + (char)1;
            byte[] b = tmp.getBytes();
            for (byte value : b) {
                controlSum += value;
            }
        }

        controlSum %= 256;
        String sum;
        if (controlSum < 10)
            sum = "00" + controlSum;
        else if (controlSum < 100)
            sum = "0" + controlSum;
        else
            sum = "" + controlSum;

        tags.put("10", "=" + sum);
    }


    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        for(String key : tags.keySet()) {
            stringBuilder.append(key).append(tags.get(key)).append('|');
        }

        return new String(stringBuilder);
    }
}

