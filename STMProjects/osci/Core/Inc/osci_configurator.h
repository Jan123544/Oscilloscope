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

void OSCI_configurator_config_defaults_ts(Osci_Transceiver*);
void OSCI_configurator_switch_relays(Osci_Transceiver* ts, Osci_Settings* s);
void OSCI_configurator_recalculate_parameters(Osci_Transceiver* ts, Osci_Settings* s);
void OSCI_configurator_recalculate_parameters_only_transform(Osci_Transceiver* ts, Osci_Settings* s);
void OSCI_configurator_distribute_settings(Osci_Transceiver* ts, Osci_Settings* s);
#endif /* INC_OSCI_CONFIGURATOR_H_ */
