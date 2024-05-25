package io.github.rczyzewski.tortilla.account;

import io.github.rczyzewski.tortilla.lens.Lens;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LensTest {

    @Test
    void updateTopLevelField() {

        Account account = Account.builder()
                .name("Foo")
                .surename("Bar")

                .build();

        Account updatedAccount = Lens.focus(Lens.of(Account::getName, Account::withName))
                .rebuildWith("FooFighters")
                .apply(account);

        assertThat(account).extracting(Account::getSurename).isEqualTo("Bar");
        assertThat(updatedAccount).extracting(Account::getSurename).isEqualTo("Bar");

        assertThat(account).extracting(Account::getName).isEqualTo("Foo");
        assertThat(updatedAccount).extracting(Account::getName).isEqualTo("FooFighters");

    }

    @Test
    void testInnerField() {

        Account account = Account.builder()
                .name("Foo")
                .surename("Bar")
                .address(Address.builder().build())
                .build();


        Account newAccount = Lens.focus(Lens.of(Account::getAddress, Account::withAddress))
                .split(Lens.focus(Lens.of(Address::getBuildingNumber, Address::withBuildingNumber)).rebuildWith(12))
                .focus(Lens.of(Address::getStreet, Address::withStreet))
                .rebuildWith("FooFighters Street")
                .apply(account);

        assertThat(newAccount).extracting(Account::getAddress).extracting(Address::getBuildingNumber).isEqualTo(12);
        assertThat(newAccount).extracting(Account::getAddress).extracting(Address::getStreet).isEqualTo("FooFighters Street");
    }
}


@With
@Value
@Builder
class Account {
    String name;
    String surename;
    Address address;

}

@With
@Value
@Builder
class Address {
    String street;
    Integer buildingNumber;
}