package jp.kshoji.blehid;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;

/**
 * @author shinilms
 */

public final class KbMousePeripheral extends HidPeripheral {

    private static final String TAG = KbMousePeripheral.class.getSimpleName();

    private static final byte MOUSE_REPORT_ID = 0x01;
    private static final byte KB_REPORT_ID = 0x02;

    private static final int REPORT_ID_KEY_INDEX = 0;

    private static final int BUTTON_KEY_INDEX = 1;
    private static final int X_AXIS_KEY_INDEX = 2;
    private static final int Y_AXIS_KEY_INDEX = 3;
    private static final int WHEEL_KEY_INDEX = 4;

    private static final int MODIFIER_KEY_INDEX = 1;
    private static final int PACKET_KEY_INDEX = 3;

    /**
     * mouse+keyboard Characteristic Data(Report Map)
     */
    private static final byte[] REPORT_MAP = {
            USAGE_PAGE(1),      0x01,         // Generic Desktop
            USAGE(1),           0x02,         // Mouse
            COLLECTION(1),      0x01,         // Application
            REPORT_ID(1),  MOUSE_REPORT_ID,        //   REPORT_ID (1)
            USAGE(1),           0x01,         //  Pointer
            COLLECTION(1),      0x00,         //  Physical
            USAGE_PAGE(1),      0x09,         //   Buttons
            USAGE_MINIMUM(1),   0x01,
            USAGE_MAXIMUM(1),   0x03,
            LOGICAL_MINIMUM(1), 0x00,
            LOGICAL_MAXIMUM(1), 0x01,
            REPORT_COUNT(1),    0x03,         //   3 bits (Buttons)
            REPORT_SIZE(1),     0x01,
            INPUT(1),           0x02,         //   Data, Variable, Absolute
            REPORT_COUNT(1),    0x01,         //   5 bits (Padding)
            REPORT_SIZE(1),     0x05,
            INPUT(1),           0x01,         //   Constant
            USAGE_PAGE(1),      0x01,         //   Generic Desktop
            USAGE(1),           0x30,         //   X
            USAGE(1),           0x31,         //   Y
            USAGE(1),           0x38,         //   Wheel
            LOGICAL_MINIMUM(1), (byte) 0x81,  //   -127
            LOGICAL_MAXIMUM(1), 0x7f,         //   127
            REPORT_SIZE(1),          0x08,         //   Three bytes
            REPORT_COUNT(1),    0x03,
            INPUT(1),           0x06,         //   Data, Variable, Relative
            END_COLLECTION(0),
            END_COLLECTION(0),

            USAGE_PAGE(1),      0x01,       // Generic Desktop Ctrls
            USAGE(1),           0x06,       // Keyboard
                REPORT_ID(1), KB_REPORT_ID,     //   REPORT_ID (2)
            COLLECTION(1),      0x01,       // Application
            USAGE_PAGE(1),      0x07,       //   Kbrd/Keypad
            USAGE_MINIMUM(1), (byte) 0xE0,
            USAGE_MAXIMUM(1), (byte) 0xE7,
            LOGICAL_MINIMUM(1), 0x00,
            LOGICAL_MAXIMUM(1), 0x01,
            REPORT_SIZE(1),          0x01,       //   1 byte (Modifier)
            REPORT_COUNT(1),    0x08,
            INPUT(1),           0x02,       //   Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position
            REPORT_COUNT(1),    0x01,       //   1 byte (Reserved)
            REPORT_SIZE(1),     0x08,
            INPUT(1),           0x01,       //   Const,Array,Abs,No Wrap,Linear,Preferred State,No Null Position
            REPORT_COUNT(1),    0x05,       //   5 bits (Num lock, Caps lock, Scroll lock, Compose, Kana)
            REPORT_SIZE(1),     0x01,
            USAGE_PAGE(1),      0x08,       //   LEDs
            USAGE_MINIMUM(1),   0x01,       //   Num Lock
            USAGE_MAXIMUM(1),   0x05,       //   Kana
            OUTPUT(1),          0x02,       //   Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile
            REPORT_COUNT(1),    0x01,       //   3 bits (Padding)
            REPORT_SIZE(1),     0x03,
            OUTPUT(1),          0x01,       //   Const,Array,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile
            REPORT_COUNT(1),    0x06,       //   6 bytes (Keys)
            REPORT_SIZE(1),     0x08,
            LOGICAL_MINIMUM(1), 0x00,
            LOGICAL_MAXIMUM(1), 0x65,       //   101 keys
            USAGE_PAGE(1),      0x07,       //   Kbrd/Keypad
            USAGE_MINIMUM(1),   0x00,
            USAGE_MAXIMUM(1),   0x65,
            INPUT(1),           0x00,       //   Data,Array,Abs,No Wrap,Linear,Preferred State,No Null Position
            END_COLLECTION(0),
    };

