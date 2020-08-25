/*
 * @(#)BNeoConsoleRecipient.java   25.03.2014
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.niagara.module.neoalarm;

import com.tridium.alarm.BConsoleRecipient;

import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.nre.annotations.NiagaraType;

/**
 * Alarm recipient for enhanced alarm console. This is marker
 * subclass, BNeoAlarmConsole is defined as view agent on this
 * recipient
 *
 */
@NiagaraType
public class BNeoConsoleRecipient extends BConsoleRecipient {

    
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.neopsis.niagara.module.neoalarm.BNeoConsoleRecipient(2979906276)1.0$ @*/
/* Generated Tue Aug 25 13:34:55 CEST 2020 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNeoConsoleRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
