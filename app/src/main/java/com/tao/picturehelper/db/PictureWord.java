package com.tao.picturehelper.db;

public class PictureWord {
    private int _id;
    private String path;
    private String word;

    public PictureWord() {
    }

    public PictureWord(int _id, String path, String word) {
        this._id = _id;
        this.path = path;
        this.word = word;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
