/*
 * osci_settings.h
 *
 *  Created on: Dec 22, 2019
 *      Author: dot
 */

#ifndef INC_OSCI_CONFIGURATOR_H_
#define INC_OSCI_CONFIGURATOR_H_

#include <math.h>
#include "osci_defines.h"
#include "osci_data_structures.h"

void osci_configurator_config_defaults_ts(Osci_Transceiver*);
void osci_configurator_switch_relays(Osci_Settings*, Osci_CalculatedParameters*);
void osci_configurator_recalculate_parameters(Osci_Settings*, Osci_CalculatedParameters*);
void osci_configurator_distribute_settings(Osci_ChannelStateMachine*, Osci_ChannelStateMachine*, Osci_Settings*, Osci_CalculatedParameters*);
#endif /* INC_OSCI_CONFIGURATOR_H_ */
