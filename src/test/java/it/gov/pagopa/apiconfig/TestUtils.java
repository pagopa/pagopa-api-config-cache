package it.gov.pagopa.apiconfig;

import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiFasciaCostoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.CdiPreference;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.DizionarioMetadati;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamentoCanale;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

  public static List<IntermediariPa> intpas = Arrays.asList(IntermediariPa.builder()
          .idIntermediarioPa("intpa1")
          .build(),
      IntermediariPa.builder()
          .idIntermediarioPa("intpa2")
          .build());

  public static List<IntermediariPsp> intpsp = Arrays.asList(IntermediariPsp.builder()
          .idIntermediarioPsp("intpa1")
          .build(),
      IntermediariPsp.builder()
          .idIntermediarioPsp("intpa2")
          .build());

  public static List<ConfigurationKeys> mockConfigurationKeys = Arrays.asList(
      ConfigurationKeys.builder()
          .configCategory("configCategory")
          .configKey("configKey")
          .configValue("configValue")
          .configDescription("configDescription").build(),
      ConfigurationKeys.builder()
          .configCategory("configCategory2")
          .configKey("configKey2")
          .configValue("configValue2")
          .configDescription("configDescription2").build());

  public static List<DizionarioMetadati> mockMetadataDicts = Arrays.asList(
      DizionarioMetadati.builder()
          .key("key")
          .startDate(ZonedDateTime.now())
          .endDate(ZonedDateTime.now())
          .description("description").build(),
      DizionarioMetadati.builder()
          .key("key2")
          .startDate(ZonedDateTime.now())
          .endDate(ZonedDateTime.now())
          .description("description2").build());

  public static List<Pa> pas = Arrays.asList(Pa.builder()
          .description("description1")
          .enabled(true)
          .idDominio("idDominio1")
          .pagamentoPressoPsp(true)
          .build(),
      Pa.builder()
          .description("description2")
          .enabled(true)
          .idDominio("idDominio2")
          .pagamentoPressoPsp(true)
          .build());

  public static List<Psp> psps = Arrays.asList(Psp.builder()
          .idPsp("idPsp1")
          .objId(1l)
          .build(),
      Psp.builder()
          .idPsp("idPsp2")
          .objId(2l)
          .build());
  public static List<Canali> canali = Arrays.asList(
      Canali.builder()
          .intermediarioPsp(intpsp.get(0))
          .protocollo("HTTP")
          .idCanale("idCanale1")
          .modelloPagamento("IMMEDIATO")
          .build(),
      Canali.builder()
          .intermediarioPsp(intpsp.get(0))
          .protocollo("HTTP")
          .modelloPagamento("IMMEDIATO")
          .idCanale("idCanale2")
          .build());

  public static List<TipiVersamento> tipiVersamento = Arrays.asList(
      TipiVersamento.builder()
          .tipoVersamento("CP")
          .build(),
      TipiVersamento.builder()
          .tipoVersamento("PO")
          .build());

  public static List<PspCanaleTipoVersamentoCanale> pspCanaliTv = Arrays.asList(
      PspCanaleTipoVersamentoCanale.builder()
          .psp(psps.get(0))
          .canale(canali.get(0))
          .tipoVersamento(tipiVersamento.get(0))
          .build(),
      PspCanaleTipoVersamentoCanale.builder()
          .psp(psps.get(1))
          .canale(canali.get(1))
          .tipoVersamento(tipiVersamento.get(1))
          .build());

  public static List<CdiDetail> cdiDetail = Arrays.asList(
      CdiDetail.builder()
          .id(1L)
          .canaleApp(1L)
          .priorita(1L)
          .modelloPagamento(1L)
          .pspCanaleTipoVersamento(pspCanaliTv.get(0))
          .build(),
      CdiDetail.builder()
          .id(2L)
          .canaleApp(2L)
          .priorita(2L)
          .modelloPagamento(2L)
          .pspCanaleTipoVersamento(pspCanaliTv.get(1))
          .build());

  public static List<CdiMasterValid> cdiMasterValid = Arrays.asList(
      CdiMasterValid.builder()
          .psp(psps.get(0))
          .cdiDetail(Arrays.asList(cdiDetail.get(0)))
          .stornoPagamento(true)
          .marcaBolloDigitale(true)
          .build(),
      CdiMasterValid.builder()
          .psp(psps.get(1))
          .cdiDetail(Arrays.asList(cdiDetail.get(1)))
          .stornoPagamento(true)
          .marcaBolloDigitale(true)
          .build());
  public static List<CdiFasciaCostoServizio> cdiFasciaCostoServizio = Arrays.asList(
      CdiFasciaCostoServizio.builder()
          .cdiDetail(cdiDetail.get(0))
          .costoFisso(10d)
          .importoMassimo(10d)
          .valoreCommissione(10d)
          .build(),
      CdiFasciaCostoServizio.builder()
          .cdiDetail(cdiDetail.get(1))
          .costoFisso(10d)
          .importoMassimo(10d)
          .valoreCommissione(10d)
          .build());
  public static List<CdiPreference> cdiPreference = Arrays.asList(
      CdiPreference.builder()
          .costoConvenzione(10d)
          .cdiDetail(cdiDetail.get(0))
          .build(),
      CdiPreference.builder()
          .costoConvenzione(10d)
          .cdiDetail(cdiDetail.get(1))
          .build());
  public static List<CdiInformazioniServizio> cdiInformazioniServizio = Arrays.asList(
      CdiInformazioniServizio.builder()
          .cdiDetail(cdiDetail.get(0))
          .codiceLingua("IT")
          .build(),
      CdiInformazioniServizio.builder()
          .cdiDetail(cdiDetail.get(1))
          .codiceLingua("IT")
          .build());

}
