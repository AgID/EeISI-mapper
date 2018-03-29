package it.infocert.eigor.api.conversion;

public interface ConversionCallback {

        void onStartingConversion(ConversionContext ctx) throws Exception;

        void onStartingToCenTranformation(ConversionContext ctx) throws Exception;

        void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception;

        void onFailedToCenConversion(ConversionContext ctx) throws Exception;

        void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception;

        void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception;

        void onFailedVerifingCenRules(ConversionContext ctx) throws Exception;

        void onStartingFromCenTransformation(ConversionContext ctx) throws Exception;

        void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception;

        void onFailedFromCenTransformation(ConversionContext ctx) throws Exception;

        void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception;

        void onTerminatedConversion(ConversionContext ctx) throws Exception;

    }