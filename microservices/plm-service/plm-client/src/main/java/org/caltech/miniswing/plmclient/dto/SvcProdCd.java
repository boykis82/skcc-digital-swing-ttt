package org.caltech.miniswing.plmclient.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.util.EnumModel;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum SvcProdCd implements EnumModel {
    P1 ("기본요금제"),
    P2 ("부가요금제"),
    P3 ("부가서비스");

    private final String value;

    SvcProdCd(String value) {
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
    public static SvcProdCd forValues(@JsonProperty("key") String key,
                                  @JsonProperty("value") String value) {
        return Arrays.stream(SvcProdCd.values())
                .filter(sp -> sp.getKey().equals(key) && sp.getValue().equals(value) )
                .findFirst()
                .orElseThrow( () -> new InvalidInputException("유효하지 않은 svc prod cd!") );
    }
}
