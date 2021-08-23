package org.skcc.team1.tobe.customerserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.util.EnumModel;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum CustTypCd implements EnumModel {
    C01 ("개인"),
    C02 ("법인");

    private final String value;

    CustTypCd(String value) {
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
    public static CustTypCd forValues(@JsonProperty("key") String key,
                                         @JsonProperty("value") String value) {
        return Arrays.stream(CustTypCd.values())
                .filter(ct -> ct.getKey().equals(key) && ct.getValue().equals(value) )
                .findFirst()
                .orElseThrow( () -> new InvalidInputException("유효하지 않은 custtypcd!") );
    }
}
