/*
 * osci_settings.c
 *
 *  Created on: Dec 22, 2019
 *      Author: dot
 */
#include "osci_defines.h"
#include "osci_data_structures.h"
#include "osci_configurator.h"

void fill_ranges(Osci_Settings* settings, Osci_CalculatedParameters* new_parameters){
	switch(settings->xVoltageRange){
		case 5:
			new_parameters->divider_max_volts_x = OSCI_MEASUREMENT_MAX_X_WITH_RANGE_5;
			new_parameters->x_active_range = 5.0f;
			break;
		case 10:
			new_parameters->divider_max_volts_x = OSCI_MEASUREMENT_MAX_X_WITH_RANGE_10;
			new_parameters->x_active_range = 10.0f;
			break;
		case 20:
			new_parameters->divider_max_volts_x = OSCI_MEASUREMENT_MAX_X_WITH_RANGE_20;
			new_parameters->x_active_range = 20.0f;
			break;
	}
	switch(settings->yVoltageRange){
		case 5:
			new_parameters->divider_max_volts_y = OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_5;
			new_parameters->y_active_range = 5.0f;
			break;
		case 10:
			new_parameters->divider_max_volts_y = OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_10;
			new_parameters->y_active_range = 10.0f;
			break;
		case 20:
			new_parameters->divider_max_volts_y = OSCI_MEASUREMENT_MAX_Y_WITH_RANGE_20;
			new_parameters->y_active_range = 20.0f;
			break;
	}
}
void fill_sensitivity_and_offset(Osci_Settings* settings, Osci_CalculatedParameters* new_parameters){
	new_parameters->levels_per_volt_x = OSCI_MEASUREMENT_MAX_LEVELS/new_parameters->x_active_range;
	new_parameters->divider_bypass_scaling_x = new_parameters->x_active_range/new_parameters->divider_max_volts_x;
	new_parameters->sensitivity_conversion_x = OSCI_MEASUREMENT_MAX_LEVELS/(settings->xSensitivity*settings->xGraticuleDivisions*new_parameters->levels_per_volt_x);
	new_parameters->offset_conversion_x = new_parameters->sensitivity_conversion_x*new_parameters->levels_per_volt_x;

	new_parameters->levels_per_volt_y = OSCI_MEASUREMENT_MAX_LEVELS/new_parameters->y_active_range;
	new_parameters->divider_bypass_scaling_y = new_parameters->y_active_range/new_parameters->divider_max_volts_y;
	new_parameters->sensitivity_conversion_y = OSCI_MEASUREMENT_MAX_LEVELS/(settings->ySensitivity*settings->yGraticuleDivisions*new_parameters->levels_per_volt_y);
	new_parameters->offset_conversion_y = new_parameters->sensitivity_conversion_y*new_parameters->levels_per_volt_y;
}
void fill_thresholds(Osci_Settings* settings, Osci_CalculatedParameters* new_parameters){
	new_parameters->xThresholdInLevels = floor(new_parameters->levels_per_volt_x*settings->xtriggerLevel);
	new_parameters->yThresholdInLevels = floor(new_parameters->levels_per_volt_y*settings->ytriggerLevel);
}

void fill_times(Osci_Settings* settings, Osci_CalculatedParameters* new_parameters){
	// Assumes 32MHZ timer clock
	new_parameters->xTimerSettings.arr = floor(32000000*settings->xTimePerDivision*settings->xGraticuleDivisions/(NUM_SAMPLES -1));
	new_parameters->yTimerSettings.arr = floor(32000000*settings->yTimePerDivision*settings->yGraticuleDivisions/(NUM_SAMPLES -1));

	new_parameters->xTimerSettings.psc = 0;
	new_parameters->yTimerSettings.psc = 0;

	//Adjust prescaler so that arr < 2^16
	if(new_parameters->xTimerSettings.arr > 65535){
		new_parameters->xTimerSettings.psc = floor(new_parameters->xTimerSettings.arr/65536);
		new_parameters->xTimerSettings.arr = new_parameters->xTimerSettings.arr%65536;
	}

	if(new_parameters->yTimerSettings.arr > 65535){
		new_parameters->yTimerSettings.psc = floor(new_parameters->yTimerSettings.arr/65536);
		new_parameters->yTimerSettings.arr = new_parameters->yTimerSettings.arr%65536;
	}
}

