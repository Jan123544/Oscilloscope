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

ADC_callback osci_timer1_update_callback;
ADC_callback osci_timer2_update_callback;
ADC_callback osci_timer3_update_callback;

void OSCI_timer_stop(TIM_TypeDef*);
void OSCI_timer_start(TIM_TypeDef*);
void OSCI_timer_set_update_callback(TIM_TypeDef*, ADC_callback);

#endif /* INC_OSCI_TIMER_H_ */
