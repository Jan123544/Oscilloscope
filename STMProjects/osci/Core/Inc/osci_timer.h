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

void osci_timer_init(Osci_ChannelStateMachine*);
void osci_timer_stop(Osci_ChannelStateMachine*);
void osci_timer_start(Osci_ChannelStateMachine*);
void osci_timer_reconfigure_for_measuring(Osci_ChannelStateMachine*);
void osci_timer_reconfigure_for_monitoring(Osci_ChannelStateMachine*);

void (*osci_timer1_update_callback) (Osci_Application*);
void (*osci_timer2_update_callback) (Osci_Application*);

void osci_timer_set_update_callback(Osci_ChannelStateMachine*, void (*) (Osci_Application*));

#endif /* INC_OSCI_TIMER_H_ */
