/*
 * Copyright (c) 2007, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 * $Id: rtimer-arch.h,v 1.6 2009/12/09 12:55:35 adamdunkels Exp $
 */

/**
 * \file
 *         Header file for the MSP430-specific rtimer code
 * \author
 *         Adam Dunkels <adam@sics.se>
 */

#ifndef __RTIMER_ARCH_H__
#define __RTIMER_ARCH_H__

#include <io.h>
#include "sys/rtimer.h"


#if GLOSSY
#define RTIMER_ARCH_SECOND (32768U)
#define rtimer_arch_now() (TAR)
#define rtimer_arch_now_dco() (TBR)
#else
#define RTIMER_ARCH_SECOND (4096U)
#define rtimer_arch_now() (TBR)
#endif /* GLOSSY */

#if WITH_FTSP
extern rtimer_long_clock_t long_time();
#define RTIMER_ARCH_SECOND_LONG (rtimer_long_clock_t)(4096LU)
#define rtimer_arch_now_long() long_time()
#endif /* WITH_FTSP */

#endif /* __RTIMER_ARCH_H__ */