    /**
     * Constructor<br />
     * Before constructing the instance, check the Bluetooth availability.
     *
     * @param context the applicationContext
     */
    public KbMousePeripheral(final Context context) throws UnsupportedOperationException {
        super(context.getApplicationContext(), true, true, false, 10);
    }

    private final byte[] lastSent = new byte[5];
    private final byte[] bytes = {5, 1, 9, 2, -95, 1, 5, 1, 9, 2, -95, 2, -123, 4, 9, 1, -95, 0, 5, 9, 25, 1, 41, 2, 21, 0, 37, 1, 117, 1, -107, 2, -127, 2, -107, 1, 117, 6, -127, 3, 5, 1, 9, 48, 9, 49, 22, 1, -8, 38, -1, 7, 117, 16, -107, 2, -127, 6, -95, 2, -123, 6, 9, 72, 21, 0, 37, 1, 53, 1, 69, 4, 117, 2, -107, 1, -79, 2, -123, 4, 9, 56, 21, -127, 37, 127, 53, 0, 69, 0, 117, 8, -107, 1, -127, 6, -64, -95, 2, -123, 6, 9, 72, 21, 0, 37, 1, 53, 1, 69, 4, 117, 2, -107, 1, -79, 2, 53, 0, 69, 0, 117, 4, -79, 3, -123, 4, 5, 12, 10, 56, 2, 21, -127, 37, 127, 117, 8, -107, 1, -127, 6, -64, -64, -64, -64, 5, 1, 9, 6, -95, 1, -123, 8, 5, 7, 25, -32, 41, -25, 21, 0, 37, 1, 117, 1, -107, 8, -127, 2, -107, 1, 117, 8, -127, 1, -107, 1, 117, 8, 21, 0, 37, 101, 5, 7, 25, 0, 41, 101, -127, 0, -64};

    @Override
    protected byte[] getReportMap() {
        Log.i(TAG, "onOutputReport data: " + Arrays.toString(bytes));
        return REPORT_MAP;
    }

