package model;

/**
 * Person abstract class
 *
 * WHY ABSTRACT CLASS:
 * Person is never instantiated directly — you always create either a Patient
 * or a Doctor. Making it abstract enforces this and lets us share common
 * attributes (userId, name, phone) across both subclasses without duplication.
 * The abstract method displayInfo() forces every subclass to provide its own
 * implementation. This is concept 2.2 (Inheritance).
 */
public abstract class Person {

    // Protected so subclasses can access directly
    protected String userId;
    protected String name;
    protected String phone;

    public Person(String userId, String name, String phone) {
        this.userId = userId;
        this.name   = name;
        this.phone  = phone;
    }

    /**
     * WHY ABSTRACT:
     * Each subclass displays different info (Patient shows history,
     * Doctor shows specialty), so we force them to override this.
     */
    public abstract void displayInfo();

    // Getters
    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getPhone()  { return phone; }

    // Setters
    public void setName(String name)   { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
}