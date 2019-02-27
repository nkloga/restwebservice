package app.entity;

public enum Gender {

    M("Male"),
    F("Female");

    private String value;

    Gender(String Value) {
        this.value = value();
    }

    public String value() {
        return value;
    }

}
