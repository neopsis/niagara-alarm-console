/*
 * @(#)BNeoAlarmConsole.java   17.03.2014
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.niagara.module.neoalarm;

import com.tridium.alarm.ui.BAlarmClassFE;
import com.tridium.alarm.ui.BAlarmConsole;
import com.tridium.alarm.ui.BInstructionsFE;

import javax.baja.alarm.BAlarmInstructions;
import javax.baja.alarm.BAlarmRecord;
import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.naming.BOrd;
import javax.baja.naming.BOrdList;
import javax.baja.naming.UnresolvedException;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.event.BKeyEvent;
import javax.baja.ui.event.BMouseEvent;
import javax.baja.ui.pane.BGridPane;
import javax.baja.ui.table.TableSelection;
import javax.baja.util.Lexicon;

/**
 * Neopsis enhanced alarm console. User can modify basic alarm
 * extension properties directly from console. Button 'Alarm
 * Properties' opens new edit dialog
 *
 */
@NiagaraType(agent = @AgentOn(types = "neoalarm:NeoConsoleRecipient", requiredPermissions = "r"))
public class BNeoAlarmConsole extends BAlarmConsole {

    /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
    /*@ $com.neopsis.niagara.module.neoalarm.BNeoAlarmConsole(1658512980)1.0$ @*/
    /* Generated Tue Aug 25 13:52:17 CEST 2020 by Slot-o-Matic (c) Tridium, Inc. 2012 */

    ////////////////////////////////////////////////////////////////
    // Type
    ////////////////////////////////////////////////////////////////
    @Override
    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BNeoAlarmConsole.class);

    /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
     * Default constructor
     *
     * @throws Exception
     */
    public BNeoAlarmConsole() throws Exception {}

    /**
     * Add edit button to the button bar
     */
    public BGridPane makeToolBar() {

        editCommand = new Command(this, lex.get("neo.alarm.console.alarmproperties.label", "Alarm properties")) {

            public CommandArtifact doInvoke() {

                BObject obj = getAlarmSource();

                if (!(obj instanceof BAlarmSourceExt)) {
                    return null;
                }

                BAlarmSourceExt ext = (BAlarmSourceExt) obj;

                editAlarmProperties(ext);

                return null;
            }
        };

        BGridPane buttonPane = super.makeToolBar();

        // remove unused
        buttonPane.remove("hyp");
        buttonPane.remove("sil");
        buttonPane.remove("filter");
        buttonPane.remove("showVideo");

        BButton editBtn = new BButton(editCommand, true, false);

        buttonPane.setColumnCount(buttonPane.getColumnCount() + 1);
        buttonPane.add("alprops", editBtn);
        editCommand.setEnabled(false);

        return buttonPane;
    }

    /**
     * Override controller
     */
    protected Controller getController() {
        return new NeoController();
    }

    /**
     * Return alarm source. Currently only local BAlarmSourceExt is object of interest
     *
     * @return Alarm source object
     */
    public BObject getAlarmSource() {

        int            k   = 0;
        TableSelection sel = sourceTable.getSelection();

        if (sel.getRowCount() > 1) {
            return null;
        }

        k = sel.getRow();

        if (k != -1) {

            BAlarmRecord rec     = sourceModel.getRecord(k);
            BOrdList     ordList = rec.getSource();
            int          size    = ordList.size();

            if (size > 1) {
                return null;
            }

            BOrd ord = ordList.get(0);

            // ord = BOrd.make("fox:|" + ord.relativizeToHost().toString(null));
            // System.out.println("Source ord2 = " + ord.toString(null));
            BObject obj = null;

            try {
                obj = ord.relativizeToHost().get(recipient);
            } catch (UnresolvedException e) {

                // maybe alarm source does not exist anymore
                return null;
            }

            if (!(obj instanceof BAlarmSourceExt)) {

                // we are interested only in BAlarmSourceExt
                return null;
            }

            BAlarmSourceExt ext = (BAlarmSourceExt) obj;

            ext.loadSlots();
            ext.lease();

            return ext;
        }

        return null;
    }

    public BNeoConsoleRecipient getRecipient() {
        return (BNeoConsoleRecipient) recipient;
    }

    //////////////////////////////////////////////////////////////////////////
    // Editor Command
    //////////////////////////////////////////////////////////////////////////

    /**
     * Opens edit dialog for given BAlarmSourceExt
     *
     * @param ext alarm source extension
     */
    protected void editAlarmProperties(BAlarmSourceExt ext) {

        BAlarmClassFE   alarmClassFE   = new BAlarmClassFE();
        BInstructionsFE instructionsFE = new BInstructionsFE();

        add("temp", alarmClassFE);
        alarmClassFE.loadValue(BString.make(ext.getAlarmClass()), null);
        remove("temp");
        add("temp", instructionsFE);
        instructionsFE.loadValue(ext.getAlarmInstructions(), null);
        remove("temp");

        BNeoAlarmPropertyEditor dialog = new BNeoAlarmPropertyEditor(this, ext, alarmClassFE, instructionsFE);

        dialog.setBoundsCenteredOnOwner();
        dialog.open();

        // update alarm class
        try {

            BString strVal = (BString) alarmClassFE.saveValue();

            ext.setAlarmClass(strVal.toString());

        } catch (Exception e) {}

        // update instructions
        try {
            ext.setAlarmInstructions((BAlarmInstructions) instructionsFE.saveValue());
        } catch (Exception e) {}
    }

    //////////////////////////////////////////////////////////////////////////
    // Controller
    //////////////////////////////////////////////////////////////////////////

    /**
     * Controller for Neopsis enhanced console. Controller enables/disables
     * 'Alarm Properties' button dynamically
     *
     */
    protected class NeoController extends BAlarmConsole.Controller {

        public void keyPressed(BKeyEvent event) {

            super.keyPressed(event);
            enableEditor();
        }

        public void mouseReleased(BMouseEvent bmouseevent) {

            super.mouseReleased(bmouseevent);
            enableEditor();
        }

        private void enableEditor() {

            BObject obj = getAlarmSource();

            if ((getRecipientForAlarm(null) == null)
                    || getRecipientForAlarm(null).getPermissions(ctx).hasAdminInvoke()
                    || getRecipientForAlarm(null).getPermissions(ctx).hasAdminWrite()) {

                if (obj instanceof BAlarmSourceExt) {
                    editCommand.setEnabled(true);
                } else {
                    editCommand.setEnabled(false);
                }

            } else {
                editCommand.setEnabled(false);
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Fields
    //////////////////////////////////////////////////////////////////////////
    static Lexicon    lex = Lexicon.make("neoalarm");
    protected Command editCommand;
}
