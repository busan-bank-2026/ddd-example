package ddd;

import java.io.Serializable;

public record Address(String value) implements Serializable {

    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("배송 주소를 입력해야 합니다");
        }
    }
}
