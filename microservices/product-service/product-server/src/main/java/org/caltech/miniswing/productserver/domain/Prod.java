package org.caltech.miniswing.productserver.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswing.domain.BaseEntity;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "prod")
public class Prod extends BaseEntity {
    @Id
    @Column(length = 10, nullable = false)
    private String prodId;

    @Column(length = 80, nullable = false)
    private String prodNm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=2)
    private SvcProdCd svcProdCd;

    @Builder
    public Prod(String prodId, String prodNm, SvcProdCd svcProdCd, String description) {
        this.prodId      = prodId;
        this.prodNm      = prodNm;
        this.svcProdCd   = svcProdCd;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Prod)) {
            return false;
        }
        Prod other = (Prod)obj;
        return prodId.equals(other.getProdId());
    }
}
