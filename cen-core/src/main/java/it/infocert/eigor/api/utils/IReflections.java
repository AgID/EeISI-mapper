package it.infocert.eigor.api.utils;

import it.infocert.eigor.model.core.model.BTBG;

import java.util.Set;

public interface IReflections {

    Set<Class<? extends BTBG>> getSubTypesOfBtBg();
}
