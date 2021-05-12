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
public enum SvcStCd implements EnumModel {
    AC ("사용중"),
    SP ("정지"),
    TG ("일반해지");

    private final String value;

    SvcStCd(String value) {
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
    public static SvcStCd forValues(@JsonProperty("key") String key,
                                      @JsonProperty("value") String value) {
        return Arrays.stream(SvcStCd.values())
                .filter(ss -> ss.getKey().equals(key) && ss.getValue().equals(value) )
                .findFirst()
                .orElseThrow( () -> new InvalidInputException("유효하지 않은 svc st cd!") );
    }
}
