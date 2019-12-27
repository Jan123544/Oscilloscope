/*
 * osci_defines.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_DEFINES_H_
#define INC_OSCI_DEFINES_H_

// Number of measured samples
#define NUM_SAMPLES 512

// Communication protocol constants
#define OSCI_DATA_START_WORD 0xEFEF
#define DATA_MAX_VALUE 4096
#define DATA_MIN_VALUE 0

// Should be calibrated with measurement
// These are used in calculations indirectly
#define OSCI_MEASUREMENT_MAX_X_WITH_RANGE_5 5.0f
#define OSCI_MEASUREMENT_MAX_X_WITH_RANGE_10 10.0f
#define OSCI_MEASUREMENT_MAX_X_WITH_RANGE_20 20.0f
#define OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_5 5.0f
#define OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_10 10.0f
#define OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_20 20.0f

// Also should be calibrated
// These are used to determine if it's safe to switch to a lower range, if not, then the switch should not happen.
#define OSCI_MEASUREMENT_20_TO_10_SWITCH_TH 2047
#define OSCI_MEASUREMENT_20_TO_5_SWITCH_TH 1024
#define OSCI_MEASUREMENT_10_TO_5_SWITCH_TH 2047

#define OSCI_MEASUREMENT_MAX_LEVELS 4095

// Default settings
#define OSCI_SETTINGS_DEFAULT_XOFFSET 0
#define OSCI_SETTINGS_DEFAULT_XSENSITIVITY 1
#define OSCI_SETTINGS_DEFAULT_YOFFSET 0
#define OSCI_SETTINGS_DEFAULT_YSENSITIVITY 1
#define OSCI_SETTINGS_DEFAULT_TIMEPERDIVISION 0.1
#define OSCI_SETTINGS_DEFAULT_XTRIGGERLEVEL 0
#define OSCI_SETTINGS_DEFAULT_YTRIGGERLEVEL 0
#define OSCI_SETTINGS_DEFAULT_TRIGGERTYPE 0
#define OSCI_SETTINGS_DEFAULT_XVOLTAGERANGE 20
#define OSCI_SETTINGS_DEFAULT_YVOLTAGERANGE 20
#define OSCI_SETTINGS_DEFAULT_XGRATICULEDIVISIONS 10
#define OSCI_SETTINGS_DEFAULT_YGRATICULEDIVISIONS 10

#define MIN(a,b) (a)<(b)?(a):(b)
#define MAX(a,b) (a)>(b)?(a):(b)
#define TRUE 1
#define FALSE 0

#endif /* INC_OSCI_DEFINES_H_ */
