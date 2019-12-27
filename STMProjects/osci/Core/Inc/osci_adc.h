/*
 * osci_adcs.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_ADC_H_
#define INC_OSCI_ADC_H_
#include "osci.h"
#include "osci_channel_state_machine.h"

void osci_adc_init(Osci_ChannelStateMachine*);
void osci_adc_stop(Osci_ChannelStateMachine*);
void osci_adc_reconfigure_for_monitoring(Osci_ChannelStateMachine*);
void osci_adc_reconfigure_for_measuring(Osci_ChannelStateMachine*);

void (*osci_adc_awd1_callback) (Osci_Application*);
void (*osci_adc_awd2_callback) (Osci_Application*);

void osci_adc_set_awd_callback(Osci_ChannelStateMachine*);

#endif /* INC_OSCI_ADC_H_ */
