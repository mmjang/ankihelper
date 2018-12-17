package com.mmjang.duckmemo.data.note;

class ExportResult{
    public final boolean successful;
    public final String msg;
    public ExportResult(boolean successful, String msg){
        this.successful = successful;
        this.msg = msg;
    }
}

public interface Exporter {
    ExportResult add(Addable addable);
}