void switch_relays(Osci_Settings* s, Osci_CalculatedParameters* p){
	// TODO
}
void wait_for_relays_to_switch(){
	// TODO
}


void osci_configurator_recalculate_parameters(Osci_Settings* s, Osci_CalculatedParameters* p){
	Osci_CalculatedParameters new_p = {0};
	fill_ranges(s, &new_p);
	fill_sensitivity_and_offset(s, &new_p);
	fill_thresholds(s, &new_p);
	fill_times(s, &new_p);
	*p = new_p;
}

void osci_configurator_switch_relays(Osci_Settings* s, Osci_CalculatedParameters* p){
	switch_relays(s, p);
	wait_for_relays_to_switch();
}

void fill_default_settings(Osci_Settings* osci_settings){
	osci_settings->xtriggerLevel = OSCI_SETTINGS_DEFAULT_XTRIGGERLEVEL;
	osci_settings->ytriggerLevel = OSCI_SETTINGS_DEFAULT_YTRIGGERLEVEL;
	osci_settings->xOffset = OSCI_SETTINGS_DEFAULT_XOFFSET;
	osci_settings->yOffset = OSCI_SETTINGS_DEFAULT_YOFFSET;
	osci_settings->ySensitivity = OSCI_SETTINGS_DEFAULT_YSENSITIVITY;
	osci_settings->xSensitivity = OSCI_SETTINGS_DEFAULT_XSENSITIVITY;
	osci_settings->xTimePerDivision = OSCI_SETTINGS_DEFAULT_TIMEPERDIVISION;
	osci_settings->yTimePerDivision = OSCI_SETTINGS_DEFAULT_TIMEPERDIVISION;
	osci_settings->triggerType = OSCI_SETTINGS_DEFAULT_TRIGGERTYPE;
	osci_settings->xVoltageRange = OSCI_SETTINGS_DEFAULT_XVOLTAGERANGE;
	osci_settings->yVoltageRange = OSCI_SETTINGS_DEFAULT_YVOLTAGERANGE;
	osci_settings->xGraticuleDivisions = OSCI_SETTINGS_DEFAULT_XGRATICULEDIVISIONS;
	osci_settings->yGraticuleDivisions = OSCI_SETTINGS_DEFAULT_YGRATICULEDIVISIONS;
}

void osci_configurator_config_defaults_ts(Osci_Transceiver* ts){
	fill_default_settings(&ts->receiveCompleteBuffer);
	fill_default_settings(&ts->receiveCompleteBuffer);
	osci_configurator_recalculate_parameters(&ts->receiveCompleteBuffer, &ts->allReceivedParameters);
}

void osci_configurator_distribute_settings(Osci_ChannelStateMachine*xcsm, Osci_ChannelStateMachine*ycsm, Osci_Settings* s, Osci_CalculatedParameters* p){
	xcsm->params.graticuleDivisions = s->xGraticuleDivisions;
	xcsm->params.offset = p->offset_conversion_x*s->xOffset;
	xcsm->params.sensitivity = p->sensitivity_conversion_x;
	xcsm->params.timerSettings = p->xTimerSettings;
	xcsm->params.triggerLevel = p->xThresholdInLevels;
	xcsm->params.voltageRange = s->xVoltageRange;

	ycsm->params.graticuleDivisions = s->yGraticuleDivisions;
	ycsm->params.offset = p->offset_conversion_y*s->yOffset;
	ycsm->params.sensitivity = p->sensitivity_conversion_y;
	ycsm->params.timerSettings = p->yTimerSettings;
	ycsm->params.triggerLevel = p->yThresholdInLevels;
	ycsm->params.voltageRange = s->yVoltageRange;
}