    private byte[] newReportGet(int features){
        // Report Map - for basic mode
        final byte REPORT_MAP_BASIC[] = {
                (byte) 0x05, (byte) 0x0C, /*        Usage Page (Consumer Devices)       */
                (byte) 0x09, (byte) 0x01, /*        Usage (Consumer Control)            */
                (byte) 0xA1, (byte) 0x01, /*        Collection (Application)            */
                (byte) 0x85, (byte) 0x02, /*        Report ID=2                         */
                (byte) 0x05, (byte) 0x0C, /*        Usage Page (Consumer Devices)       */
                (byte) 0x15, (byte) 0x00, /*        Logical Minimum (0)                 */
                (byte) 0x25, (byte) 0x01, /*        Logical Maximum (1)                 */
                (byte) 0x75, (byte) 0x01, /*        Report Size (1)                     */
                (byte) 0x95, (byte) 0x0B, /*        Report Count (11)                   */

                (byte) 0x09, (byte) 0x6F, /* 1       Usage (Bright Up)                  */
                (byte) 0x09, (byte) 0x70, /* 2       Usage (Bright Down)                */

                (byte) 0x09, (byte) 0xB5, /* 3       Usage (Scan Next Track)            */
                (byte) 0x09, (byte) 0xB6, /* 4       Usage (Scan Previous Track)        */
                (byte) 0x09, (byte) 0xB7, /* 5       Usage (Stop)                       */
                (byte) 0x09, (byte) 0xCD, /* 6       Usage (Play / Pause)               */

                (byte) 0x09, (byte) 0xE2, /* 7       Usage (Mute)                       */
                (byte) 0x09, (byte) 0xE9, /* 8       Usage (Volume Up)                  */
                (byte) 0x09, (byte) 0xEA, /* 9       Usage (Volume Down)                */

                (byte) 0x09, (byte) 0xB8, /* 10      Usage (Eject)                      */
                (byte) 0x09, (byte) 0xb8, /* 11      Usage (Snapshot)                   */

                (byte) 0x81, (byte) 0x02, /*        Input (Data, Variable, Absolute)    */
                (byte) 0x95, (byte) 0x05, /*        Report Count (5)                    */
                (byte) 0x81, (byte) 0x01, /*        Input (Constant)                    */
                (byte) 0xC0,
        };

        // Report Map - concatenation of few arrays
        final byte REPORT_MAP_START[] = {
                (byte) 0x05, (byte) 0x0C, /*        Usage Page (Consumer Devices)       */
                (byte) 0x09, (byte) 0x01, /*        Usage (Consumer Control)            */
                (byte) 0xA1, (byte) 0x01, /*        Collection (Application)            */
                (byte) 0x85, (byte) 0x02, /*        Report ID=2                         */
        };
        final byte REPORT_MAP_CONSUMER[] =
                ((features & ReportField.REP_CONSUMER) == ReportField.REP_CONSUMER) ? new byte[]{
                        /*========================== Consumer control ==========================*/
                        (byte) 0xA1, (byte) 0x02, /*        Collection (Logical)            */
                        (byte) 0x05, (byte) 0x0C, /*        Usage Page (Consumer Devices)       */
                        (byte) 0x15, (byte) 0x00, /*        Logical Minimum (0)                 */
                        (byte) 0x25, (byte) 0x01, /*        Logical Maximum (1)                 */
                        (byte) 0x75, (byte) 0x01, /*        Report Size (1)                     */
                        (byte) 0x95, (byte) 0x10, /*        Report Count (16)                   */

                        (byte) 0x09, (byte) 0x6F, /* 1       Usage (Bright Up)                  */
                        (byte) 0x09, (byte) 0x70, /* 2       Usage (Bright Down)                */

                        (byte) 0x09, (byte) 0xB5, /* 3       Usage (Scan Next Track)            */
                        (byte) 0x09, (byte) 0xB6, /* 4       Usage (Scan Previous Track)        */
                        (byte) 0x09, (byte) 0xB7, /* 5       Usage (Stop)                       */
                        (byte) 0x09, (byte) 0xCD, /* 6       Usage (Play / Pause)               */

                        (byte) 0x09, (byte) 0xE2, /* 7       Usage (Mute)                       */
                        (byte) 0x09, (byte) 0xE9, /* 8       Usage (Volume Up)                  */
                        (byte) 0x09, (byte) 0xEA, /* 9       Usage (Volume Down)                */

                        (byte) 0x09, (byte) 0xB8, /* 10      Usage (Eject)                      */
                        (byte) 0x09, (byte) 0x65, /* 11      Usage (Snapshot)                   */

                        (byte) 0x05, (byte) 0x01, /*        Usage Page (Generic Desktop)        */
                        (byte) 0x09, (byte) 0x82, /* 12      Usage (System Sleep)               */
                        (byte) 0x09, (byte) 0xA8, /* 13      Usage (System Hibernate)           */
                        (byte) 0x09, (byte) 0x81, /* 14      Usage (System Power Down)          */
                        (byte) 0x09, (byte) 0x8E, /* 15      Usage (System Cold Restart)        */
                        (byte) 0x09, (byte) 0x8F, /* 16      Usage (System Warm Restart)        */

                        (byte) 0x81, (byte) 0x02, /*        Input (Data, Variable, Absolute)    */
//                (byte) 0x95, (byte) 0x00, /*        Report Count (1)                    */
//                (byte) 0x81, (byte) 0x01, /*        Input (Constant)                    */

                        /*==================== Application Launcher Buttons ====================*/
                        (byte) 0x05, (byte) 0x0C,              /* Usage Page (Consumer Devices) */
                        (byte) 0x95, (byte) 0x01,              /* Report Count (1)              */
                        (byte) 0x75, (byte) 0x10,              /* Report Size (16)              */
                        (byte) 0x16, (byte) 0x81, (byte) 0x01, /* Logical Minimum (385)         */
                        (byte) 0x26, (byte) 0xC7, (byte) 0x01, /* Logical Maximum (455)         */
                        (byte) 0x05, (byte) 0x0C,              /* Usage Page (Consumer Devices) */
                        (byte) 0x1a, (byte) 0x81, (byte) 0x01, /* Usage Minimum (385)           */
                        (byte) 0x2a, (byte) 0xC7, (byte) 0x01, /* Usage Maximum (455)           */
                        (byte) 0x81, (byte) 0x00,              /* Input (Data, Array)           */

                        /*==================== Application Control Buttons =====================*/
                        (byte) 0x05, (byte) 0x0C,              /* Usage Page (Consumer Devices) */
                        (byte) 0x95, (byte) 0x01,              /* Report Count (1)              */
                        (byte) 0x75, (byte) 0x10,              /* Report Size (16)              */
                        (byte) 0x16, (byte) 0x01, (byte) 0x02, /* Logical Minimum (385)         */
                        (byte) 0x26, (byte) 0x9C, (byte) 0x02, /* Logical Maximum (455)         */
                        (byte) 0x05, (byte) 0x0C,              /* Usage Page (Consumer Devices) */
                        (byte) 0x1a, (byte) 0x01, (byte) 0x02, /* Usage Minimum (385)           */
                        (byte) 0x2a, (byte) 0x9C, (byte) 0x02, /* Usage Maximum (455)           */
                        (byte) 0x81, (byte) 0x00,              /* Input (Data, Array)           */
                        (byte) 0xC0,              /*       End Collection                       */
                } : new byte[]{};
        final byte REPORT_MAP_KEYBOARD[] =
                ((features & ReportField.REP_KEYBOARD) == ReportField.REP_KEYBOARD) ? new byte[]{
                        /*============================== Keyboard ==============================*/
                        (byte) 0x05, (byte) 0x07, /*        Usage Page (Keyboard/Keypad)        */
                        (byte) 0x15, (byte) 0x00, /*        Logical Minimum (0)                 */
                        (byte) 0x25, (byte) 0x01, /*        Logical Maximum (1)                 */
                        (byte) 0x75, (byte) 0x01, /*        Report Size (1)                     */
                        (byte) 0x95, (byte) 0x08, /*        Report Count (8)                    */

                        (byte) 0x09, (byte) 0xE0, /* 1       Usage (LeftControl)                */
                        (byte) 0x09, (byte) 0xE1, /* 2       Usage (LeftShift)                  */
                        (byte) 0x09, (byte) 0xE2, /* 3       Usage (LeftAlt)                    */
                        (byte) 0x09, (byte) 0xE3, /* 4       Usage (LeftGUI)                    */
                        (byte) 0x09, (byte) 0xE4, /* 5       Usage (RightControl)               */
                        (byte) 0x09, (byte) 0xE5, /* 6       Usage (RightShift)                 */
                        (byte) 0x09, (byte) 0xE6, /* 7       Usage (RightAlt)                   */
                        (byte) 0x09, (byte) 0xE7, /* 8       Usage (RightGUI)                   */
                        (byte) 0x81, (byte) 0x02, /*        Input (Data, Variable, Absolute)    */

                        (byte) 0x05, (byte) 0x07, /*        Usage Page (Keyboard/Keypad)        */
                        (byte) 0x95, (byte) 0x01, /*        Report Count (1)                    */
                        (byte) 0x75, (byte) 0x08, /*        Report Size (8)                     */
                        (byte) 0x15, (byte) 0x04, /*        Logical Minimum (4)                 */
                        (byte) 0x25, (byte) 0xDF, /*        Logical Maximum (223)               */
                        (byte) 0x05, (byte) 0x07, /*        Usage Page (Key codes)              */
                        (byte) 0x19, (byte) 0x04, /*        Usage Minimum (4)                   */
                        (byte) 0x29, (byte) 0xDF, /*        Usage Maximum (223)                 */
                        (byte) 0x81, (byte) 0x00, /*        Input (Data, Array)                 */
                } : new byte[]{};
        final byte REPORT_MAP_MOUSE[] =
                ((features & ReportField.REP_MOUSE) == ReportField.REP_MOUSE) ? new byte[]{
                        /*================================ Mouse ===============================*/
                        (byte) 0x05, (byte) 0x01, /*        Usage Page (Generic Desktop)        */
                        (byte) 0x09, (byte) 0x02, /*        Usage (Mouse)                       */
                        (byte) 0xa1, (byte) 0x01, /*         Collection (Application)            */
                        (byte) 0x85, (byte) 0x02,
                        (byte) 0x09, (byte) 0x01, /*        Usage (Consumer Control)            */
                        (byte) 0xa1, (byte) 0x00, /*        Collection (Physical)               */
                        (byte) 0x05, (byte) 0x09, /*        Usage Page (Button)                 */
                        (byte) 0x19, (byte) 0x01, /*        Usage Minimum (1)                   */
                        (byte) 0x29, (byte) 0x05, /*        Usage Maximum (5)                   */
                        (byte) 0x15, (byte) 0x00, /*        Logical Minimum (0)                 */
                        (byte) 0x25, (byte) 0x01, /*        Logical Maximum (1)                 */
                        (byte) 0x95, (byte) 0x05, /*        Report Count (5)                    */
                        (byte) 0x75, (byte) 0x01, /*        Report Size (1)                     */
                        (byte) 0x81, (byte) 0x02, /*        Input (Variable, Absolute)          */

                        (byte) 0x95, (byte) 0x01, /*        Report Count (1)                    */
                        (byte) 0x75, (byte) 0x03, /*        Report Size (3)                     */
                        (byte) 0x81, (byte) 0x01, /*        Input (Constant)                    */

                        (byte) 0x05, (byte) 0x01, /*        Usage Page (Generic Desktop)        */
                        (byte) 0x09, (byte) 0x30, /*        Usage (X)                           */
                        (byte) 0x09, (byte) 0x31, /*        Usage (Y)                           */
                        (byte) 0x15, (byte) 0x81, /*        Logical Minimum (-127)              */
                        (byte) 0x25, (byte) 0x7f, /*        Logical Maximum (127)               */
                        (byte) 0x75, (byte) 0x08, /*        Report Size (8)                     */
                        (byte) 0x95, (byte) 0x02, /*        Report Count (2)                    */
                        (byte) 0x81, (byte) 0x06, /*        Input (Variable, Relative)          */

                        (byte) 0x09, (byte) 0x38, /*        Usage (Wheel)                       */
                        (byte) 0x15, (byte) 0x81, /*        Logical Minimum (-127)              */
                        (byte) 0x25, (byte) 0x7f, /*        Logical Maximum (127)               */
                        (byte) 0x75, (byte) 0x08, /*        Report Size (8)                     */
                        (byte) 0x95, (byte) 0x01, /*        Report Count (1)                    */
                        (byte) 0x81, (byte) 0x06, /*        Input (Variable, Relative)          */
                        (byte) 0xC0,              /*       End Collection                       */
                        (byte) 0xC0,              /*       End Collection                       */
                } : new byte[]{};
        final byte REPORT_MAP_END[] = {
                (byte) 0xC0,              /*       End Collection                       */
        };

        int position = 0;
        byte REPORT_MAP[];


        if ((features & ReportField.REP_BASIC) == ReportField.REP_BASIC) {
            REPORT_MAP = REPORT_MAP_BASIC;
        } else {
            REPORT_MAP = new byte[REPORT_MAP_START.length + REPORT_MAP_CONSUMER.length +
                    REPORT_MAP_KEYBOARD.length + REPORT_MAP_MOUSE.length + REPORT_MAP_END.length];

            System.arraycopy(REPORT_MAP_START, 0, REPORT_MAP, position, REPORT_MAP_START.length);
            position += REPORT_MAP_START.length;
            System.arraycopy(REPORT_MAP_CONSUMER, 0, REPORT_MAP, position, REPORT_MAP_CONSUMER.length);
            position += REPORT_MAP_CONSUMER.length;
            System.arraycopy(REPORT_MAP_KEYBOARD, 0, REPORT_MAP, position, REPORT_MAP_KEYBOARD.length);
            position += REPORT_MAP_KEYBOARD.length;
            System.arraycopy(REPORT_MAP_MOUSE, 0, REPORT_MAP, position, REPORT_MAP_MOUSE.length);
            position += REPORT_MAP_MOUSE.length;
            System.arraycopy(REPORT_MAP_END, 0, REPORT_MAP, position, REPORT_MAP_END.length);
        }
        return REPORT_MAP;
    }

