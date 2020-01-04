/*
 * osci_timers.c
 *
 *  Created on: Dec 23, 2019
 *      Author: dot
 */

#include <osci_timer.h>
#include "main.h"

void OSCI_timer_stop(Osci_ChannelStateMachine* csm)
{
	LL_TIM_DisableCounter(csm->timer);
}

void OSCI_timer_start(Osci_ChannelStateMachine* csm)
{
	LL_TIM_EnableUpdateEvent(csm->timer);
	LL_TIM_EnableIT_UPDATE(csm->timer);

	LL_TIM_SetCounter(csm->timer, 0);
	LL_TIM_EnableCounter(csm->timer);
}

void OSCI_timer_set_update_callback(Osci_ChannelStateMachine* csm, ADC_callback new_callback)
{
	if(csm->timer == TIM1)
	{
		osci_timer1_update_callback = new_callback;
	}
	else
	{
		if(csm->timer == TIM2)
			osci_timer2_update_callback = new_callback;
	}
}
