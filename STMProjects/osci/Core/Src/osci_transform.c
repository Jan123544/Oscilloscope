/*
 * osci_transform.c
 *
 *  Created on: 3. 12. 2019
 *      Author: dot
 */

#include"osci_transform.h"

void OSCI_transform_apply(Osci_DataFrame* df, Osci_ChannelParameters px, Osci_ChannelParameters py)
{
	for(uint32_t i = 0; i < NUM_SAMPLES;i++)
	{
		df->xChannel.values[i] = MIN(MAX(floor(1/px.sensitivity*df->xChannel.values[i]*px.alpha + px.offset), DATA_MIN_VALUE), DATA_MAX_VALUE);
		df->yChannel.values[i] = MIN(MAX(floor(1/py.sensitivity*df->yChannel.values[i]*py.alpha + py.offset), DATA_MIN_VALUE), DATA_MAX_VALUE);
	}
}

