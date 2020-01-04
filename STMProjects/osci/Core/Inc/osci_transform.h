/*
 * transform.h
 *
 *  Created on: 3. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_TRANSFORM_H_
#define INC_OSCI_TRANSFORM_H_


#include "main.h"
#include "osci.h"
#include "osci_channel_state_machine.h"

//void osci_transform_apply_transforms();
void OSCI_transform_apply(Osci_DataFrame* df, Osci_ChannelParameters params);

#endif /* INC_OSCI_TRANSFORM_H_ */
