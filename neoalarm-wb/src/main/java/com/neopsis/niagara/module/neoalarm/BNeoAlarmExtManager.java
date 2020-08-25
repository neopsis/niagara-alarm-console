/*
 * @(#)BNeoAlarmExtManager.java   17.03.2014
 *
 * Copyright (c) 2007 Neopsis GmbH
 *
 *
 */



package com.neopsis.niagara.module.neoalarm;

import com.tridium.alarm.ui.BAlarmClassFE;
import com.tridium.alarm.ui.BInstructionsFE;

import javax.baja.alarm.BAlarmInstructions;
import javax.baja.alarm.BAlarmService;
import javax.baja.alarm.BAlarmTransitionBits;
import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.collection.BITable;
import javax.baja.collection.Column;
import javax.baja.collection.TableCursor;
import javax.baja.control.BControlPoint;
import javax.baja.gx.BImage;
import javax.baja.gx.BInsets;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.Array;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.event.BMouseEvent;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.pane.BGridPane;
import javax.baja.ui.table.*;
import javax.baja.util.Lexicon;
import javax.baja.workbench.CannotSaveException;
import javax.baja.workbench.view.BWbComponentView;

import java.util.Comparator;

/**
 * Improved version of AlarmExtManager
 *
 */
@NiagaraType(agent = @AgentOn(types = "alarm:AlarmService", requiredPermissions = "r"))
public class BNeoAlarmExtManager extends BWbComponentView {

    
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.neopsis.niagara.module.neoalarm.BNeoAlarmExtManager(2131604556)1.0$ @*/
/* Generated Tue Aug 25 13:52:17 CEST 2020 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNeoAlarmExtManager.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
     * Constructor creates layout and instantiates model and controller. We are using
     * DynamicTableModel wrapper to allow column hiding
     */
    public BNeoAlarmExtManager() {

        lexSlotPath           = lex.get("neo.alarm.manager.slotpath.label", "Slot Path");
        lexPointName          = lex.get("neo.alarm.manager.pointname.label", "Point Name");
        lexAlarmSourceName    = lex.get("neo.alarm.manager.alarmsource.label", "Alarm Source");
        lexPointDisplayName   = lex.get("neo.alarm.manager.pointdisplayname.label", "Display Name");
        lexAlarmClass         = lex.get("neo.alarm.manager.alarmclass.label", "Alarm Class");
        lexOffnormalEnabled   = lex.get("neo.alarm.manager.offnormalenabled.label", "Offnormal Enabled");
        lexFaultEnabled       = lex.get("neo.alarm.manager.faultenabled.label", "Fault Enabled");
        lexAlarmOffNormalText = lex.get("neo.alarm.manager.offnormaltext.label", "Offnormal text");
        lexAlarmNormalText    = lex.get("neo.alarm.manager.normaltext.label", "Normal Text");
        lexAlarmFaultText     = lex.get("neo.alarm.manager.faulttext.label", "Fault Text");
        lexAlarmInhibit       = lex.get("neo.alarm.manager.alarminhibit.label", "Alarm Inhibit");
        lexInhibitTime        = lex.get("neo.alarm.manager.inhibittime.label", "Inhibit Time");
        lexTimeDelay          = lex.get("neo.alarm.manager.alarmdelay.label", "Alarm Delay");
        lexTimeDelayToNormal  = lex.get("neo.alarm.manager.delaytonormal.label", "Delay to Normal");
        points                = new Array(String.class);

        // buttons
        BButton editButton = new BButton(addExtCommand = new EditAlarmCommand(this, "Edit"), true, true);

        buttonPane = new BGridPane(1);
        buttonPane.setColumnAlign(BHalign.center);
        buttonPane.setUniformColumnWidth(false);
        buttonPane.add("edit", editButton);

        // table - DynamicTableModel allows column hiding
        model = new DynamicTableModel(new AlarmMgrModel(), visibleColumns);
        table = new BTable(model);
        table.setController(new AlarmMgrController());
        table.setCellRenderer(new TableCellRenderer());
        table.setSelection(new TableSelection());
        tablePane = new BBorderPane(table, BBorder.inset, BInsets.DEFAULT);

        // put everything together
        mainPane = new BEdgePane();
        mainPane.setCenter(tablePane);
        mainPane.setBottom(buttonPane);
        setContent(mainPane);
    }

