package com.chartiq.sdk.model;

import java.util.Date;

public class OHLCChart {

    private Date DT;
    private double Open;
    private double High;
    private double Low;
    private double Close;
    private double Volume;
    private double AdjClose;

    public OHLCChart(Date date, double open, double high, double low, double close, double volume, double adjClose) {
        this.DT = date;
        this.Open = open;
        this.High = high;
        this.Low = low;
        this.Close = close;
        this.Volume = volume;
        this.AdjClose = adjClose;
    }

    public Date getDT() {
        return DT;
    }

    public void setDT(Date DT) {
        this.DT = DT;
    }

    public double getOpen() {
        return Open;
    }

    public void setOpen(double open) {
        Open = open;
    }

    public double getHigh() {
        return High;
    }

    public void setHigh(double high) {
        High = high;
    }

    public double getLow() {
        return Low;
    }

    public void setLow(double low) {
        Low = low;
    }

    public double getClose() {
        return Close;
    }

    public void setClose(double close) {
        Close = close;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public double getAdjClose() {
        return AdjClose;
    }

    public void setAdjClose(double adjClose) {
        AdjClose = adjClose;
    }

    @Override
    public String toString() {
        return "{" +
                "\"DT\":\"" + DT +
                "\",\"Open\":" + Open +
                ",\"High\":" + High +
                ",\"Low\":" + Low +
                ",\"Close\":" + Close +
                ",\"Volume\":" + Volume +
                ",\"Adj_Close\":" + AdjClose +
                "}";
    }
}