    enum ReportField {
        /* Consumer */
        REPORT_FIELD_CONSUMER_CONTROL(0, 2),
        REPORT_FIELD_LAUNCHER_BUTTON(2, 2),
        REPORT_FIELD_CONTROL_BUTTON(4, 2),
        /* Keyboard */
        REPORT_FIELD_KEYBOARD_META_KEYS(6, 1),
        REPORT_FIELD_KEYBOARD_KEYS(7, 1),
        REPORT_FIELD_KEYBOARD_ALL(6, 2),    // REPORT_FIELD_KEYBOARD_META_KEYS + REPORT_FIELD_KEYBOARD_KEYS
        /* Mouse */
        REPORT_FIELD_MOUSE_BUTTONS(8, 1),
        REPORT_FIELD_MOUSE_X(9, 1),
        REPORT_FIELD_MOUSE_Y(10, 1),
        REPORT_FIELD_MOUSE_BUTTONS_XY(8, 3),
        REPORT_FIELD_MOUSE_XY(9, 2),
        REPORT_FIELD_MOUSE_SCROLL(11, 1);

        final static public int REP_CONSUMER = 0x01;
        final static public int REP_MOUSE = 0x02;
        final static public int REP_KEYBOARD = 0x04;
        final static public int REP_BASIC = 0x08;
        public final int byte_size;
        public int byte_offset;