    /**
     * Load value into UI. Value should be of type BAlarmService
     */
    public void doLoadValue(BObject value, Context context) {

        if (!(value instanceof BAlarmService)) {
            return;
        }

        alarmService = (BAlarmService) value;
        alarmService.loadSlots();
        alarmService.lease();

        /*
        N4

        // Iterate a BITable using a TableCursor
        //
        BITable    table = (BITable)bqlOrd.resolve().get();
        Column[] columns = table.getColumns().list();
        try(TableCursor<BIObject> cursor = table.cursor()) {
            // Just for printing purposes, not for random access.
            int row = 0;
            while (cursor.next()) {
                 System.out.print(row + ": ");
                 for (Column col : columns) {
                     System.out.print(cursor.cell(col) + ", ");
                 }
                 System.out.println();
            }
            ++row;
         }


         */
        String   bql            = "slot:/|bql:select slotPath from alarm:AlarmSourceExt";
        BITable  result         = (BITable) BOrd.make(bql).resolve(alarmService).get();
        Column[] columns        = result.getColumns().list();
        Column   slotPathColumn = columns[0];

        try (TableCursor<BIObject> cursor = result.cursor()) {

            points.clear();

            while (cursor.next()) {

                cursor.cell(columns[0]);

                String          slotPath = cursor.cell(slotPathColumn).toString(null);
                BAlarmSourceExt ext      = getAlarmSourceExt(slotPath);

                if (ext != null) {

                    BAlarmTransitionBits trans = ext.getAlarmEnable();

                    if (trans.isToOffnormal() || trans.isToFault()) {
                        points.add(cursor.cell(slotPathColumn).toString(null));
                    }
                }
            }
        }

        // ColumnList  columns        = result.getColumns();
        // Column      slotPathColumn = columns.get(0);
        // TableCursor cursor         = (TableCursor) result.cursor();
        //
        // points.clear();
        //
        // while (cursor.next()) {
        //
        // String          slotPath = cursor.get(slotPathColumn).toString(null);
        // BAlarmSourceExt ext      = getAlarmSourceExt(slotPath);
        //
        // if (ext != null) {
        //
        // BAlarmTransitionBits trans = ext.getAlarmEnable();
        //
        // if (trans.isToOffnormal() || trans.isToFault()) {
        // points.add(cursor.get(slotPathColumn).toString(null));
        // }
        // }
        // }
    }

    public BObject doSaveValue(BObject value, Context cx) throws CannotSaveException, Exception {
        return super.doSaveValue(value, cx);
    }

    //////////////////////////////////////////////////////////////////////////
    // Model
    //////////////////////////////////////////////////////////////////////////

    /**
     * Model for Alarm Manager
     *
     */
    class AlarmMgrModel extends TableModel {

        /**
         * Default constructor
         */
        public AlarmMgrModel() {
            super();
        }

        /**
         * Return number of rows as size of model array
         */
        public int getRowCount() {
            return points.size();
        }

        /**
         * Returns number of table columns
         */
        public int getColumnCount() {
            return 15;
        }

        /**
         * Returns row image from control point
         */
        public BImage getRowIcon(int row) {

            BControlPoint point = getControlPoint((String) points.get(row));

            if (point == null) {
                return null;
            }

            return BImage.make(point.getIcon());
        }

        /**
         * Initial columns are sortable, all other not
         */
        public boolean isColumnSortable(int i) {

            boolean isSortable = false;

            switch (i) {

            case 2 :
            case 3 :
            case 4 :
            case 7 :
            case 8 :
            case 10 :
            case 12 :
                isSortable = true;

                break;

            default :
                isSortable = false;

                break;
            }

            return isSortable;
        }

        /**
         * Execute sort operation on one column
         */
        public void sortByColumn(int col, boolean ascending) {

            if (points.size() == 0) {
                return;
            }

            AlarmMgrComparator comparator = new AlarmMgrComparator(col);

            points = points.sort(comparator);

            if (!ascending) {
                points = points.reverse();
            }
        }

