package fr.cpe.model;

public class CreditCardItem implements IVaultItem {
    private String name;
    private String description;
    private String cardNumber;
    private String expiryDate;
    private String cvv;


    public CreditCardItem() {}

    public CreditCardItem(String name, String description, String cardNumber, String expiryDate, String cvv) {
        this.name = name;
        this.description = description;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
