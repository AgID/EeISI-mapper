package it.infocert.eigor.api.conversion;

public abstract class AbstractConversionCallback implements ConversionCallback {

    @Override
    public void onStartingConversion(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onStartingToCenTranformation(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onFailedToCenConversion(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onFailedVerifyingCenRules(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onStartingFromCenTransformation(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onFailedFromCenTransformation(ConversionContext ctx) throws Exception {
    }

    @Override
    public void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception {
    }

    @Override
    public void onTerminatedConversion(ConversionContext ctx) throws Exception {
    }

}