        /**
         * Returns column header titles
         */
        public String getColumnName(int col) {

            if (col == 0) {
                return lexSlotPath;
            }

            if (col == 1) {
                return lexPointName;
            }

            if (col == 2) {
                return lexPointDisplayName;
            }

            if (col == 3) {
                return lexAlarmSourceName;
            }

            if (col == 4) {
                return lexAlarmClass;
            }

            if (col == 5) {
                return lexOffnormalEnabled;
            }

            if (col == 6) {
                return lexFaultEnabled;
            }

            if (col == 7) {
                return lexAlarmOffNormalText;
            }

            if (col == 8) {
                return lexAlarmNormalText;
            }

            if (col == 9) {
                return lexAlarmFaultText;
            }

            if (col == 10) {
                return lexAlarmInhibit;
            }

            if (col == 11) {
                return lexInhibitTime;
            }

            if (col == 12) {
                return lexTimeDelay;
            }

            if (col == 13) {
                return lexTimeDelayToNormal;
            }

            return "";
        }

        /**
         * Returns cell values at row/column coordinates
         */
        public Object getValueAt(int row, int col) {

            String          path = (String) points.get(row);
            BAlarmSourceExt ext  = getAlarmSourceExt(path);

            if (ext == null) {
                return null;
            }

            BControlPoint point = (BControlPoint) ext.getParent();

            if (point == null) {
                return null;
            }

            point.loadSlots();
            point.lease();

            if (col == 0) {
                return point.getSlotPath().toDisplayString();
            }

            if (col == 1) {
                return SlotPath.unescape(point.getName());
            }

            if (col == 2) {
                return SlotPath.unescape(point.getDisplayName(null));
            }

            if (col == 3) {
                return SlotPath.unescape(ext.getSourceName().format(ext));
            }

            if (col == 4) {
                return ext.getAlarmClass();
            }

            if (col == 5) {
                return BBoolean.make(ext.getAlarmEnable().isToOffnormal());
            }

            if (col == 6) {
                return BBoolean.make(ext.getAlarmEnable().isToFault());
            }

            if (col == 7) {
                return ext.getToOffnormalText();
            }

            if (col == 8) {
                return ext.getToNormalText();
            }

            if (col == 9) {
                return ext.getToFaultText();
            }

            if (col == 10) {
                return ext.getAlarmInhibit();
            }

            if (col == 11) {
                return ext.getInhibitTime();
            }

            if (col == 12) {
                return ext.getTimeDelay();
            }

            if (col == 13) {
                return ext.getTimeDelayToNormal();
            }

            return null;
        }

        /**
         * Returns column values for comparator
         *
         */
        public Object getValueAtColumn(String slotPath, int col) {

            if (slotPath == null) {
                return null;
            }

            BAlarmSourceExt ext = getAlarmSourceExt(slotPath);

            if (ext == null) {
                return null;
            }

            if (col == 2) {

                BControlPoint point = (BControlPoint) ext.getParent();

                if (point == null) {
                    return null;
                }

                return BString.make(point.getDisplayName(null));
            }

            if (col == 3) {
                return BString.make(ext.getSourceName().format(ext));
            }

            if (col == 4) {
                return BString.make(ext.getAlarmClass());
            }

            if (col == 7) {
                return BString.make(ext.getToOffnormalText().toString(null));
            }

            if (col == 8) {
                return BString.make(ext.getToNormalText().toString(null));
            }

            if (col == 10) {
                return BString.make(ext.getAlarmInhibit().toString(null));
            }

            if (col == 12) {
                return ext.getTimeDelay();
            }

            return null;
        }
    }


