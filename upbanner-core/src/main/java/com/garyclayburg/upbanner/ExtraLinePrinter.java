package com.garyclayburg.upbanner;

/**
 * <br><br>
 * Interface used to customize the printing of banners
 *
 * @author Gary Clayburg
 */
@FunctionalInterface
public interface ExtraLinePrinter {

    void call(StringBuilder stringBuilder);
}
