package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@ToString
@Entity
@Table(name = "GDE_CONFIG", schema = "cfg")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@IdClass(GdeConfigPk.class)
public class GdeConfig {

  @Id
  @Column(name = "PRIMITIVA", nullable = false)
  private String primitiva;

  @Column(name = "TYPE", nullable = false)
  private String type;

  @Column(name = "EVENT_HUB")
  private Boolean eventHubEnabled;

  @Column(name = "EVENT_HUB_PAYLOAD")
  private Boolean eventHubPayloadEnabled;

}