        ReportField(int byte_offset, int byte_size) {
            this.byte_offset = byte_offset;
            this.byte_size = byte_size;
        }

        static void updateValues(int features) {
            /* Restore original value */
            REPORT_FIELD_CONSUMER_CONTROL.byte_offset = 0;
            REPORT_FIELD_LAUNCHER_BUTTON.byte_offset = 2;
            REPORT_FIELD_CONTROL_BUTTON.byte_offset = 4;

            REPORT_FIELD_KEYBOARD_META_KEYS.byte_offset = 6;
            REPORT_FIELD_KEYBOARD_KEYS.byte_offset = 7;
            REPORT_FIELD_KEYBOARD_ALL.byte_offset = 6;

            REPORT_FIELD_MOUSE_BUTTONS.byte_offset = 8;
            REPORT_FIELD_MOUSE_X.byte_offset = 9;
            REPORT_FIELD_MOUSE_Y.byte_offset = 10;
            REPORT_FIELD_MOUSE_BUTTONS_XY.byte_offset = 8;
            REPORT_FIELD_MOUSE_XY.byte_offset = 9;
            REPORT_FIELD_MOUSE_SCROLL.byte_offset = 11;

            if ((features & REP_CONSUMER) == 0) {
                REPORT_FIELD_CONSUMER_CONTROL.byte_offset = -1;
                REPORT_FIELD_LAUNCHER_BUTTON.byte_offset = -1;
                REPORT_FIELD_CONTROL_BUTTON.byte_offset = -1;

                REPORT_FIELD_KEYBOARD_META_KEYS.byte_offset -= 6;
                REPORT_FIELD_KEYBOARD_KEYS.byte_offset -= 6;
                REPORT_FIELD_KEYBOARD_ALL.byte_offset -= 6;

                REPORT_FIELD_MOUSE_BUTTONS.byte_offset -= 6;
                REPORT_FIELD_MOUSE_X.byte_offset -= 6;
                REPORT_FIELD_MOUSE_Y.byte_offset -= 6;
                REPORT_FIELD_MOUSE_BUTTONS_XY.byte_offset -= 6;
                REPORT_FIELD_MOUSE_XY.byte_offset -= 6;
                REPORT_FIELD_MOUSE_SCROLL.byte_offset -= 6;
            } else if ((features & REP_BASIC) == REP_BASIC) {
                REPORT_FIELD_LAUNCHER_BUTTON.byte_offset = -1;
                REPORT_FIELD_CONTROL_BUTTON.byte_offset = -1;

                REPORT_FIELD_KEYBOARD_META_KEYS.byte_offset -= 4;
                REPORT_FIELD_KEYBOARD_KEYS.byte_offset -= 4;
                REPORT_FIELD_KEYBOARD_ALL.byte_offset -= 4;

                REPORT_FIELD_MOUSE_BUTTONS.byte_offset -= 4;
                REPORT_FIELD_MOUSE_X.byte_offset -= 4;
                REPORT_FIELD_MOUSE_Y.byte_offset -= 4;
                REPORT_FIELD_MOUSE_BUTTONS_XY.byte_offset -= 4;
                REPORT_FIELD_MOUSE_XY.byte_offset -= 4;
                REPORT_FIELD_MOUSE_SCROLL.byte_offset -= 4;
            }

            if ((features & REP_KEYBOARD) == 0) {
                REPORT_FIELD_KEYBOARD_META_KEYS.byte_offset = -1;
                REPORT_FIELD_KEYBOARD_KEYS.byte_offset = -1;
                REPORT_FIELD_KEYBOARD_ALL.byte_offset = -1;

                REPORT_FIELD_MOUSE_BUTTONS.byte_offset -= 2;
                REPORT_FIELD_MOUSE_X.byte_offset -= 2;
                REPORT_FIELD_MOUSE_Y.byte_offset -= 2;
                REPORT_FIELD_MOUSE_BUTTONS_XY.byte_offset -= 2;
                REPORT_FIELD_MOUSE_XY.byte_offset -= 2;
                REPORT_FIELD_MOUSE_SCROLL.byte_offset -= 2;
            }

            if ((features & REP_MOUSE) == 0) {
                REPORT_FIELD_MOUSE_BUTTONS.byte_offset = -1;
                REPORT_FIELD_MOUSE_X.byte_offset = -1;
                REPORT_FIELD_MOUSE_Y.byte_offset = -1;
                REPORT_FIELD_MOUSE_BUTTONS_XY.byte_offset = -1;
                REPORT_FIELD_MOUSE_XY.byte_offset = -1;
                REPORT_FIELD_MOUSE_SCROLL.byte_offset = -1;
            }
        }
    }

