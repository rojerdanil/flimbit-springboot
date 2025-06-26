package com.riseup.flimbit.request;

import lombok.Data;

import java.util.List;

@Data
public class DataTableRequest {
    private int draw;
    private int start;
    private int length;
    private Search search;
    private List<Order> order;
    private List<Column> columns;
    String language;

    @Data
    public static class Search {
        private String value;
        private boolean regex;
    }

    @Data
    public static class Order {
        private int column;
        private String dir;
    }

    @Data
    public static class Column {
        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;
    }
}