    /**
     * Return alarm source. Currently only local BAlarmSourceExt is object of interest
     *
     * @return Alarm source object
     */
    public BAlarmSourceExt getAlarmSourceAtRow() {

        int            k   = 0;
        TableSelection sel = table.getSelection();

        if (sel.getRowCount() > 1) {
            return null;
        }

        k = sel.getRow();

        if (k != -1) {

            BAlarmSourceExt ext = getAlarmSourceExt((String) points.get(k));

            ext.loadSlots();
            ext.lease();

            return ext;
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////
    // Controller
    //////////////////////////////////////////////////////////////////////////

    /**
     * Controller for Alarm Manager, placeholder for
     * later development
     *
     */
    class AlarmMgrController extends TableController {

        /**
         * Constructor
         *
         */
        AlarmMgrController() {
            super();
        }

        public void mouseReleased(BMouseEvent mouseEvent) {

            super.mouseReleased(mouseEvent);

            // do something with mouse click
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Commands
    //////////////////////////////////////////////////////////////////////////

    /**
     * Edit Alarm Command
     *
     */
    public class EditAlarmCommand extends Command {

        public EditAlarmCommand(BWidget widget, String title) {
            super(widget, title);
        }

        public CommandArtifact doInvoke() {

            BAlarmSourceExt ext = getAlarmSourceAtRow();

            if (ext == null) {
                return null;
            }

            ext.loadSlots();
            editAlarmProperties(ext);

            return null;
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Editor Command
    //////////////////////////////////////////////////////////////////////////
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
    // Helpers
    //////////////////////////////////////////////////////////////////////////
    private BAlarmSourceExt getAlarmSourceExt(String path) {

        if (path == null) {
            return null;
        }

        BAlarmSourceExt ext = (BAlarmSourceExt) BOrd.make(path).get(alarmService);

        ext.loadSlots();
        ext.lease();

        return ext;
    }

    private BControlPoint getControlPoint(String path) {

        BAlarmSourceExt ext = getAlarmSourceExt(path);

        if (ext == null) {
            return null;
        }

        BControlPoint point = (BControlPoint) ext.getParent();

        point.loadSlots();
        point.lease();

        return point;
    }

    //////////////////////////////////////////////////////////////////////////
    // Comparator
    //////////////////////////////////////////////////////////////////////////

    /**
     * Comparator for column sort
     *
     */
    class AlarmMgrComparator implements Comparator {

        /**
         * Constructor saves the column reference
         * @param c
         */
        public AlarmMgrComparator(int c) {
            column = c;
        }

        /**
         * Compare two model array entries for given column
         *
         *  @param  key1  key (slotPath) for first entry
         *  @param  key2  key (slotPath) for second entry
         *  @return comparison result (-1, 0, +1)
         *
         */
        public int compare(Object key1, Object key2) {

            AlarmMgrModel mgrModel = (AlarmMgrModel) ((DynamicTableModel) table.getModel()).getRootModel();
            Object        colVal1  = (mgrModel.getValueAtColumn((String) key1, column));
            Object        colVal2  = (mgrModel.getValueAtColumn((String) key2, column));

            if (colVal1 instanceof BRelTime) {
                return ((BRelTime) colVal1).compareTo(((BRelTime) colVal2));
            } else if (colVal1 instanceof BString) {
                return ((BString) colVal1).compareTo(((BString) colVal2));
            } else {
                return colVal1.toString().toLowerCase().compareTo(colVal2.toString().toLowerCase());
            }
        }

        // local store for compared column
        int column;
    }


    //////////////////////////////////////////////////////////////////////////
    // Fields
    //////////////////////////////////////////////////////////////////////////
    final String                lexSlotPath;
    final String                lexPointName;
    final String                lexPointDisplayName;
    final String                lexAlarmSourceName;
    final String                lexAlarmClass;
    final String                lexAlarmOffNormalText;
    final String                lexAlarmNormalText;
    final String                lexAlarmFaultText;
    final String                lexInhibitTime;
    final String                lexAlarmInhibit;
    final String                lexTimeDelay;
    final String                lexTimeDelayToNormal;
    final String                lexOffnormalEnabled;
    final String                lexFaultEnabled;
    private BEdgePane           mainPane         = null;
    private BGridPane           buttonPane       = null;
    private BBorderPane         tablePane        = null;
    BLabel                      counterLabel     = null;
    BLabel                      titleLabel       = null;
    Command                     addExtCommand    = null;
    Command                     removeExtCommand = null;
    BAlarmService               alarmService     = null;
    private Array               points           = null;
    private BTable              table            = null;
    private DynamicTableModel   model            = null;
    int[]                       visibleColumns   = new int[] {
        2, 3, 4, 7, 8, 10, 12
    };
    public static final Lexicon lex              = Lexicon.make("neoalarm");
}