    @Override
    protected void onOutputReport(byte[] outputReport) {
        Log.i(TAG, "onOutputReport data: " + Arrays.toString(outputReport));
    }

    /**
     * Move the mouse pointer (float parameters)
     *
     * @param dx delta X (-127 .. +127)
     * @param dy delta Y (-127 .. +127)
     * @param wheel wheel (-127 .. +127)
     * @param leftButton true : button down
     * @param rightButton true : button down
     * @param middleButton true : button down
     */
    public void movePointer(int dx, int dy, int wheel, final boolean leftButton, final boolean rightButton,
                            final boolean middleButton) {
        if (dx > 127) dx = 127;
        if (dx < -127) dx = -127;
        if (dy > 127) dy = 127;
        if (dy < -127) dy = -127;
        if (wheel > 127) wheel = 127;
        if (wheel < -127) wheel = -127;
        byte button = 0;
        if (leftButton) {
            button |= 1;
        }
        if (rightButton) {
            button |= 2;
        }
        if (middleButton) {
            button |= 4;
        }

        final byte[] report = new byte[5];
        report[REPORT_ID_KEY_INDEX] = MOUSE_REPORT_ID;
        report[BUTTON_KEY_INDEX] = (byte) (button & 7);
        report[X_AXIS_KEY_INDEX] = (byte) dx;
        report[Y_AXIS_KEY_INDEX] = (byte) dy;
        report[WHEEL_KEY_INDEX] = (byte) wheel;

        if (lastSent[0] == 0 && lastSent[1] == 0 && lastSent[2] == 0 && lastSent[3] == 0 && lastSent[4] == 0 &&
                report[0] == 0 && report[1] == 0 && report[2] == 0 && report[3] == 0&& report[4] == 0) {
            return;
        }
        lastSent[0] = report[0];
        lastSent[1] = report[1];
        lastSent[2] = report[2];
        lastSent[3] = report[3];
        lastSent[4] = report[4];
        addInputReport(report);
    }


