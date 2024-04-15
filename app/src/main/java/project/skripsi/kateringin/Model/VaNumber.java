package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class VaNumber implements Serializable {
    public String bank;
    public String va_number;

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getVa_number() {
        return va_number;
    }

    public void setVa_number(String va_number) {
        this.va_number = va_number;
    }
}
