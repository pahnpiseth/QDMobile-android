/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjsip_redirect_op {
  PJSIP_REDIRECT_REJECT,
  PJSIP_REDIRECT_ACCEPT,
  PJSIP_REDIRECT_PENDING,
  PJSIP_REDIRECT_STOP;

  public final int swigValue() {
    return swigValue;
  }

  public static pjsip_redirect_op swigToEnum(int swigValue) {
    pjsip_redirect_op[] swigValues = pjsip_redirect_op.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsip_redirect_op swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsip_redirect_op.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private pjsip_redirect_op() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private pjsip_redirect_op(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private pjsip_redirect_op(pjsip_redirect_op swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