    /**
     * keyboard
     */
    public static final int MODIFIER_KEY_NONE = 0;
    public static final int MODIFIER_KEY_CTRL = 1;
    public static final int MODIFIER_KEY_SHIFT = 2;
    public static final int MODIFIER_KEY_ALT = 4;

    public static final int KEY_F1 = 0x3a;
    public static final int KEY_F2 = 0x3b;
    public static final int KEY_F3 = 0x3c;
    public static final int KEY_F4 = 0x3d;
    public static final int KEY_F5 = 0x3e;
    public static final int KEY_F6 = 0x3f;
    public static final int KEY_F7 = 0x40;
    public static final int KEY_F8 = 0x41;
    public static final int KEY_F9 = 0x42;
    public static final int KEY_F10 = 0x43;
    public static final int KEY_F11 = 0x44;
    public static final int KEY_F12 = 0x45;

    public static final int KEY_PRINT_SCREEN = 0x46;
    public static final int KEY_SCROLL_LOCK = 0x47;
    public static final int KEY_CAPS_LOCK = 0x39;
    public static final int KEY_NUM_LOCK = 0x53;
    public static final int KEY_INSERT = 0x49;
    public static final int KEY_HOME = 0x4a;
    public static final int KEY_PAGE_UP = 0x4b;
    public static final int KEY_PAGE_DOWN = 0x4e;

    public static final int KEY_RIGHT_ARROW = 0x4f;
    public static final int KEY_LEFT_ARROW = 0x50;
    public static final int KEY_DOWN_ARROW = 0x51;
    public static final int KEY_UP_ARROW = 0x52;

    /**
     * Modifier code for US Keyboard
     *
     * @param aChar String contains one character
     * @return modifier code
     */
    public static byte modifier(final String aChar) {
        switch (aChar) {
            case "A":
            case "B":
            case "C":
            case "D":
            case "E":
            case "F":
            case "G":
            case "H":
            case "I":
            case "J":
            case "K":
            case "L":
            case "M":
            case "N":
            case "O":
            case "P":
            case "Q":
            case "R":
            case "S":
            case "T":
            case "U":
            case "V":
            case "W":
            case "X":
            case "Y":
            case "Z":
            case "!":
            case "@":
            case "#":
            case "$":
            case "%":
            case "^":
            case "&":
            case "*":
            case "(":
            case ")":
            case "_":
            case "+":
            case "{":
            case "}":
            case "|":
            case ":":
            case "\"":
            case "~":
            case "<":
            case ">":
            case "?":
                return MODIFIER_KEY_SHIFT;
            default:
                return MODIFIER_KEY_NONE;
        }
    }

