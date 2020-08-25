/*
 * @(#)BNeoAlarmPropertyEditor.java   02.04.2014
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.niagara.module.neoalarm;

import com.tridium.alarm.ui.BAlarmClassFE;
import com.tridium.alarm.ui.BInstructionsFE;
import com.tridium.workbench.fieldeditors.BRelTimeFE;
import com.tridium.workbench.fieldeditors.BStatusValueFE;
import com.tridium.workbench.fieldeditors.BStringFE;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.gx.BFont;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.pane.BGridPane;
import javax.baja.ui.pane.BScrollPane;
import javax.baja.ui.util.UiLexicon;
import javax.baja.util.BFormat;
import javax.baja.util.Lexicon;
import javax.baja.workbench.CannotSaveException;

/**
 * Class description
 *
 */
@NiagaraType
public class BNeoAlarmPropertyEditor extends BDialog {

    
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.neopsis.niagara.module.neoalarm.BNeoAlarmPropertyEditor(2979906276)1.0$ @*/
/* Generated Tue Aug 25 13:34:55 CEST 2020 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNeoAlarmPropertyEditor.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    public BNeoAlarmPropertyEditor() {
        super();
    }

    public BNeoAlarmPropertyEditor(BWidget parent, BAlarmSourceExt ext, BAlarmClassFE alarmClassFE, BInstructionsFE instructionsFE) {

        super(parent, lex.get("alarm.property.dialog.label", "Alarm properties"), true);
        alarmExtension = ext;
        closeCmd       = new CloseCmd(this);
        saveCmd        = new SaveCmd(this);

        BButton closeBtn = new BButton(closeCmd);
        BButton saveBtn  = new BButton(saveCmd);

        // properties
        BGridPane pPane = new BGridPane(2);

        pPane.setColumnGap(10);
        pPane.setRowGap(6);
        pPane.setHalign(BHalign.left);
        normalTextFE.loadValue(BString.make(ext.getToNormalText().format(ext)), BFacets.make(BFacets.MULTI_LINE, true));
        offnormalTextFE.loadValue(BString.make(ext.getToOffnormalText().format(ext)), BFacets.make(BFacets.MULTI_LINE, true));
        faultTextFE.loadValue(BString.make(ext.getToFaultText().format(ext)), BFacets.make(BFacets.MULTI_LINE, true));

        // alarmTransitionsFE.loadValue(ext.getAlarmEnable(),
        // BFacets.make(BFacets.make("showNormal", false), BFacets.make("showAlert", false)));
        alarmInhibitFE.loadValue(ext.getAlarmInhibit());

        // inhibitTimeFE.loadValue(ext.getInhibitTime(), BFacets.make(BFacets.SHOW_MILLISECONDS, false));
        alarmDelayFE.loadValue(ext.getTimeDelay(), BFacets.make(BFacets.SHOW_MILLISECONDS, false));

        // delayToNormalFE.loadValue(ext.getTimeDelayToNormal(), BFacets.make(BFacets.SHOW_MILLISECONDS, false));
        pPane.add("lbl1", makeLabel("normaltext", "Normal Text"));
        pPane.add("edt1", normalTextFE);
        pPane.add("lbl2", makeLabel("offnormaltext", "Offnormal Text"));
        pPane.add("edt2", offnormalTextFE);
        pPane.add("lbl3", makeLabel("faulttext", "Fault Text"));
        pPane.add("edt3", faultTextFE);

        // pPane.add("lbl4", makeLabel("alarmenable", "Alarm Enable"));
        // pPane.add("edt4", alarmTransitionsFE);
        pPane.add("lbl5", makeLabel("alarminhibit", "Alarm Inhibit"));
        pPane.add("edt5", alarmInhibitFE);

        // pPane.add("lbl6", makeLabel("inhibittime", "Inhibit Time"));
        // pPane.add("edt6", inhibitTimeFE);
        pPane.add("lbl7", makeLabel("alarmdelay", "Alarm Delay"));
        pPane.add("edt7", alarmDelayFE);

        // pPane.add("lbl8", makeLabel("delaytonormal", "Delay to Normal"));
        // pPane.add("edt8", delayToNormalFE);
        pPane.add("lbl9", makeLabel("alarmclass", "Alarm Class"));
        pPane.add("edt9", alarmClassFE);
        pPane.add("lbl10", makeLabel("instructions", "Instructions"));
        pPane.add("edt10", instructionsFE);

        // buttons
        BGridPane bPane = new BGridPane(2);

        bPane.add("save", saveBtn);
        bPane.add("close", closeBtn);

        // put it together
        BEdgePane ePane = new BEdgePane();

        ePane.setCenter(new BBorderPane(pPane));
        ePane.setBottom(new BBorderPane(bPane));

        BScrollPane sPane = new BScrollPane();

        sPane.setContent(ePane);
        setContent(sPane);
    }

    private BLabel makeLabel(String key, String defaultLabel) {

        String lexKey = "neo.alarm.manager." + key + ".label";

        return new BLabel(lex.get(lexKey, defaultLabel), boldFont);
    }

    //
    // private void setVisibleProperty(BAlarmSourceExt ext, Property prop, boolean hide) {
    //
    // if (hide) {
    // ext.setFlags(prop, Flags.HIDDEN | ext.getFlags(prop));
    // } else {
    // ext.setFlags(prop, ext.getFlags(prop) & ~Flags.HIDDEN);
    // }
    // }
    //////////////////////////////////////////////////////////////////////////
    // Commands
    //////////////////////////////////////////////////////////////////////////
    class CloseCmd extends Command {

        public CommandArtifact doInvoke() {

            close();

            return null;
        }

        CloseCmd(BWidget bwidget) {
            super(bwidget, UiLexicon.bajaui, "commands.close");
        }
    }


    class SaveCmd extends Command {

        public CommandArtifact doInvoke() throws CannotSaveException, Exception {

            // save values back to extension
            BString strVal = null;

            strVal = (BString) offnormalTextFE.saveValue();
            alarmExtension.setToOffnormalText(BFormat.make(strVal.getString()));
            strVal = (BString) normalTextFE.saveValue();
            alarmExtension.setToNormalText(BFormat.make(strVal.getString()));
            strVal = (BString) faultTextFE.saveValue();
            alarmExtension.setToFaultText(BFormat.make(strVal.getString()));

            // alarmExtension.setAlarmEnable((BAlarmTransitionBits) alarmTransitionsFE.saveValue());
            alarmInhibitFE.saveValue(alarmExtension.getAlarmInhibit(), null);

            // alarmExtension.setInhibitTime((BRelTime) inhibitTimeFE.saveValue());
            alarmExtension.setTimeDelay((BRelTime) alarmDelayFE.saveValue());

            // alarmExtension.setTimeDelayToNormal((BRelTime) delayToNormalFE.saveValue());
            close();

            return null;
        }

        SaveCmd(BWidget bwidget) {
            super(bwidget, UiLexicon.bajaui, "commands.save");
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Fields
    //////////////////////////////////////////////////////////////////////////
    CloseCmd                    closeCmd;
    SaveCmd                     saveCmd;
    BAlarmSourceExt             alarmExtension;
    BFont                       boldFont = BFont.make("Tahoma", 11, BFont.BOLD);
    public static final Lexicon lex      = Lexicon.make("neoalarm");

    // properties
    BStringFE normalTextFE    = new BStringFE();
    BStringFE offnormalTextFE = new BStringFE();
    BStringFE faultTextFE     = new BStringFE();

    // BAlarmTransitionBitsFE alarmTransitionsFE = new BAlarmTransitionBitsFE();
    BStatusValueFE alarmInhibitFE = new BStatusValueFE();

    // BRelTimeFE             inhibitTimeFE      = new BRelTimeFE();
    BRelTimeFE alarmDelayFE = new BRelTimeFE();

    // BRelTimeFE             delayToNormalFE    = new BRelTimeFE();
}
