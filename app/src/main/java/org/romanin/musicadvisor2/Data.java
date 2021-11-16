package org.romanin.musicadvisor2;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * deliberately kept as simple as possible because of awful tests
 */
public class Data{
    static final Logger LOG = Logger.getLogger(Data.class.toString());

    static void p(String s) {
        System.out.println(s);
    }
    private ArrayList<String> items;
    private int startIndex;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private String error;

    public Data() {
        items = new ArrayList<>();
        startIndex = 0;
        error = "";
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public void addItem(String item) {
        this.items.add(item);
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getItemsSize() {
        return items.size();
    }

    public void print(int pageSize) {
        if (error.length() > 0) {
            System.out.println(error);
            error = "";
        }else {
            int totalPages = (int) Math.ceil(items.size() / (double) pageSize);
            int currentPage = pageSize == 1 ? startIndex + 1 : ((startIndex - 1) / pageSize) + 1;
            int endIndex = Math.min(startIndex + pageSize, items.size());
            LOG.finest(String.valueOf(totalPages));
            LOG.fine(String.valueOf(currentPage));
            LOG.fine(String.valueOf(endIndex));
            for (int i = startIndex; i < endIndex; i++) {
                p(items.get(i));
            }
            if (items.size() > 0) {
                System.out.printf("---PAGE %s OF %s---", currentPage, totalPages);
            }
            p("\n");
        }
    }
}