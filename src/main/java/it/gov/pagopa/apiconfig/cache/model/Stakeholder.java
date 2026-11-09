package it.gov.pagopa.apiconfig.cache.model;

public enum Stakeholder {
    FDR,
    NODE,
    STANDIN,
    WISP,
    VERIFIER,
    TEST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
