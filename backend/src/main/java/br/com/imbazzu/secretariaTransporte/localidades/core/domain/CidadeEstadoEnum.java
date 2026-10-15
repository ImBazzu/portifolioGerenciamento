package br.com.imbazzu.secretariaTransporte.localidades.core.domain;


public enum CidadeEstadoEnum {
    AC("AC"), AL("AL"), AP("AP"), AM("AM"), BA("BA"),
    CE("CE"), DF("DF"), ES("ES"), GO("GO"), MA("MA"),
    MT("MT"), MS("MS"), MG("MG"), PA("PA"), PB("PB"),
    PR("PR"), PE("PE"), PI("PI"), RJ("RJ"), RN("RN"),
    RS("RS"), RO("RO"), RR("RR"), SC("SC"), SP("SP"),
    SE("SE"), TO("TO");

    private final String valueString;

    CidadeEstadoEnum(String valueString) {
        this.valueString = valueString;
    }

    public String getValueString() {
        return valueString;
    }
}