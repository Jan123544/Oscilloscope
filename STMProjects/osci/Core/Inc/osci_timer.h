/*
 * osci_timers.h
 *
 *  Created on: Dec 23, 2019
 *      Author: dot
 */

#ifndef INC_OSCI_TIMER_H_
#define INC_OSCI_TIMER_H_
#include "main.h"
#include "osci_data_structures.h"

#define TIM1_ID 1
#define TIM2_ID 2

void OSCI_timer_init(Osci_ChannelStateMachine*);
void OSCI_timer_stop(Osci_ChannelStateMachine*);
void OSCI_timer_start(Osci_ChannelStateMachine*);
void OSCI_timer_reconfigure_for_measuring(Osci_ChannelStateMachine*);
void OSCI_timer_reconfigure_for_monitoring(Osci_ChannelStateMachine*);

ADC_callback osci_timer1_update_callback;
ADC_callback osci_timer2_update_callback;

void OSCI_timer_set_update_callback(Osci_ChannelStateMachine*, ADC_callback);

#endif /* INC_OSCI_TIMER_H_ */
