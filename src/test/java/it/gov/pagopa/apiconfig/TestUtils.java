package it.gov.pagopa.apiconfig;

import it.gov.pagopa.apiconfig.starter.entity.CanaliView;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiFasciaCostoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.CdiPreference;
import it.gov.pagopa.apiconfig.starter.entity.CdsCategoria;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggetto;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.DizionarioMetadati;
import it.gov.pagopa.apiconfig.starter.entity.FtpServers;
import it.gov.pagopa.apiconfig.starter.entity.GdeConfig;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamentoCanale;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

  public static List<String> pastazioniV2 = Arrays.asList("1", "2", "3", "4");

  public static String cacheId = "testCacheId";

  public static List<IntermediariPa> intpas =
      Arrays.asList(
          IntermediariPa.builder().idIntermediarioPa("intpa1").build(),
          IntermediariPa.builder().idIntermediarioPa("intpa2").build());

  public static List<IntermediariPsp> intpsp =
      Arrays.asList(
          IntermediariPsp.builder().idIntermediarioPsp("intpa1").build(),
          IntermediariPsp.builder().idIntermediarioPsp("intpa2").build());

  public static List<ConfigurationKeys> mockConfigurationKeys =
      Arrays.asList(
          ConfigurationKeys.builder()
              .configCategory("configCategory")
              .configKey("configKey")
              .configValue("configValue")
              .configDescription("configDescription")
              .build(),
          ConfigurationKeys.builder()
              .configCategory("configCategory2")
              .configKey("configKey2")
              .configValue("configValue2")
              .configDescription("configDescription2")
              .build());

  public static List<DizionarioMetadati> mockMetadataDicts =
      Arrays.asList(
          DizionarioMetadati.builder()
              .key("key")
              .startDate(ZonedDateTime.now())
              .endDate(ZonedDateTime.now())
              .description("description")
              .build(),
          DizionarioMetadati.builder()
              .key("key2")
              .startDate(ZonedDateTime.now())
              .endDate(ZonedDateTime.now())
              .description("description2")
              .build());

  public static List<Pa> pas =
      Arrays.asList(
          Pa.builder()
              .objId(1l)
              .description("description1")
              .enabled(true)
              .idDominio("idDominio1")
              .pagamentoPressoPsp(true)
              .build(),
          Pa.builder()
              .objId(2l)
              .description("description2")
              .enabled(true)
              .idDominio("idDominio2")
              .pagamentoPressoPsp(true)
              .build());

  public static List<Psp> psps =
      Arrays.asList(
          Psp.builder().idPsp("idPsp1").objId(1l).build(),
          Psp.builder().idPsp("idPsp2").objId(2l).build());
  public static List<CanaliView> canali =
      Arrays.asList(
          CanaliView.builder()
              .intermediarioPsp(intpsp.get(0))
              .protocollo("HTTP")
              .idCanale("idCanale1")
              .modelloPagamento("IMMEDIATO")
              .build(),
          CanaliView.builder()
              .intermediarioPsp(intpsp.get(0))
              .protocollo("HTTP")
              .modelloPagamento("IMMEDIATO")
              .idCanale("idCanale2")
              .build());

  public static List<TipiVersamento> tipiVersamento =
      Arrays.asList(
          TipiVersamento.builder().tipoVersamento("CP").build(),
          TipiVersamento.builder().tipoVersamento("PO").build());

  public static List<PspCanaleTipoVersamentoCanale> pspCanaliTv =
      Arrays.asList(
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

  public static List<CdiDetail> cdiDetail =
      Arrays.asList(
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

  public static List<CdiMasterValid> cdiMasterValid =
      Arrays.asList(
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
  public static List<CdiFasciaCostoServizio> cdiFasciaCostoServizio =
      Arrays.asList(
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
  public static List<CdiPreference> cdiPreference =
      Arrays.asList(
          CdiPreference.builder().costoConvenzione(10d).cdiDetail(cdiDetail.get(0)).build(),
          CdiPreference.builder().costoConvenzione(10d).cdiDetail(cdiDetail.get(1)).build());
  public static List<CdiInformazioniServizio> cdiInformazioniServizio =
      Arrays.asList(
          CdiInformazioniServizio.builder().cdiDetail(cdiDetail.get(0)).codiceLingua("IT").build(),
          CdiInformazioniServizio.builder().cdiDetail(cdiDetail.get(1)).codiceLingua("IT").build());
  public static List<FtpServers> ftpServers =
      Arrays.asList(FtpServers.builder().id(0L).build(), FtpServers.builder().id(1L).build());
  public static List<GdeConfig> gdeConfigurations =
      Arrays.asList(
          GdeConfig.builder().type("type1").build(), GdeConfig.builder().type("type2").build());
  public static List<WfespPluginConf> plugins =
      Arrays.asList(
          WfespPluginConf.builder().idServPlugin("type1").build(),
          WfespPluginConf.builder().idServPlugin("type2").build());

  public static List<IbanValidiPerPa> ibans =
      Arrays.asList(
          IbanValidiPerPa.builder().ibanAccredito("type1").fkPa(1l).pa(pas.get(0)).build(),
          IbanValidiPerPa.builder().ibanAccredito("type2").fkPa(2l).pa(pas.get(1)).build());

  public static List<Codifiche> encodings =
      Arrays.asList(
          Codifiche.builder().objId(1l).idCodifica("QR-CODE").build(),
          Codifiche.builder().objId(2l).idCodifica("QR-CODE").build());

  public static List<CodifichePa> encodingsPA =
      Arrays.asList(
          CodifichePa.builder()
              .pa(pas.get(0))
              .codifica(encodings.get(0))
              .codicePa(pas.get(0).getIdDominio())
              .build(),
          CodifichePa.builder()
              .pa(pas.get(1))
              .codifica(encodings.get(1))
              .codicePa(pas.get(1).getIdDominio())
              .build());
  public static List<Stazioni> stazioni =
      Arrays.asList(
          Stazioni.builder()
              .objId(1l)
              .idStazione("idStazione1")
              .intermediarioPa(intpas.get(0))
              .build(),
          Stazioni.builder()
              .objId(2l)
              .idStazione("idStazione2")
              .intermediarioPa(intpas.get(1))
              .proxyPort(9999L)
              .proxyHost("10.10.10.10")
              .proxyEnabled(true)
              .build());
  public static List<PaStazionePa> paStazioniPa =
      Arrays.asList(
          PaStazionePa.builder().objId(1l).pa(pas.get(0)).stazione(stazioni.get(0)).build(),
          PaStazionePa.builder().objId(2l).pa(pas.get(1)).stazione(stazioni.get(1)).build());

  public static List<CdsCategoria> cdsCategorie =
      Arrays.asList(
          CdsCategoria.builder().id(1l).description("cat1").build(),
          CdsCategoria.builder().id(2l).description("cat2").build());

  public static List<CdsServizio> cdsServizi =
      Arrays.asList(
          CdsServizio.builder().id(1l).idServizio("s1").categoria(cdsCategorie.get(0)).build(),
          CdsServizio.builder().id(2l).idServizio("s2").categoria(cdsCategorie.get(1)).build());

  public static List<CdsSoggetto> cdsSoggetti =
      Arrays.asList(
          CdsSoggetto.builder().id(1l).creditorInstitutionCode(pas.get(0).getIdDominio()).build(),
          CdsSoggetto.builder().id(2l).creditorInstitutionCode(pas.get(1).getIdDominio()).build());

  public static List<CdsSoggettoServizio> cdsSoggettiServizi =
      Arrays.asList(
          CdsSoggettoServizio.builder()
              .id(1l)
              .servizio(cdsServizi.get(0))
              .soggetto(cdsSoggetti.get(0))
              .build(),
          CdsSoggettoServizio.builder()
              .id(2l)
              .servizio(cdsServizi.get(1))
              .soggetto(cdsSoggetti.get(1))
              .build());
}
