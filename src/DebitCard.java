/*  Thikamporn Simud 5910401033
    Wipawadee Monkhut 5910406451
    SEC 1
 */
public class DebitCard {
    private String numberDebit;
    private String numberCVV;

    public DebitCard(String numberDebit, String numberCVV) {
        this.numberDebit = numberDebit;
        this.numberCVV = numberCVV;
    }

    public String getNumberDebit() {
        return numberDebit;
    }

    public void setNumberDebit(String numberDebit) {
        this.numberDebit = numberDebit;
    }

    public String getNumberCVV() {
        return numberCVV;
    }

    public void setNumberCVV(String numberCVV) {
        this.numberCVV = numberCVV;
    }
}