    /**
     * Key code for US Keyboard
     *
     * @param aChar String contains one character
     * @return keyCode
     */
    public static byte keyCode(final String aChar) {
        switch (aChar) {
            case "A":
            case "a":
                return 0x04;
            case "B":
            case "b":
                return 0x05;
            case "C":
            case "c":
                return 0x06;
            case "D":
            case "d":
                return 0x07;
            case "E":
            case "e":
                return 0x08;
            case "F":
            case "f":
                return 0x09;
            case "G":
            case "g":
                return 0x0a;
            case "H":
            case "h":
                return 0x0b;
            case "I":
            case "i":
                return 0x0c;
            case "J":
            case "j":
                return 0x0d;
            case "K":
            case "k":
                return 0x0e;
            case "L":
            case "l":
                return 0x0f;
            case "M":
            case "m":
                return 0x10;
            case "N":
            case "n":
                return 0x11;
            case "O":
            case "o":
                return 0x12;
            case "P":
            case "p":
                return 0x13;
            case "Q":
            case "q":
                return 0x14;
            case "R":
            case "r":
                return 0x15;
            case "S":
            case "s":
                return 0x16;
            case "T":
            case "t":
                return 0x17;
            case "U":
            case "u":
                return 0x18;
            case "V":
            case "v":
                return 0x19;
            case "W":
            case "w":
                return 0x1a;
            case "X":
            case "x":
                return 0x1b;
            case "Y":
            case "y":
                return 0x1c;
            case "Z":
            case "z":
                return 0x1d;
            case "!":
            case "1":
                return 0x1e;
            case "@":
            case "2":
                return 0x1f;
            case "#":
            case "3":
                return 0x20;
            case "$":
            case "4":
                return 0x21;
            case "%":
            case "5":
                return 0x22;
            case "^":
            case "6":
                return 0x23;
            case "&":
            case "7":
                return 0x24;
            case "*":
            case "8":
                return 0x25;
            case "(":
            case "9":
                return 0x26;
            case ")":
            case "0":
                return 0x27;
            case "\n": // LF
                return 0x28;
            case "\b": // BS
                return 0x2a;
            case "\t": // TAB
                return 0x2b;
            case " ":
                return 0x2c;
            case "_":
            case "-":
                return 0x2d;
            case "+":
            case "=":
                return 0x2e;
            case "{":
            case "[":
                return 0x2f;
            case "}":
            case "]":
                return 0x30;
            case "|":
            case "\\":
                return 0x31;
            case ":":
            case ";":
                return 0x33;
            case "\"":
            case "'":
                return 0x34;
            case "~":
            case "`":
                return 0x35;
            case "<":
            case ",":
                return 0x36;
            case ">":
            case ".":
                return 0x37;
            case "?":
            case "/":
                return 0x38;
            default:
                return 0;
        }
    }

    /**
     * Send text to Central device
     * @param text the text to send
     */
    public void sendKeys(final String text) {
        String lastKey = null;
        for (int i = 0; i < text.length(); i++) {
            final String key = text.substring(i, i + 1);
            final byte[] report = new byte[9];
            report[REPORT_ID_KEY_INDEX] = KB_REPORT_ID;
            report[MODIFIER_KEY_INDEX] = modifier(key);
            report[PACKET_KEY_INDEX] = keyCode(key);

            if (key.equals(lastKey))
                sendKeyUp();

            addInputReport(report);
            lastKey = key;
        }
        sendKeyUp();
    }

    /**
     * Send Key Down Event
     * @param modifier modifier key
     * @param keyCode key code
     */
    public void sendKeyDown(final byte modifier, final byte keyCode) {
        final byte[] report = new byte[9];
        report[REPORT_ID_KEY_INDEX] = KB_REPORT_ID;
        report[MODIFIER_KEY_INDEX] = modifier;
        report[PACKET_KEY_INDEX] = keyCode;

        addInputReport(report);
    }

    private static final byte[] EMPTY_REPORT = new byte[9];

    /**
     * Send Key Up Event
     */
    public void sendKeyUp() {
        EMPTY_REPORT[REPORT_ID_KEY_INDEX] = KB_REPORT_ID;
        addInputReport(EMPTY_REPORT);
    }
}
