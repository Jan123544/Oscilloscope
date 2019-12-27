/*
 * osci_callback.c
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#include "osci_callback.h"
#include "osci_adc.h"
#include "osci_channel_state_machine.h"


void osci_callback_set_monitoring_callbacks(Osci_ChannelStateMachine* csm){
	osci_timer_set_update_callback(csm, csm->monitoring_callback);
	osci_adc_set_awd_callback(csm);
}

void osci_callback_set_measuring_callbacks(Osci_ChannelStateMachine* csm){
	osci_timer_set_update_callback(csm, csm->measuring_callback);
	osci_dma_set_TC_callback(csm);
}

void noop(void){
	return;
}
