package it.gov.pagopa.microservice.entity;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GdeConfigPk implements Serializable {

    private String primitiva;
    private String type;

}
