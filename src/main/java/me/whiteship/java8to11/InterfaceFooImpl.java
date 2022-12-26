package me.whiteship.java8to11;

public class InterfaceFooImpl implements InterfaceFoo {

    String name;

    public InterfaceFooImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void printName() {
        System.out.println(this.name);
    }
}
