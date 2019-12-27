/*
 * osci_timers.c
 *
 *  Created on: Dec 23, 2019
 *      Author: dot
 */

#include <osci_timer.h>
#include "main.h"



void osci_timer_stop(Osci_ChannelStateMachine* csm){
	//LL_TIM_DisableUpdateEvent(csm->timer);
	LL_TIM_DisableCounter(csm->timer);
}

void osci_timer_start(Osci_ChannelStateMachine* csm){
	//LL_TIM_EnableUpdateEvent(csm->timer);
	LL_TIM_SetCounter(csm->timer, 0);
	LL_TIM_EnableCounter(csm->timer);
	//LL_TIM_EnableIT_UPDATE(csm->timer);
}

void osci_timer_reconfigure_for_measuring(Osci_ChannelStateMachine* csm){
	// TODO
	csm->timer->PSC = csm->params.timerSettings.psc;
	csm->timer->ARR = csm->params.timerSettings.arr;
}

void osci_timer_reconfigure_for_monitoring(Osci_ChannelStateMachine* csm){
	csm->timer->PSC = 63; // 500KHZ at 32MHZ CLK
	csm->timer->ARR = 19; // 25KHZ
}

void osci_timer_init(Osci_ChannelStateMachine* csm){
	// This must be called only after handles of update interrupt are initialized in osci_channel_init(), otherwise it generates an interrupt with null callback functions, which results in hard_fault
	LL_TIM_EnableUpdateEvent(csm->timer);
	LL_TIM_EnableIT_UPDATE(csm->timer);
}

void osci_timer_set_update_callback(Osci_ChannelStateMachine* csm, void (*new_callback) (Osci_Application*)){
	if(csm->timer == TIM1){
		osci_timer1_update_callback = new_callback;
	}else {
		if(csm->timer == TIM2){
			osci_timer2_update_callback = new_callback;
		}
	}
}
