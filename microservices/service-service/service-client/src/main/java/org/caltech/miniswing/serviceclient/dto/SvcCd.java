package org.caltech.miniswing.serviceclient.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.util.EnumModel;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum SvcCd implements EnumModel {
    C ("이동전화"),
    I ("인터넷");

    private final String value;

    SvcCd(String value) {
        this.value = value;
    }


    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }


    @JsonCreator
    public static SvcCd forValues(@JsonProperty("key") String key,
                                      @JsonProperty("value") String value) {
        return Arrays.stream(SvcCd.values())
                .filter(sc -> sc.getKey().equals(key) && sc.getValue().equals(value) )
                .findFirst()
                .orElseThrow( () -> new InvalidInputException("유효하지 않은 svccd!") );
    }
}
