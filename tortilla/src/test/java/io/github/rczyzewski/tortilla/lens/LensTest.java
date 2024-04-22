package io.github.rczyzewski.tortilla.lens;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.var;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LensTest {

    @Test
    void updateTopLevelField(){

        var account = Account.builder()
                .name("Foo")
                .surename("Bar")

                .build();

        var updatedAccount = Lens.focus(Lens.of(Account::getName, Account::withName))
                .witherIt("FooFighters")
                .apply(account);

       assertThat(account).extracting(Account::getSurename).isEqualTo("Bar");
       assertThat(updatedAccount).extracting(Account::getSurename).isEqualTo("Bar");

       assertThat(account).extracting(Account::getName).isEqualTo("Foo");
       assertThat(updatedAccount).extracting(Account::getName).isEqualTo("FooFighters");

    }
    @Test
    void testInnerField() {

        var account = Account.builder()
                .name("Foo")
                .surename("Bar")
                .address(Address.builder().build())
                .build();

        var updatedAccount = Lens.focus(Lens.of(Account::getAddress, Account::withAddress))
                .focus(Lens.of(Address::getStreet, Address::withStreet))
                .witherIt("FooFighters Street")
                .apply(account);

        assertThat(updatedAccount)
                .extracting(Account::getAddress)
                .extracting(Address::getStreet)
                .isEqualTo("FooFighters Street");


    }

}

@With
@Value
@Builder
class Account{
    String name;
    String surename;
    Address address;

}
@With
@Value
@Builder
class Address{
    String street;
    Integer buildingNumber;
}