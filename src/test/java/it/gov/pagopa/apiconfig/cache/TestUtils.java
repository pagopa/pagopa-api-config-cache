package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.Channel;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.starter.entity.*;
import org.modelmapper.TypeToken;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

  public static void inizializeInMemoryCache(CacheController cacheController,
                                             ConfigMapper modelMapper,
                                             String version,
                                             String cacheVersion,
                                             ZonedDateTime timestamp) {
    org.springframework.test.util.ReflectionTestUtils.setField(cacheController, "inMemoryCache", inMemoryCache(modelMapper, version, cacheVersion, timestamp));
  }

  public static LinkedHashMap<String, Object> inMemoryCache(ConfigMapper modelMapper,
                                                      String version,
                                                      String cacheVersion,
                                                      ZonedDateTime timestamp) {
    LinkedHashMap<String, Object> objectObjectHashMap = new LinkedHashMap<>();

    objectObjectHashMap.put(Constants.VERSION, version);
    objectObjectHashMap.put(Constants.CACHE_VERSION, cacheVersion);
    objectObjectHashMap.put(Constants.TIMESTAMP, timestamp);

    List<Station> stations = modelMapper.modelMapper().map(
            TestUtils.stazioni,
            new TypeToken<List<Station>>() {
            }.getType());
    objectObjectHashMap.put(Constants.STATIONS, stations.stream()
            .collect(Collectors.toMap(
                    Station::getStationCode,
                    obj -> obj,
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
            )));

    List<Channel> channels = modelMapper.modelMapper().map(
            TestUtils.canali,
            new TypeToken<List<Channel>>() {
            }.getType());
    objectObjectHashMap.put(Constants.CHANNELS, channels.stream()
            .collect(Collectors.toMap(
                    Channel::getChannelCode,
                    obj -> obj,
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
            )));
    return objectObjectHashMap;
  }
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
                  .flagTravaso(true)
                  .ip("ip")
                  .redirectIp("redirectIp")
                  .redirectPort(0000l)
                  .redirectPath("redirectPath")
                  .redirectProtocollo("HTTPS")
                  .redirectQueryString("redirectQueryString")
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

  public static List<PspCanaleTipoVersamento> pspCanaliTipoVersamento =
      Arrays.asList(
              PspCanaleTipoVersamento.builder()
              .psp(psps.get(0))
                      .canaleTipoVersamento(CanaleTipoVersamento.builder()
                              .tipoVersamento(tipiVersamento.get(0))
                              .canale(Canali.builder()
                                      .idCanale("idCanale1")
                                      .fkIntermediarioPsp(intpsp.get(0))
                                      .protocollo("HTTP")
                                      .build())
                              .build())
              .build(),
              PspCanaleTipoVersamento.builder()
                      .psp(psps.get(0))
                      .canaleTipoVersamento(CanaleTipoVersamento.builder()
                              .tipoVersamento(tipiVersamento.get(0))
                              .canale(Canali.builder()
                                      .idCanale("idCanale2")
                                      .fkIntermediarioPsp(intpsp.get(0))
                                      .protocollo("HTTP")
                                      .build())
                              .build())
                      .build());


  public static List<CdiDetail> cdiDetail =
      Arrays.asList(
          CdiDetail.builder()
              .id(1L)
              .canaleApp(1L)
              .priorita(1L)
              .modelloPagamento(1L)
              .pspCanaleTipoVersamento(pspCanaliTipoVersamento.get(0))
              .fkCdiMaster(CdiMaster.builder().id(1L).build())
              .tags("Maestro")
              .build(),
          CdiDetail.builder()
              .id(2L)
              .canaleApp(2L)
              .priorita(2L)
              .modelloPagamento(2L)
              .pspCanaleTipoVersamento(pspCanaliTipoVersamento.get(1))
              .fkCdiMaster(CdiMaster.builder().id(2L).build())
              .tags("Maestro")
              .build());

  public static List<CdiMasterValid> cdiMasterValid =
      Arrays.asList(
          CdiMasterValid.builder()
              .id(1L)
              .fkPsp(psps.get(0))
              .cdiDetail(Arrays.asList(cdiDetail.get(0)))
              .stornoPagamento(true)
              .marcaBolloDigitale(true)
              .dataInizioValidita(new Timestamp(0))
              .build(),
          CdiMasterValid.builder()
              .id(2L)
              .fkPsp(psps.get(1))
              .cdiDetail(Arrays.asList(cdiDetail.get(1)))
              .stornoPagamento(true)
              .marcaBolloDigitale(true)
              .dataInizioValidita(new Timestamp(0))
              .build());
  public static List<CdiFasciaCostoServizio> cdiFasciaCostoServizio =
      Arrays.asList(
          CdiFasciaCostoServizio.builder()
              .fkCdiDetail(cdiDetail.get(0))
              .costoFisso(10d)
              .importoMassimo(10d)
              .valoreCommissione(10d)
              .build(),
          CdiFasciaCostoServizio.builder()
              .fkCdiDetail(cdiDetail.get(1))
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
          CdiInformazioniServizio.builder()
              .fkCdiDetail(cdiDetail.get(0))
              .codiceLingua("IT")
              .build(),
          CdiInformazioniServizio.builder()
              .fkCdiDetail(cdiDetail.get(1))
              .codiceLingua("IT")
              .build());
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
          IbanValidiPerPa.builder()
              .ibanAccredito("type1")
              .fkPa(1l)
              .pa(pas.get(0))
              .idBancaSeller("0")
              .idMerchant("1")
              .chiaveAvvio("2")
              .chiaveEsito("3")
              .build(),
          IbanValidiPerPa.builder().ibanAccredito("type2").fkPa(2l).pa(pas.get(1)).build());

  public static List<Codifiche> encodings =
      Arrays.asList(
          Codifiche.builder().objId(1l).idCodifica("QR-CODE").build(),
          Codifiche.builder().objId(2l).idCodifica("QR-CODE").build());

  public static List<CodifichePa> encodingsPA =
      Arrays.asList(
          CodifichePa.builder()
              .fkPa(pas.get(0))
              .fkCodifica(encodings.get(0))
              .codicePa(pas.get(0).getIdDominio())
              .build(),
          CodifichePa.builder()
              .fkPa(pas.get(1))
              .fkCodifica(encodings.get(1))
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
          PaStazionePa.builder().objId(1l).pa(pas.get(0)).fkStazione(stazioni.get(0)).build(),
          PaStazionePa.builder().objId(2l).pa(pas.get(1)).fkStazione(stazioni.get(1)).build());

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
                  .idSoggettoServizio("0_0")
              .build(),
          CdsSoggettoServizio.builder()
              .id(2l)
              .servizio(cdsServizi.get(1))
              .soggetto(cdsSoggetti.get(1))
                  .idSoggettoServizio("1_1")
              .build());

  public static List<InformativePaDetail> informativePaDetails =
      Arrays.asList(
          InformativePaDetail.builder().flagDisponibilita(Boolean.TRUE).build(),
          InformativePaDetail.builder().flagDisponibilita(Boolean.FALSE).build());

  public static List<InformativePaMaster> informativePaMaster =
      Arrays.asList(
          InformativePaMaster.builder().fkPa(pas.get(0)).details(informativePaDetails).build(),
          InformativePaMaster.builder().fkPa(pas.get(1)).details(informativePaDetails).build());
}
